package com.ke.coding.service.filesystem.fat16xservice.regionservice;

import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.ROOT_DIRECTORY_START;

import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.RootDirectoryRegion;
import com.ke.coding.service.disk.IDisk;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/14 12:10
 * @description:
 */
public class RootDirectoryRegionService {

	public RootDirectoryRegionService(IDisk disk) {
		this.disk = disk;
	}

	IDisk disk;

	public void save(RootDirectoryRegion rootDirectoryRegion, int index, DirectoryEntry directoryEntry) {
		rootDirectoryRegion.getDirectoryEntries()[index] = directoryEntry;
		int sectorIndex = (index * DIRECTORY_ENTRY_SIZE) / disk.sectorSize();
		int sectorDataSize = (index * DIRECTORY_ENTRY_SIZE) % disk.sectorSize();
		byte[] bytes = disk.readSector(ROOT_DIRECTORY_START + sectorIndex);
		System.arraycopy(directoryEntry.getData(), 0, bytes, sectorDataSize, DIRECTORY_ENTRY_SIZE);
		disk.writeSector(ROOT_DIRECTORY_START + sectorIndex, bytes);
	}

	public void update(RootDirectoryRegion rootDirectoryRegion, DirectoryEntry directoryEntry) {
		rootDirectoryRegion.getDirectoryEntries()[directoryEntry.getIndex()] = directoryEntry;
		int sectorIndex = (directoryEntry.getIndex() * DIRECTORY_ENTRY_SIZE) / disk.sectorSize();
		int sectorDataSize = (directoryEntry.getIndex() * DIRECTORY_ENTRY_SIZE) % disk.sectorSize();
		byte[] bytes = disk.readSector(ROOT_DIRECTORY_START + sectorIndex);
		System.arraycopy(directoryEntry.getData(), 0, bytes, sectorDataSize, DIRECTORY_ENTRY_SIZE);
		disk.writeSector(ROOT_DIRECTORY_START + sectorIndex, bytes);
	}

	public void rm(RootDirectoryRegion rootDirectoryRegion, DirectoryEntry directoryEntry){
		rootDirectoryRegion.getDirectoryEntries()[directoryEntry.getIndex()] = null;
		int sectorIndex = (directoryEntry.getIndex() * DIRECTORY_ENTRY_SIZE) / disk.sectorSize();
		int sectorDataSize = (directoryEntry.getIndex() * DIRECTORY_ENTRY_SIZE) % disk.sectorSize();
		byte[] bytes = disk.readSector(ROOT_DIRECTORY_START + sectorIndex);
		System.arraycopy(new byte[DIRECTORY_ENTRY_SIZE], 0, bytes, sectorDataSize, DIRECTORY_ENTRY_SIZE);
		disk.writeSector(ROOT_DIRECTORY_START + sectorIndex, bytes);
	}

}
