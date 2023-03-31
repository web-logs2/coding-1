package com.ke.coding.service.filesystem.fat16xservice.filesystemservice;

import static com.ke.coding.api.enums.Constants.BOOT_SECTOR_SIZE;
import static com.ke.coding.api.enums.Constants.BOOT_SECTOR_START;
import static com.ke.coding.api.enums.Constants.DATA_REGION_START;
import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.FAT_NC_END_OF_FILE;
import static com.ke.coding.api.enums.Constants.FAT_SIZE;
import static com.ke.coding.api.enums.Constants.FAT_START;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.PER_CLUSTER_SECTOR;
import static com.ke.coding.api.enums.Constants.PER_SECTOR_BYTES;
import static com.ke.coding.api.enums.Constants.ROOT_DIRECTORY_SIZE;
import static com.ke.coding.api.enums.Constants.ROOT_DIRECTORY_START;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.common.ArrayUtils.array2List;
import static com.ke.coding.common.ArrayUtils.list2Ary;

import com.google.common.collect.Lists;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.bootregion.BootSector;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.RootDirectoryRegion;
import com.ke.coding.api.dto.filesystem.fat16x.fatregion.FatRegion;
import com.ke.coding.common.ArrayUtils;
import com.ke.coding.service.disk.IDisk;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.DataClusterService;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.DataRegionService;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.FatRegionService;
import com.ke.coding.service.filesystem.fat16xservice.regionservice.RootDirectoryRegionService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/23 14:27
 * @description:
 */
@Service
public class Fat16xSystemServiceImpl implements FileSystemService {

	@Getter
	@Setter
	Fat16xFileSystem fat16xFileSystem;

	@Autowired
	DataClusterService dataClusterService;

	@Autowired
	DataRegionService dataRegionService;

	@Autowired
	FatRegionService fatRegionService;

	@Autowired
	RootDirectoryRegionService rootDirectoryRegionService;

	@Autowired
	IDisk iDisk;

	/**
	 * 初始化文件系统
	 */
	@Override
	@PostConstruct
	public void init() {
		fat16xFileSystem = new Fat16xFileSystem();
		fat16xFileSystem.setReservedRegion(new BootSector(iDisk.readSector(BOOT_SECTOR_START, BOOT_SECTOR_SIZE)));
		fat16xFileSystem.setRootDirectoryRegion(new RootDirectoryRegion(iDisk.readSector(ROOT_DIRECTORY_START, ROOT_DIRECTORY_SIZE)));
		fat16xFileSystem.setFatRegion(new FatRegion(iDisk.readSector(FAT_START, FAT_SIZE)));
	}


	/**
	 * 找到根目录条目
	 *
	 * @param currentPath 当前路径
	 * @return {@link DirectoryEntry}
	 */
	private DirectoryEntry findRootDirectoryEntry(String currentPath) {
		String[] split = currentPath.split(PATH_SPLIT);
		String path = split[1];
		DirectoryEntry directoryEntry = null;
		for (int i = 0; i < fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries().length; i++) {
			if (fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()[i] != null && fat16xFileSystem.getRootDirectoryRegion()
				.getDirectoryEntries()[i].getFileName().equals(path)) {
				directoryEntry = fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()[i];
				directoryEntry.setIndex(i);
				directoryEntry.setRootEntry(true);
				break;
			}
		}
		return directoryEntry;
	}

