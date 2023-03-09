package com.ke.coding.service.filesystem.fatservice.impl;

import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY;
import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY_POS;
import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.FAT_NC_END_OF_FILE;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.PER_CLUSTER_SECTOR;
import static com.ke.coding.api.enums.Constants.PER_SECTOR_BYTES;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_DATA_ERROR;
import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_LENGTH_TOO_LONG;
import static com.ke.coding.api.enums.ErrorCodeEnum.FILENAME_LENGTH_TOO_LONG;
import static com.ke.coding.api.enums.ErrorCodeEnum.INSUFFICIENT_SPACE;
import static com.ke.coding.api.enums.ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataCluster;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataSector;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.service.filesystem.fatservice.AbstractFileSystem;
import com.ke.risk.safety.common.util.json.JsonUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 11:17
 * @description:
 */
public class Fat16xFileSystemServiceV2 extends AbstractFileSystem {


	Fat16xFileSystem fat16xFileSystem;


	/**
	 * 执行
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	@Override
	public FileSystemActionResult execute(Command command) {
		return null;
	}

	/**
	 * mkdir
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	@Override
	public FileSystemActionResult mkdir(Command command) {
		//step: 文件名，文件后缀长度限制
		String newDir = command.getParams().get(0);
		if (newDir.length() > 8) {
			return FileSystemActionResult.fail(DIR_LENGTH_TOO_LONG);
		}
		//step: 当前路径是根目录，需要判断根目录区域空间是否充足
		String currentPath = command.getCurrentPath();
		if (ROOT_PATH.equals(currentPath) && !hasIdleRootDirectorySpace()) {
			return FileSystemActionResult.fail(INSUFFICIENT_SPACE);
		}
		//step: 目录数据需要写入cluster，判断剩余cluster空间是否充足
		if (fat16xFileSystem.getIdleClusterSize() <= 0) {
			return FileSystemActionResult.fail(INSUFFICIENT_SPACE);
		}

		DirectoryEntry newDirectoryEntry = DirectoryEntry.buildDir(newDir, ATTRIBUTE_DIRECTORY);
		//currentPath=/, param1=test
		//step: 根目录，不需要存储cluster。
		if (ROOT_PATH.equals(currentPath)) {
			//step:直接保存至RootDirectoryRegion
			fat16xFileSystem.getRootDirectoryRegion().
				getDirectoryEntries()[fat16xFileSystem.getRootDirectoryRegion().freeIndex()] = newDirectoryEntry;
		} else {
			//currentPath=/test, param1=111
			//step：非根目录，寻根目录节点
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(currentPath, fat16xFileSystem);
			int startingCluster = rootDirectoryEntry.getStartingCluster();
			//startingCluster==0,说明之前没有分配目录cluster
			if (startingCluster == 0) {
				//选择空闲的cluster，并把当前创建的目录数据写入
				int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
				//保存数据区域数据
				fat16xFileSystem.getDataRegion().saveDir(newDirectoryEntry.getData(), firstFreeFat, fat16xFileSystem);
				//保存fat区数据
				fat16xFileSystem.getFatRegion().save(firstFreeFat, FAT_NC_END_OF_FILE);
				rootDirectoryEntry.setStartingCluster(firstFreeFat);
			} else {
				String[] split = currentPath.split(PATH_SPLIT);
				int beginCluster = rootDirectoryEntry.getStartingCluster();
				if (split.length == 2) {
					//currentPath=/test, param1=222
					//step： 一级目录创建：之前分配过目录，那么我们根据当前路径，去寻找到对应的cluster，并把目录数据追加进去
					createFileOrDirInFirstLevelPath(beginCluster, newDirectoryEntry);
				} else {
					//currentPath=/test/222, param1=xyl
					//currentPath=/test/222, param1=xyl1
					createFileOrDirInSecondLevelPath(split, beginCluster, newDirectoryEntry);
				}
			}
		}

		return FileSystemActionResult.success();
	}

	/**
	 * touch
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	@Override
	public FileSystemActionResult touch(Command command) {
		//step: 文件名，文件名后缀长度限制
		String fileName = command.getParams().get(0);
		String fileNameExtension = "";
		if (fileName.contains(".")) {
			String[] split = fileName.split("\\.");
			fileName = split[0];
			fileNameExtension = split[1];
		}
		if (fileName.length() > 8 || fileNameExtension.length() > 3) {
			return FileSystemActionResult.fail(FILENAME_LENGTH_TOO_LONG);
		}
		//step: 当前路径是根目录，需要判断根目录区域空间是否充足
		String currentPath = command.getCurrentPath();
		if (ROOT_PATH.equals(currentPath) && !hasIdleRootDirectorySpace()) {
			return FileSystemActionResult.fail(INSUFFICIENT_SPACE);
		}
		DirectoryEntry newDirectoryEntry = DirectoryEntry.buildFile(fileName, fileNameExtension);
		//step: 根下创建文件，不需要存储目录cluster
		//ex：/xyl.jpg
		if (ROOT_PATH.equals(currentPath)) {
			fat16xFileSystem.getRootDirectoryRegion().
				getDirectoryEntries()[fat16xFileSystem.getRootDirectoryRegion().freeIndex()] = newDirectoryEntry;
		} else {
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(currentPath, fat16xFileSystem);
			if (rootDirectoryEntry == null) {
				return FileSystemActionResult.fail(DIR_DATA_ERROR);
			} else {
				String[] split = currentPath.split(PATH_SPLIT);
				int beginCluster = rootDirectoryEntry.getStartingCluster();
				//ex：/test/xyl.jpg
				//step：一级目录创建文件
				if (split.length == 2) {
					createFileOrDirInFirstLevelPath(beginCluster, newDirectoryEntry);
				} else {
					//ex：/test/222/xyl.jpg
					//step：非一级目录创建文件
					createFileOrDirInSecondLevelPath(split, beginCluster, newDirectoryEntry);
				}
			}
		}
		return FileSystemActionResult.success();
	}

	/**
	 * 一级目录创建文件or目录 ex：/test/xyl.jpg ex：/test/111
	 */
	void createFileOrDirInFirstLevelPath(int beginCluster, DirectoryEntry newDirectoryEntry) {
		int endOfFileCluster = fat16xFileSystem.getFatRegion().endOfFileCluster(beginCluster);
		//保存目录信息至数据区域数据
		int newEndOfFileCluster = fat16xFileSystem.getDataRegion().saveDir(newDirectoryEntry.getData(), endOfFileCluster,
			fat16xFileSystem);
		if (endOfFileCluster != newEndOfFileCluster) {
			//保存fat区数据,新cluster置为尾部，老cluster指向新的cluster
			fat16xFileSystem.getFatRegion().save(newEndOfFileCluster, FAT_NC_END_OF_FILE);
			fat16xFileSystem.getFatRegion().save(endOfFileCluster, String.format("%04x", newEndOfFileCluster));
		}
	}

