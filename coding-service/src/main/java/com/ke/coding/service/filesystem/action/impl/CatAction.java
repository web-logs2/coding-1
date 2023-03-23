package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.api.enums.ErrorCodeEnum;
import com.ke.coding.service.filesystem.action.AbstractAction;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class CatAction extends AbstractAction {

	@Override
	public FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem) {
		String currentPath = command.getCurrentPath();
		String fileName = command.getParams().get(0);
		currentPath = currentPath.equals(ROOT_PATH) ? currentPath + fileName : currentPath + PATH_SPLIT + fileName;
		DirectoryEntry directoryEntry = fileSystemService.findDirectoryEntry(currentPath);
		if (directoryEntry == null) {
			return FileSystemActionResult.fail(ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY);
		}
		return fileSystemService.readFile(directoryEntry);
	}
}