	/**
	 * 获取条目下所有信息
	 *
	 * @param directoryEntry 目录条目
	 * @return {@link List}<{@link DirectoryEntry}>
	 */
	@Override
	public List<DirectoryEntry> getAllDirectoryEntry(DirectoryEntry directoryEntry) {
		if (directoryEntry == null || directoryEntry.getStartingCluster() == 0) {
			return new ArrayList<>();
		}
		if (directoryEntry.isRootEntry()) {
			return Arrays.asList(fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries());
		} else {
			List<DirectoryEntry> directoryEntries = new ArrayList<>();
			//找到对应的全部目录data cluster
			int[] dataIndex = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(directoryEntry.getStartingCluster());
			for (int index : dataIndex) {
				byte[] allData = dataClusterService.getClusterData(index);
				List<List<Byte>> lists = Lists.partition(array2List(allData), DIRECTORY_ENTRY_SIZE);
				for (List<Byte> bytes : lists) {
					if (ArrayUtils.isEmpty(bytes)) {
						break;
					} else {
						DirectoryEntry temp = new DirectoryEntry();
						System.arraycopy(list2Ary(bytes), 0, temp.getData(), 0, DIRECTORY_ENTRY_SIZE);
						directoryEntries.add(temp);
					}
				}
			}
			return directoryEntries;
		}

	}

	@Override
	public DirectoryEntry saveDir(String currentPath, String fileName, boolean dir) {
		DirectoryEntry newDirectoryEntry = dir ? DirectoryEntry.buildDir(fileName) : DirectoryEntry.buildFile(fileName);
		if (ROOT_PATH.equals(currentPath)) {
			rootDirectoryRegionService.save(fat16xFileSystem.getRootDirectoryRegion(), fat16xFileSystem.getRootDirectoryRegion().freeIndex(),
				newDirectoryEntry);
		} else {
			//数据区域数据保存
			DirectoryEntry directoryEntry = findDirectoryEntry(currentPath);
			directoryEntry = initDirectoryEntry(directoryEntry);
			int newEndOfFileCluster = dataRegionService.saveDir(newDirectoryEntry, directoryEntry.getStartingCluster(), fat16xFileSystem);
			fatRegionService.relink(directoryEntry.getStartingCluster(), newEndOfFileCluster, fat16xFileSystem.getFatRegion());
		}
		return newDirectoryEntry;
	}

	DirectoryEntry initDirectoryEntry(DirectoryEntry directoryEntry) {
		if (directoryEntry == null) {
			directoryEntry = new DirectoryEntry();
			int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
			directoryEntry.setStartingCluster(firstFreeFat);
			iDisk.writeSector(DATA_REGION_START + firstFreeFat * PER_CLUSTER_SECTOR, directoryEntry.getData());
			fatRegionService.save(firstFreeFat, FAT_NC_END_OF_FILE, fat16xFileSystem.getFatRegion());
		} else if (directoryEntry.getStartingCluster() == 0) {
			directoryEntry = initDirectoryEntryCluster(directoryEntry);
		}
		return directoryEntry;
	}

	DirectoryEntry initDirectoryEntryCluster(DirectoryEntry directoryEntry) {
		if (directoryEntry.getStartingCluster() == 0) {
			allocate(directoryEntry);
			persistDirectoryEntry(directoryEntry);
		}
		return directoryEntry;
	}

	void allocate(DirectoryEntry directoryEntry) {
		int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
		directoryEntry.setStartingCluster(firstFreeFat);
		fatRegionService.save(firstFreeFat, FAT_NC_END_OF_FILE, fat16xFileSystem.getFatRegion());
	}

	void persistDirectoryEntry(DirectoryEntry directoryEntry) {
		if (directoryEntry.isRootEntry()) {
			rootDirectoryRegionService.update(fat16xFileSystem.getRootDirectoryRegion(), directoryEntry);
		} else {
			int sectorIdx = DATA_REGION_START + directoryEntry.getAtCluster() * PER_CLUSTER_SECTOR;
			// 17 * 32 = 544
			int i = directoryEntry.getIndex() * DIRECTORY_ENTRY_SIZE;
			// 544 / 512 = 1
			int sectorOffset = i / PER_SECTOR_BYTES;
			// 544 % 512 = 32
			int sectorOffsetBeginIndex = i % PER_SECTOR_BYTES;
			// 从第AtCluster个集群，偏移对应的sector数量，再偏移对应的entry数量
			iDisk.appendWriteSector(sectorIdx + sectorOffset, directoryEntry.getData(), sectorOffsetBeginIndex);
		}
	}

