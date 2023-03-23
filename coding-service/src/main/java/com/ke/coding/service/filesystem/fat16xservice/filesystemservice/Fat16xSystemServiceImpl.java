package com.ke.coding.service.filesystem.fat16xservice.filesystemservice;

import static com.ke.coding.api.enums.Constants.BOOT_SECTOR_SIZE;
import static com.ke.coding.api.enums.Constants.BOOT_SECTOR_START;
import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.FAT_SIZE;
import static com.ke.coding.api.enums.Constants.FAT_START;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_DIRECTORY_SIZE;
import static com.ke.coding.api.enums.Constants.ROOT_DIRECTORY_START;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.common.ArrayUtils.array2List;
import static com.ke.coding.common.ArrayUtils.list2Ary;

import com.google.common.collect.Lists;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
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
	public FatRegionService fatRegionService;

	@Autowired
	public RootDirectoryRegionService rootDirectoryRegionService;

	@Autowired
	IDisk iDisk;

	/**
	 * 初始化文件系统
	 */
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
				break;
			}
		}
		return directoryEntry;
	}

	/**
	 * 构建所有目录条目
	 *
	 * @param startingCluster 从集群
	 * @return {@link List}<{@link DirectoryEntry}>
	 */
	@Override
	public List<DirectoryEntry> getAllDirectoryEntry(int startingCluster) {
		if (startingCluster == 0) {
			return new ArrayList<>();
		}
		List<DirectoryEntry> directoryEntries = new ArrayList<>();
		//找到对应的全部目录data cluster
		int[] dataIndex = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(startingCluster);
		for (int index : dataIndex) {
			byte[] allData = dataClusterService.getClusterData(index);
			List<List<Byte>> lists = Lists.partition(array2List(allData), DIRECTORY_ENTRY_SIZE);
			for (List<Byte> bytes : lists) {
				if (ArrayUtils.isEmpty(bytes)) {
					break;
				} else {
					DirectoryEntry directoryEntry = new DirectoryEntry();
					System.arraycopy(list2Ary(bytes), 0, directoryEntry.getData(), 0, DIRECTORY_ENTRY_SIZE);
					directoryEntries.add(directoryEntry);
				}
			}
		}
		return directoryEntries;
	}

	@Override
	public FileSystemActionResult saveDir(String currentPath, String fileName) {
		DirectoryEntry newDirectoryEntry = DirectoryEntry.buildDir(fileName);
		//RootDirectoryRegion保存
		if (ROOT_PATH.equals(currentPath)) {
			rootDirectoryRegionService.save(fat16xFileSystem.getRootDirectoryRegion(), fat16xFileSystem.getRootDirectoryRegion().freeIndex(),
				newDirectoryEntry);
		} else {
			//数据区域数据保存
			DirectoryEntry directoryEntry = findDirectoryEntry(currentPath);
			initDirectoryEntry(directoryEntry);
			int newEndOfFileCluster = dataRegionService.saveDir(newDirectoryEntry.getData(), directoryEntry.getStartingCluster(), fat16xFileSystem);
			fatRegionService.relink(directoryEntry.getStartingCluster(), newEndOfFileCluster, fat16xFileSystem.getFatRegion());
		}
		return FileSystemActionResult.success();
	}

	void initDirectoryEntry(DirectoryEntry directoryEntry) {
		if (directoryEntry.getStartingCluster() == 0) {
			int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
			directoryEntry.setStartingCluster(firstFreeFat);
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
		if (splitPath.length == 2) {
			return findRootDirectoryEntry(filePath);
		} else {
			DirectoryEntry result = null;
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(filePath);
			int startingCluster = rootDirectoryEntry.getStartingCluster();
			int index = 1;
			while (index < splitPath.length) {
				List<DirectoryEntry> directoryEntries = getAllDirectoryEntry(startingCluster);
				int offset = 0;
				for (DirectoryEntry directoryEntry : directoryEntries) {
					offset++;
					if (directoryEntry.getWholeFileName().equals(splitPath[index])) {
						startingCluster = directoryEntry.getStartingCluster();
						if (index == splitPath.length - 1) {
							directoryEntry.setIndex(offset);
							result = directoryEntry;
						}
					}
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
	 * @return {@link FileSystemActionResult}
	 */
	@Override
	public FileSystemActionResult readFile(DirectoryEntry directoryEntry) {
		//文件之前未写入过内容
		if (directoryEntry.getStartingCluster() == 0) {
			return FileSystemActionResult.success("");
		} else {
			int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(directoryEntry.getStartingCluster());
			byte[] dataBytes = new byte[(int) directoryEntry.getFileSize()];
			dataRegionService.getClustersData(index, dataBytes);
			directoryEntry.setLastAccessTimeStamp();
			return FileSystemActionResult.success(new String(dataBytes));
		}
	}

}
