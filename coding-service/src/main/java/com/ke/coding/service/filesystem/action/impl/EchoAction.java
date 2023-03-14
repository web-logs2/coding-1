package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY_POS;
import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.PER_CLUSTER_SECTOR;
import static com.ke.coding.api.enums.Constants.PER_SECTOR_BYTES;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.FILENAME_LENGTH_TOO_LONG;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataCluster;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataSector;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.service.filesystem.action.AbstractAction;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class EchoAction extends AbstractAction {

	@Autowired
	TouchAction touchAction;

	@Override
	public FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem) {
		String currentPath = command.getCurrentPath();
		String data = command.getParams().get(0);
		byte[] dataBytes = data.getBytes();
		String wholeFileName = command.getParams().get(2);
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
			boolean haveCreatedFile = saveFatAndDataClusterForFileInRootRegion(wholeFileName, dataBytes, fat16xFileSystem);
			//文件没有创建过
			if (!haveCreatedFile) {
				//先把文件搞出来
				touchAction.run(Command.build(currentPath, Collections.singletonList(wholeFileName)), fat16xFileSystem);
				//再保存目录和数据
				saveFatAndDataClusterForFileInRootRegion(wholeFileName, dataBytes, fat16xFileSystem);
			}
		} else {
			//step：非根目录，寻根目录节点
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(currentPath, fat16xFileSystem);
			int rootStartingCluster = rootDirectoryEntry.getStartingCluster();
			//rootStartingCluster==0,说明之前没有分配目录cluster
			if (rootStartingCluster == 0) {
				//先把文件搞出来
				touchAction.run(Command.build(currentPath, Collections.singletonList(wholeFileName)), fat16xFileSystem);
				//保存目录和数据
				saveFileDataAndUpdateDirectoryEntry(dataBytes, rootDirectoryEntry, fat16xFileSystem);
			} else {
				String[] split = currentPath.split(PATH_SPLIT);
				//ex：/test/111
				//step：一级目录文件内容写入
				if (split.length == 2) {
					boolean haveCreateFile = saveFatAndDataClusterForFileInFirstPath(rootStartingCluster, wholeFileName, dataBytes, fat16xFileSystem);
					//之前文件压根没创建过
					if (!haveCreateFile) {
						//先把文件搞出来
						touchAction.run(Command.build(currentPath, Collections.singletonList(wholeFileName)), fat16xFileSystem);
						;
						//再保存一次
						saveFatAndDataClusterForFileInFirstPath(rootStartingCluster, wholeFileName, dataBytes, fat16xFileSystem);
					}
				} else {
					//step：二级目录文件内容写入
					boolean haveCreateFile = saveFatAndDataClusterForFileInSecondPath(currentPath, rootStartingCluster, wholeFileName, dataBytes,
						fat16xFileSystem);
					//之前文件压根没创建过
					if (!haveCreateFile) {
						//先把文件搞出来
						touchAction.run(Command.build(currentPath, Collections.singletonList(wholeFileName)), fat16xFileSystem);
						;
						//再保存一次
						saveFatAndDataClusterForFileInSecondPath(currentPath, rootStartingCluster, wholeFileName, dataBytes, fat16xFileSystem);
					}
				}
			}
		}

		return FileSystemActionResult.success();
	}

	/**
	 * 一级目录存储文件数据
	 *
	 * @param beginCluster 开始集群
	 * @param fileName     文件名称
	 * @param dataBytes    数据字节
	 * @return boolean
	 */
	private boolean saveFatAndDataClusterForFileInFirstPath(int beginCluster, String fileName, byte[] dataBytes, Fat16xFileSystem fat16xFileSystem) {
		return saveFatAndDataClusterForFileInCurrentPath(beginCluster, fileName, dataBytes, fat16xFileSystem);
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
	private boolean saveFatAndDataClusterForFileInSecondPath(String currentPath, int beginCluster, String fileName, byte[] dataBytes,
		Fat16xFileSystem fat16xFileSystem) {
		boolean haveCreateFile = false;
		String[] splitPath = currentPath.split(PATH_SPLIT);
		//ex：/test/222/xyl
		//step：二级目录文件内容写入
		for (int i = 2; i < splitPath.length; i++) {
			//找到对应的全部目录cluster
			int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(beginCluster);
			DataCluster[] clusters = dataRegionService.findClusters(index);
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
										touchAction.run(Command.build(currentPath, Collections.singletonList(fileName)), fat16xFileSystem);
									} else {
										haveCreateFile = saveFatAndDataClusterForFileInCurrentPath(directoryEntry.getStartingCluster(), fileName,
											dataBytes, fat16xFileSystem);
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

	private boolean saveFatAndDataClusterForFileInRootRegion(String wholeFileName, byte[] dataBytes, Fat16xFileSystem fat16xFileSystem) {
		for (DirectoryEntry directoryEntry : fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()) {
			if (directoryEntry == null) {
				break;
				//匹配到对应的文件
			} else if (wholeFileName.equals(directoryEntry.getWholeFileName())) {
				int beginCluster = directoryEntry.getStartingCluster();
				//文件之前未写入过内容
				if (beginCluster == 0) {
					saveFileDataAndUpdateDirectoryEntry(dataBytes, directoryEntry, fat16xFileSystem);
				} else {
					appendSaveFatAndDataClusterForFile(dataBytes, directoryEntry, fat16xFileSystem);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 保存集群和数据文件
	 *
	 * @param dataBytes      数据字节
	 * @param directoryEntry 目录条目
	 */
	private void saveFileDataAndUpdateDirectoryEntry(byte[] dataBytes, DirectoryEntry directoryEntry, Fat16xFileSystem fat16xFileSystem) {
		//选择空闲的cluster，并把当前创建的目录数据写入
		int[] fatArray = fatRegionService.freeFatArray(dataBytes.length, fat16xFileSystem.getFatRegion());
		//保存数据区域数据
		dataRegionService.saveFile(dataBytes, fatArray);
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
	private void appendSaveFatAndDataClusterForFile(byte[] dataBytes, DirectoryEntry directoryEntry, Fat16xFileSystem fat16xFileSystem) {
		//文件之前写入过,所以需要追加数据
		int endOfFileCluster = fat16xFileSystem.getFatRegion().endOfFileCluster(directoryEntry.getStartingCluster());
		//尾节点剩余空间
		int endOfFileClusterRemainSpace =
			(int) (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR - directoryEntry.getFileSize() % (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR));
		//文件size小于等于尾节点剩余空间，直接当前节点追加内容
		if (dataBytes.length <= endOfFileClusterRemainSpace) {
			dataRegionService.appendSaveFile(dataBytes, endOfFileCluster, (int) directoryEntry.getFileSize());
		} else {
			//文件size大于尾节点剩余空间
			byte[] appendSaveData = new byte[endOfFileClusterRemainSpace];
			System.arraycopy(dataBytes, 0, appendSaveData, 0, endOfFileClusterRemainSpace);
			//把当前节点装满
			dataRegionService.appendSaveFile(appendSaveData, endOfFileCluster, (int) directoryEntry.getFileSize());
			//申请新的数据集群列表保存剩余数据
			int[] fatArray = fatRegionService.freeFatArray(dataBytes.length - endOfFileClusterRemainSpace, fat16xFileSystem.getFatRegion());
			//保存数据区域数据
			byte[] saveData = new byte[dataBytes.length - endOfFileClusterRemainSpace];
			System.arraycopy(dataBytes, endOfFileClusterRemainSpace, saveData, 0, dataBytes.length - endOfFileClusterRemainSpace);
			dataRegionService.saveFile(saveData, fatArray);
			//原有链路的末尾，指向新申请的链表首部
			fatRegionService.save(endOfFileCluster, String.format("%04x", fatArray[0]), fat16xFileSystem.getFatRegion());
		}
		//更新文件大小,时间
		directoryEntry.updateWriteInfo(directoryEntry.getFileSize() + dataBytes.length);
	}

	/**
	 * 在当前目录保存目录和文件
	 *
	 * @param beginCluster 开始集群
	 * @param fileName     文件名称
	 * @param dataBytes    数据字节
	 */
	private boolean saveFatAndDataClusterForFileInCurrentPath(int beginCluster, String fileName, byte[] dataBytes,
		Fat16xFileSystem fat16xFileSystem) {
		boolean haveCreatedFile = false;
		//找到对应的全部目录cluster
		int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(beginCluster);
		DataCluster[] clusters = dataRegionService.findClusters(index);
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
									saveFileDataAndUpdateDirectoryEntry(dataBytes, directoryEntry, fat16xFileSystem);
								} else {
									appendSaveFatAndDataClusterForFile(dataBytes, directoryEntry, fat16xFileSystem);
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
}