	/**
	 * 找到目录项 根据路径，找到对应的目录entry
	 *
	 * @param filePath 文件路径
	 * @return {@link DirectoryEntry}
	 */
	@Override
	public DirectoryEntry findDirectoryEntry(String filePath) {
		String[] splitPath = filePath.split(PATH_SPLIT);
		if (splitPath.length == 0) {
			DirectoryEntry directoryEntry = new DirectoryEntry();
			directoryEntry.setRootEntry(true);
			return directoryEntry;
		} else if (splitPath.length == 2) {
			return findRootDirectoryEntry(filePath);
		} else {
			DirectoryEntry result = null;
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(filePath);
			if (rootDirectoryEntry == null || rootDirectoryEntry.getStartingCluster() == 0) {
				return null;
			}
			int startingCluster = rootDirectoryEntry.getStartingCluster();
			int index = 2;
			while (index < splitPath.length) {
				List<DirectoryEntry> directoryEntries = getAllDirectoryEntry(rootDirectoryEntry);
				int offset = 0;
				for (DirectoryEntry directoryEntry : directoryEntries) {
					if (directoryEntry.getWholeFileName().equals(splitPath[index])) {
						if (index == splitPath.length - 1) {
							directoryEntry.setIndex(offset);
							directoryEntry.setAtCluster(startingCluster);
							result = directoryEntry;
						}
						startingCluster = directoryEntry.getStartingCluster();
					}
					offset++;
				}
				index++;
			}
			return result;
		}
	}

	/**
	 * 读取entry对应的内容数据
	 *
	 * @param directoryEntry 目录条目
	 * @return {@link FileSystemResult}
	 */
	@Override
	public FileSystemResult readFile(DirectoryEntry directoryEntry) {
		//文件之前未写入过内容
		if (directoryEntry.getStartingCluster() == 0) {
			return FileSystemResult.success("");
		} else {
			int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(directoryEntry.getStartingCluster());
			byte[] dataBytes = new byte[(int) directoryEntry.getFileSize()];
			dataRegionService.getClustersData(index, dataBytes);
			directoryEntry.setLastAccessTimeStamp();
			return FileSystemResult.success(new String(dataBytes));
		}
	}

	@Override
	public FileSystemResult writeFile(DirectoryEntry directoryEntry, byte[] dataBytes) {
		directoryEntry = initDirectoryEntryCluster(directoryEntry);
		int endOfFileCluster = fat16xFileSystem.getFatRegion().endOfFileCluster(directoryEntry.getStartingCluster());
		//集群剩余空间
		long endOfFileClusterUsedSpace = directoryEntry.getFileSize() == 0 ? 0 : (
			directoryEntry.getFileSize() % (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR) == 0 ? PER_SECTOR_BYTES * PER_CLUSTER_SECTOR
				: directoryEntry.getFileSize() % (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR)
		);
		int endOfFileClusterRemainSpace = (int) (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR - endOfFileClusterUsedSpace);
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
			int[] fatArray = fatRegionService.allocateFatArray(dataBytes.length - endOfFileClusterRemainSpace, fat16xFileSystem.getFatRegion());
			//保存数据区域数据
			byte[] saveData = new byte[dataBytes.length - endOfFileClusterRemainSpace];
			System.arraycopy(dataBytes, endOfFileClusterRemainSpace, saveData, 0, dataBytes.length - endOfFileClusterRemainSpace);
			dataRegionService.saveFile(saveData, fatArray);
			//原有链路的末尾，指向新申请的链表首部
			fatRegionService.save(endOfFileCluster, String.format("%04x", fatArray[0]), fat16xFileSystem.getFatRegion());
		}
		//更新文件大小,时间
		directoryEntry.updateWriteInfo(directoryEntry.getFileSize() + dataBytes.length);
		persistDirectoryEntry(directoryEntry);
		return FileSystemResult.success();
	}

}
