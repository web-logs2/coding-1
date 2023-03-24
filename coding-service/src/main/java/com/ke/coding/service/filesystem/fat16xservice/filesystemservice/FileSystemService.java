package com.ke.coding.service.filesystem.fat16xservice.filesystemservice;

import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import java.util.List;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/23 16:41
 * @description:
 */
public interface FileSystemService {

	DirectoryEntry findDirectoryEntry(String filePath);

	FileSystemActionResult readFile(DirectoryEntry directoryEntry);

	List<DirectoryEntry> getAllDirectoryEntry(int startingCluster);

	FileSystemActionResult saveDir(String currentPath, String fileName);

	void init();
}
