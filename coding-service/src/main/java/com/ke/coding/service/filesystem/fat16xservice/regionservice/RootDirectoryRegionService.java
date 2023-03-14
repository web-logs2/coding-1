package com.ke.coding.service.filesystem.fat16xservice.regionservice;

import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.ROOT_DIRECTORY_START;

import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.RootDirectoryRegion;
import com.ke.coding.service.disk.IDisk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/14 12:10
 * @description:
 */
@Service
public class RootDirectoryRegionService {

	@Autowired
	IDisk disk;

	public void save(RootDirectoryRegion rootDirectoryRegion, int index, DirectoryEntry directoryEntry){
		rootDirectoryRegion.getDirectoryEntries()[index] = directoryEntry;
		int sectorIndex = (index * DIRECTORY_ENTRY_SIZE) / disk.sectorSize();
		int sectorDataSize = (index * DIRECTORY_ENTRY_SIZE) % disk.sectorSize();
		byte[] bytes = disk.readSector(ROOT_DIRECTORY_START + sectorIndex);
		System.arraycopy(bytes, sectorDataSize, directoryEntry.getData(), 0, DIRECTORY_ENTRY_SIZE);
		disk.writeSector(ROOT_DIRECTORY_START + sectorIndex, bytes);
	}

}