	/**
	 * 在二级路径(及以上)创建文件或dir
	 *
	 * @param split             分裂
	 * @param beginCluster      开始集群
	 * @param newDirectoryEntry 新目录条目
	 */
	void createFileOrDirInSecondLevelPath(String[] split, int beginCluster, DirectoryEntry newDirectoryEntry) {
		for (int i = 2; i < split.length; i++) {
			//找到对应的全部目录cluster
			int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(beginCluster);
			DataCluster[] clusters = fat16xFileSystem.getDataRegion().findClusters(index);
			//循环跳出label，下边循环找到match的目录时，直接跳到这一层循环
			outloop:
			for (DataCluster cluster : clusters) {
				if (cluster != null) {
					//遍历所有扇区
					for (DataSector sector : cluster.getSectors()) {
						if (sector != null) {
							//找到当前扇区的末尾
							int freeSpaceIndex = sector.freeSpaceIndexForDir(DIRECTORY_ENTRY_SIZE);
							int begin = 0;
							while (begin < freeSpaceIndex) {
								DirectoryEntry directoryEntry = new DirectoryEntry();
								System.arraycopy(sector.getData(), begin, directoryEntry.getData(), 0, DIRECTORY_ENTRY_SIZE);
								//当前directoryEntry为文件夹，并且名称可以match
								if (1 == directoryEntry.getAttribute(ATTRIBUTE_DIRECTORY_POS) && directoryEntry.getFileName()
									.equals(split[i])) {
									beginCluster = directoryEntry.getStartingCluster();
									//如果是最后一层目录，操作数据保存
									if (i == split.length - 1) {
										//beginCluster = 0,说明当前文件夹创建后，并未分配cluster，需要先初始化
										//currentPath=/test/222, param1=xyl.jpg
										if (beginCluster == 0) {
											int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
											directoryEntry.setStartingCluster(firstFreeFat);
											//directoryEntry是从原来的数据读取出来的，所以更新后，数据要刷回去
											System.arraycopy(directoryEntry.getData(), 0, sector.getData(), begin, DIRECTORY_ENTRY_SIZE);
											//保存数据区域数据
											fat16xFileSystem.getDataRegion()
												.saveDir(newDirectoryEntry.getData(), firstFreeFat, fat16xFileSystem);
											//保存fat区数据
											fat16xFileSystem.getFatRegion().save(firstFreeFat, FAT_NC_END_OF_FILE);
										} else {
											//currentPath=/test/222, param1=xyl1.jpg
											//beginCluster不为0,说明分配过cluster，直接追加内容
											//通过beginCluster去查询fat区，查询后续的cluster
											int endOfFileCluster = fat16xFileSystem.getFatRegion().endOfFileCluster(beginCluster);
											//保存目录信息至数据区域数据
											int newEndOfFileCluster = fat16xFileSystem.getDataRegion()
												.saveDir(newDirectoryEntry.getData(), endOfFileCluster,
													fat16xFileSystem);
											if (endOfFileCluster != newEndOfFileCluster) {
												//保存fat区数据,新cluster置为尾部，老cluster指向新的cluster
												fat16xFileSystem.getFatRegion().save(newEndOfFileCluster, FAT_NC_END_OF_FILE);
												fat16xFileSystem.getFatRegion()
													.save(endOfFileCluster, String.format("%04x", newEndOfFileCluster));
											}
										}
									}
									break outloop;
								}
								begin += DIRECTORY_ENTRY_SIZE;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ls
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	@Override
	public FileSystemActionResult ls(Command command) {
		List<String> result = new ArrayList<>();
		//step: 根目录，查询
		if (ROOT_PATH.equals(command.getCurrentPath())) {
			for (DirectoryEntry directoryEntry : fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()) {
				if (directoryEntry == null) {
					break;
				}
				result.add(buildName(directoryEntry));
			}
		} else {
			//step；非根目录查询,/test ,/test/222
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(command.getCurrentPath(), fat16xFileSystem);
			int beginCluster = rootDirectoryEntry.getStartingCluster();
			//找到对应的全部目录cluster
			String[] splitPath = command.getCurrentPath().split(PATH_SPLIT);
			if (2 == splitPath.length) {
				//一级目录查询， /test
				List<DirectoryEntry> directoryEntries = buildAllDirectoryEntry(beginCluster);
				for (DirectoryEntry entry : directoryEntries) {
					result.add(buildName(entry));
				}
			} else {
				//二级及二级以上目录查询，/test/222
				for (int i = 2; i < splitPath.length; i++) {
					//找到对应的全部目录cluster
					int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(beginCluster);
					DataCluster[] clusters = fat16xFileSystem.getDataRegion().findClusters(index);
					//循环跳出label，下边循环找到match的目录时，直接跳到这一层循环
					outloop:
					for (DataCluster cluster : clusters) {
						if (cluster != null) {
							//遍历所有扇区
							for (DataSector sector : cluster.getSectors()) {
								if (sector != null) {
									//找到当前扇区的末尾
									int freeSpaceIndex = sector.freeSpaceIndexForDir(DIRECTORY_ENTRY_SIZE);
									int begin = 0;
									while (begin < freeSpaceIndex) {
										DirectoryEntry directoryEntry = new DirectoryEntry();
										System.arraycopy(sector.getData(), begin, directoryEntry.getData(), 0, DIRECTORY_ENTRY_SIZE);
										//当前directoryEntry为文件夹，并且名称可以match
										if (1 == directoryEntry.getAttribute(ATTRIBUTE_DIRECTORY_POS) && directoryEntry.getFileName()
											.equals(splitPath[i])) {
											beginCluster = directoryEntry.getStartingCluster();
											//如果是最后一层目录，读取数据
											if (i == splitPath.length - 1) {
												List<DirectoryEntry> directoryEntries = buildAllDirectoryEntry(directoryEntry.getStartingCluster());
												for (DirectoryEntry entry : directoryEntries) {
													result.add(buildName(entry));
												}
											}
											break outloop;
										}
										begin += DIRECTORY_ENTRY_SIZE;
									}
								}
							}
						}
					}
				}
			}
		}
		return FileSystemActionResult.success(JsonUtils.parseBean2Str(result));
	}

	/**
	 * echo: echo "123" >> hello
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	@Override
	public FileSystemActionResult echo(Command command) {

		String currentPath = command.getCurrentPath();
		String data = command.getParams().get(0);
		byte[] dataBytes = data.getBytes();
		String wholeFileName = command.getParams().get(1);
		String fileName = wholeFileName;
		String fileNameExtension = "";
		if (wholeFileName.contains(".")) {
			String[] split = wholeFileName.split("\\.");
			fileName = split[0];
			fileNameExtension = split[1];
		}
		if (fileName.length() > 8 || fileNameExtension.length() > 3) {
			return FileSystemActionResult.fail(FILENAME_LENGTH_TOO_LONG);
		}
		//currentPath=/, param1=test
		if (ROOT_PATH.equals(currentPath)) {
			//step:直接保存至RootDirectoryRegion
			boolean haveCreatedFile = saveFatAndDataClusterForFileInRootRegion(wholeFileName, dataBytes);
			//文件没有创建过
			if (!haveCreatedFile) {
				//先把文件搞出来
				touch(Command.build(currentPath, Collections.singletonList(wholeFileName)));
				//再保存目录和数据
				saveFatAndDataClusterForFileInRootRegion(wholeFileName, dataBytes);
			}
		} else {
			//step：非根目录，寻根目录节点
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(currentPath, fat16xFileSystem);
			int rootStartingCluster = rootDirectoryEntry.getStartingCluster();
			//rootStartingCluster==0,说明之前没有分配目录cluster
			if (rootStartingCluster == 0) {
				//先把文件搞出来
				touch(Command.build(currentPath, Collections.singletonList(wholeFileName)));
				//保存目录和数据
				saveFileDataAndUpdateDirectoryEntry(dataBytes, rootDirectoryEntry);
			} else {
				String[] split = currentPath.split(PATH_SPLIT);
				//ex：/test/111
				//step：一级目录文件内容写入
				if (split.length == 2) {
					boolean haveCreateFile = saveFatAndDataClusterForFileInFirstPath(rootStartingCluster, wholeFileName, dataBytes);
					//之前文件压根没创建过
					if (!haveCreateFile) {
						//先把文件搞出来
						touch(Command.build(currentPath, Collections.singletonList(wholeFileName)));
						//再保存一次
						saveFatAndDataClusterForFileInFirstPath(rootStartingCluster, wholeFileName, dataBytes);
					}
				} else {
					//step：二级目录文件内容写入
					boolean haveCreateFile = saveFatAndDataClusterForFileInSecondPath(currentPath, rootStartingCluster, wholeFileName, dataBytes);
					//之前文件压根没创建过
					if (!haveCreateFile) {
						//先把文件搞出来
						touch(Command.build(currentPath, Collections.singletonList(wholeFileName)));
						//再保存一次
						saveFatAndDataClusterForFileInSecondPath(currentPath, rootStartingCluster, wholeFileName, dataBytes);
					}
				}
			}
		}

		return FileSystemActionResult.success();
	}

	/**
	 * cat
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	@Override
	public FileSystemActionResult cat(Command command) {
		String currentPath = command.getCurrentPath();
		String fileName = command.getParams().get(0);
		//cat /xyl.dat
		if (ROOT_PATH.equals(currentPath)) {
			for (DirectoryEntry directoryEntry : fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()) {
				if (directoryEntry == null) {
					break;
					//匹配到对应的文件
				} else if (fileName.equals(directoryEntry.getWholeFileName())) {
					int beginCluster = directoryEntry.getStartingCluster();
					//文件之前未写入过内容
					if (beginCluster == 0) {
						return FileSystemActionResult.success();
					} else {
						int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(beginCluster);
						byte[] dataBytes = new byte[(int) directoryEntry.getFileSize()];
						fat16xFileSystem.getDataRegion().getClustersData(index, dataBytes);
						directoryEntry.setLastAccessTimeStamp();
						return FileSystemActionResult.success(new String(dataBytes));
					}
				}
			}
		} else {
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(currentPath, fat16xFileSystem);
			int startingCluster = rootDirectoryEntry.getStartingCluster();
			if (startingCluster != 0) {
				//cat /test/xyl1.dat
				String[] splitPath = command.getCurrentPath().split(PATH_SPLIT);
				if (splitPath.length == 2) {
					//一级目录全部元素
					List<DirectoryEntry> directoryEntries = buildAllDirectoryEntry(startingCluster);
					for (DirectoryEntry directoryEntry : directoryEntries) {
						//找到对应文件，返回数据
						if (directoryEntry.getWholeFileName().equals(fileName)) {
							int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(directoryEntry.getStartingCluster());
							byte[] dataBytes = new byte[(int) directoryEntry.getFileSize()];
							fat16xFileSystem.getDataRegion().getClustersData(index, dataBytes);
							directoryEntry.setLastAccessTimeStamp();
							return FileSystemActionResult.success(new String(dataBytes));
						}
					}
				} else {
					//cat /test/111/xyl2.dat
					//二级目录遍历match
					for (int i = 2; i < splitPath.length; i++) {
						//找到对应的全部目录cluster
						List<DirectoryEntry> directoryEntries = buildAllDirectoryEntry(startingCluster);
						for (DirectoryEntry directoryEntry : directoryEntries) {
							//匹配目录名称
							if (1 == directoryEntry.getAttribute(ATTRIBUTE_DIRECTORY_POS) && directoryEntry.getFileName().equals(splitPath[i])) {
								if (i == splitPath.length - 1) {
									//找到对应文件，返回数据
									List<DirectoryEntry> currentPathDirectoryEntries = buildAllDirectoryEntry(directoryEntry.getStartingCluster());
									for (DirectoryEntry currentPathDirectoryEntry : currentPathDirectoryEntries) {
										if (currentPathDirectoryEntry.getWholeFileName().equals(fileName)) {
											int[] index = fat16xFileSystem.getFatRegion()
												.allOfFileClusterIndex(currentPathDirectoryEntry.getStartingCluster());
											byte[] dataBytes = new byte[(int) currentPathDirectoryEntry.getFileSize()];
											fat16xFileSystem.getDataRegion().getClustersData(index, dataBytes);
											currentPathDirectoryEntry.setLastAccessTimeStamp();
											return FileSystemActionResult.success(new String(dataBytes));
										}
									}
								} else {
									startingCluster = directoryEntry.getStartingCluster();
								}
							}
						}
					}
				}
			}
		}
		return FileSystemActionResult.fail(NO_SUCH_FILE_OR_DIRECTORY);
	}

	private boolean saveFatAndDataClusterForFileInRootRegion(String wholeFileName, byte[] dataBytes) {
		for (DirectoryEntry directoryEntry : fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()) {
			if (directoryEntry == null) {
				break;
				//匹配到对应的文件
			} else if (wholeFileName.equals(directoryEntry.getWholeFileName())) {
				int beginCluster = directoryEntry.getStartingCluster();
				//文件之前未写入过内容
				if (beginCluster == 0) {
					saveFileDataAndUpdateDirectoryEntry(dataBytes, directoryEntry);
				} else {
					appendSaveFatAndDataClusterForFile(dataBytes, directoryEntry);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 二级目录存储文件数据
	 *
	 * @param beginCluster 开始集群
	 * @param fileName     文件名称
	 * @param dataBytes    数据字节
	 * @param currentPath  当前路径
	 * @return boolean
	 */
	private boolean saveFatAndDataClusterForFileInSecondPath(String currentPath, int beginCluster, String fileName, byte[] dataBytes) {
		boolean haveCreateFile = false;
		String[] splitPath = currentPath.split(PATH_SPLIT);
		//ex：/test/222/xyl
		//step：二级目录文件内容写入
		for (int i = 2; i < splitPath.length; i++) {
			//找到对应的全部目录cluster
			int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(beginCluster);
			DataCluster[] clusters = fat16xFileSystem.getDataRegion().findClusters(index);
			for (DataCluster cluster : clusters) {
				if (cluster != null) {
					//遍历所有扇区
					for (DataSector sector : cluster.getSectors()) {
						if (sector != null) {
							//找到当前扇区的末尾
							int freeSpaceIndex = sector.freeSpaceIndexForDir(DIRECTORY_ENTRY_SIZE);
							int begin = 0;
							while (begin < freeSpaceIndex) {
								DirectoryEntry directoryEntry = new DirectoryEntry();
								System.arraycopy(sector.getData(), begin, directoryEntry.getData(), 0, DIRECTORY_ENTRY_SIZE);
								//当前directoryEntry为文件夹，并且名称可以match
								if (1 == directoryEntry.getAttribute(ATTRIBUTE_DIRECTORY_POS) && directoryEntry.getFileName()
									.equals(splitPath[i]) && i == splitPath.length - 1) {
									//当前目录未初始化
									if (directoryEntry.getStartingCluster() == 0) {
										//先把文件搞出来
										touch(Command.build(currentPath, Collections.singletonList(fileName)));
//										//保存目录和数据
//										saveFileDataAndUpdateDirectoryEntry(dataBytes, directoryEntry);
//										//数据刷回去
//										System.arraycopy(directoryEntry.getData(), 0, sector.getData(), begin, DIRECTORY_ENTRY_SIZE);
									} else {
										haveCreateFile = saveFatAndDataClusterForFileInCurrentPath(directoryEntry.getStartingCluster(), fileName,
											dataBytes);
									}
								}
								begin += DIRECTORY_ENTRY_SIZE;
							}
						}
					}
				}
			}
		}
		return haveCreateFile;

	}

	/**
	 * 一级目录存储文件数据
	 *
	 * @param beginCluster 开始集群
	 * @param fileName     文件名称
	 * @param dataBytes    数据字节
	 * @return boolean
	 */
	private boolean saveFatAndDataClusterForFileInFirstPath(int beginCluster, String fileName, byte[] dataBytes) {
		return saveFatAndDataClusterForFileInCurrentPath(beginCluster, fileName, dataBytes);
	}

	/**
	 * 在当前目录保存目录和文件
	 *
	 * @param beginCluster 开始集群
	 * @param fileName     文件名称
	 * @param dataBytes    数据字节
	 */
	private boolean saveFatAndDataClusterForFileInCurrentPath(int beginCluster, String fileName, byte[] dataBytes) {
		boolean haveCreatedFile = false;
		//找到对应的全部目录cluster
		int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(beginCluster);
		DataCluster[] clusters = fat16xFileSystem.getDataRegion().findClusters(index);
		//遍历目录，找到对应的文件
		for (DataCluster cluster : clusters) {
			if (cluster != null) {
				//遍历所有扇区
				for (DataSector sector : cluster.getSectors()) {
					if (sector != null) {
						//找到当前扇区的末尾
						int freeSpaceIndex = sector.freeSpaceIndexForDir(DIRECTORY_ENTRY_SIZE);
						int begin = 0;
						while (begin < freeSpaceIndex) {
							DirectoryEntry directoryEntry = new DirectoryEntry();
							System.arraycopy(sector.getData(), begin, directoryEntry.getData(), 0, DIRECTORY_ENTRY_SIZE);
							//找到了
							if (directoryEntry.getWholeFileName().equals(fileName)) {
								//文件之前未写入过内容
								if (directoryEntry.getStartingCluster() == 0) {
									saveFileDataAndUpdateDirectoryEntry(dataBytes, directoryEntry);
								} else {
									appendSaveFatAndDataClusterForFile(dataBytes, directoryEntry);
								}
								//数据一定要刷回去
								System.arraycopy(directoryEntry.getData(), 0, sector.getData(), begin, DIRECTORY_ENTRY_SIZE);
								haveCreatedFile = true;
							}
							begin += DIRECTORY_ENTRY_SIZE;
						}
					}
				}
			}
		}
		return haveCreatedFile;
	}

	/**
	 * 保存集群和数据文件
	 *
	 * @param dataBytes      数据字节
	 * @param directoryEntry 目录条目
	 */
	private void saveFileDataAndUpdateDirectoryEntry(byte[] dataBytes, DirectoryEntry directoryEntry) {
		//选择空闲的cluster，并把当前创建的目录数据写入
		int[] fatArray = fat16xFileSystem.getFatRegion().freeFatArray(dataBytes.length);
		//保存数据区域数据
		fat16xFileSystem.getDataRegion().saveFile(dataBytes, fatArray);
		//更新文件信息
		directoryEntry.updateWriteInfo(dataBytes.length);
		//更新初始坐标
		directoryEntry.setStartingCluster(fatArray[0]);
	}

	/**
	 * 追加保存集群和数据文件
	 *
	 * @param dataBytes      数据字节
	 * @param directoryEntry 目录条目
	 */
	private void appendSaveFatAndDataClusterForFile(byte[] dataBytes, DirectoryEntry directoryEntry) {
		//文件之前写入过,所以需要追加数据
		int endOfFileCluster = fat16xFileSystem.getFatRegion().endOfFileCluster(directoryEntry.getStartingCluster());
		//尾节点剩余空间
		int endOfFileClusterRemainSpace =
			(int) (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR - directoryEntry.getFileSize() % (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR));
		//文件size小于等于尾节点剩余空间，直接当前节点追加内容
		if (dataBytes.length <= endOfFileClusterRemainSpace) {
			fat16xFileSystem.getDataRegion().appendSaveFile(dataBytes, endOfFileCluster, (int) directoryEntry.getFileSize());
		} else {
			//文件size大于尾节点剩余空间
			byte[] appendSaveData = new byte[endOfFileClusterRemainSpace];
			System.arraycopy(dataBytes, 0, appendSaveData, 0, endOfFileClusterRemainSpace);
			//把当前节点装满
			fat16xFileSystem.getDataRegion().appendSaveFile(appendSaveData, endOfFileCluster, (int) directoryEntry.getFileSize());
			//申请新的数据集群列表保存剩余数据
			int[] fatArray = fat16xFileSystem.getFatRegion().freeFatArray(dataBytes.length - endOfFileClusterRemainSpace);
			//保存数据区域数据
			byte[] saveData = new byte[dataBytes.length - endOfFileClusterRemainSpace];
			System.arraycopy(dataBytes, endOfFileClusterRemainSpace, saveData, 0, dataBytes.length - endOfFileClusterRemainSpace);
			fat16xFileSystem.getDataRegion().saveFile(saveData, fatArray);
			//原有链路的末尾，指向新申请的链表首部
			fat16xFileSystem.getFatRegion().save(endOfFileCluster, String.format("%04x", fatArray[0]));
		}
		//更新文件大小,时间
		directoryEntry.updateWriteInfo(directoryEntry.getFileSize() + dataBytes.length);
	}

	private List<DirectoryEntry> buildAllDirectoryEntry(int startingCluster) {
		List<DirectoryEntry> directoryEntries = new ArrayList<>();
		//找到对应的全部目录data cluster
		int[] dataIndex = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(startingCluster);
		DataCluster[] dataClusters = fat16xFileSystem.getDataRegion().findClusters(dataIndex);
		//遍历所有目录集群信息
		for (DataCluster cluster : dataClusters) {
			if (cluster != null) {
				//遍历所有扇区
				for (DataSector sector : cluster.getSectors()) {
					if (sector != null) {
						//找到当前扇区的末尾
						int freeSpaceIndex = sector.freeSpaceIndexForDir(DIRECTORY_ENTRY_SIZE);
						int begin = 0;
						while (begin < freeSpaceIndex) {
							DirectoryEntry directoryEntry = new DirectoryEntry();
							System.arraycopy(sector.getData(), begin, directoryEntry.getData(), 0, DIRECTORY_ENTRY_SIZE);
							directoryEntries.add(directoryEntry);
							begin += DIRECTORY_ENTRY_SIZE;
						}
					}
				}
			}
		}
		return directoryEntries;
	}

	private String buildName(DirectoryEntry directoryEntry) {
		String name;
		if (1 == directoryEntry.getAttribute(ATTRIBUTE_DIRECTORY_POS)) {
			name = "/" + directoryEntry.getFileName();
		} else {
			name = directoryEntry.getFileName() + "." + directoryEntry.getFileNameExtension();
		}
		return name;
	}

	DirectoryEntry findRootDirectoryEntry(String currentPath, Fat16xFileSystem fat16xFileSystem) {
		String[] split = currentPath.split(PATH_SPLIT);
		String path = split[1];
		DirectoryEntry directoryEntry = null;
		for (DirectoryEntry tempEntry : fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()) {
			if (tempEntry != null && tempEntry.getFileName().equals(path)) {
				directoryEntry = tempEntry;
			}
		}
		return directoryEntry;

	}

	/**
	 * 空闲目录空间
	 *
	 * @return boolean
	 */
	@Override
	protected boolean hasIdleRootDirectorySpace() {
		return fat16xFileSystem.getRootDirectoryRegion().haveIdleSpace();
	}
}
