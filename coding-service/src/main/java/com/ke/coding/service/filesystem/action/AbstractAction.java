package com.ke.coding.service.filesystem.action;

import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY_POS;
import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.FAT_NC_END_OF_FILE;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataCluster;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataSector;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.DataRegionService;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.FatRegionService;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.RootDirectoryRegionService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:40
 * @description:
 */
public abstract class AbstractAction implements Action {

	@Autowired
	public DataRegionService dataRegionService;
	@Autowired
	public RootDirectoryRegionService rootDirectoryRegionService;
	@Autowired
	public FatRegionService fatRegionService;


	/**
	 * 找到根目录条目
	 *
	 * @param currentPath      当前路径
	 * @param fat16xFileSystem fat16x文件系统
	 * @return {@link DirectoryEntry}
	 */
	public DirectoryEntry findRootDirectoryEntry(String currentPath, Fat16xFileSystem fat16xFileSystem) {
		String[] split = currentPath.split(PATH_SPLIT);
		String path = split[1];
		DirectoryEntry directoryEntry = null;
		for (int i = 0; i < fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries().length; i++) {
			if (fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()[i] != null && fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()[i].getFileName().equals(path)) {
				directoryEntry = fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()[i];
				directoryEntry.setIndex(i);
			}
		}
		return directoryEntry;
	}

	/**
	 * 构建所有目录条目
	 *
	 * @param startingCluster  从集群
	 * @param fat16xFileSystem fat16x文件系统
	 * @return {@link List}<{@link DirectoryEntry}>
	 */
	public List<DirectoryEntry> buildAllDirectoryEntry(int startingCluster, Fat16xFileSystem fat16xFileSystem) {
		if (startingCluster == 0) {
			return new ArrayList<>();
		}
		List<DirectoryEntry> directoryEntries = new ArrayList<>();
		//找到对应的全部目录data cluster
		int[] dataIndex = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(startingCluster);
		DataCluster[] dataClusters = dataRegionService.findClusters(dataIndex);
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

	/**
	 * 一级目录创建文件or目录 ex：/test/xyl.jpg ex：/test/111
	 */
	public void createFileOrDirInFirstLevelPath(int beginCluster, DirectoryEntry newDirectoryEntry, Fat16xFileSystem fat16xFileSystem) {
		int endOfFileCluster = fat16xFileSystem.getFatRegion().endOfFileCluster(beginCluster);
		//保存目录信息至数据区域数据
		int newEndOfFileCluster = dataRegionService.saveDir(newDirectoryEntry.getData(), endOfFileCluster,
			fat16xFileSystem);
		if (endOfFileCluster != newEndOfFileCluster) {
			//保存fat区数据,新cluster置为尾部，老cluster指向新的cluster
			fatRegionService.save(newEndOfFileCluster, FAT_NC_END_OF_FILE, fat16xFileSystem.getFatRegion());
			fatRegionService.save(endOfFileCluster, String.format("%04x", newEndOfFileCluster), fat16xFileSystem.getFatRegion());
		}
	}

	/**
	 * 在二级路径(及以上)创建文件或dir
	 *
	 * @param split             分裂
	 * @param beginCluster      开始集群
	 * @param newDirectoryEntry 新目录条目
	 */
	public void createFileOrDirInSecondLevelPath(String[] split, int beginCluster, DirectoryEntry newDirectoryEntry,
		Fat16xFileSystem fat16xFileSystem) {
		for (int i = 2; i < split.length; i++) {
			//找到对应的全部目录cluster
			int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(beginCluster);
			DataCluster[] clusters = dataRegionService.findClusters(index);
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
											dataRegionService.updateDirInfo(cluster);
											//保存数据区域数据
											dataRegionService.saveDir(newDirectoryEntry.getData(), firstFreeFat, fat16xFileSystem);
											//保存fat区数据
											fatRegionService.save(firstFreeFat, FAT_NC_END_OF_FILE, fat16xFileSystem.getFatRegion());
										} else {
											//currentPath=/test/222, param1=xyl1.jpg
											//beginCluster不为0,说明分配过cluster，直接追加内容
											//通过beginCluster去查询fat区，查询后续的cluster
											int endOfFileCluster = fat16xFileSystem.getFatRegion().endOfFileCluster(beginCluster);
											//保存目录信息至数据区域数据
											int newEndOfFileCluster = dataRegionService.saveDir(newDirectoryEntry.getData(), endOfFileCluster,
													fat16xFileSystem);
											if (endOfFileCluster != newEndOfFileCluster) {
												//保存fat区数据,新cluster置为尾部，老cluster指向新的cluster
												fatRegionService.save(newEndOfFileCluster, FAT_NC_END_OF_FILE, fat16xFileSystem.getFatRegion());
												fatRegionService.save(endOfFileCluster, String.format("%04x", newEndOfFileCluster), fat16xFileSystem.getFatRegion());
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

	public boolean hasIdleRootDirectorySpace(Fat16xFileSystem fat16xFileSystem) {
		return fat16xFileSystem.getRootDirectoryRegion().haveIdleSpace();
	}

}
