package com.ke.coding.service.filesystem.fat16xservice.filesystemservice;

import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import java.util.List;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/23 16:41
 * @description:
 */
public interface FileSystemService {

	DirectoryEntry findDirectoryEntry(String filePath);

	FileSystemResult readFile(DirectoryEntry directoryEntry);

	FileSystemResult writeFile(DirectoryEntry directoryEntry, byte[] data);

	List<DirectoryEntry> getAllDirectoryEntry(DirectoryEntry directoryEntry);

	DirectoryEntry saveDir(String currentPath, String fileName, boolean dir);

	void init();
}
