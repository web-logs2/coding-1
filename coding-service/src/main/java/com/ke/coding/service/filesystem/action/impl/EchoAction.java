package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.FILENAME_LENGTH_TOO_LONG;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.service.filesystem.action.AbstractAction;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class EchoAction extends AbstractAction {

	@Override
	public FileSystemResult run(Command command) {
		String currentPath = command.getCurrentPath();
		String wholeFileName = command.getParams().get(2);
		String fileName = wholeFileName;
		String fileNameExtension = "";
		if (wholeFileName.contains(".")) {
			String[] split = wholeFileName.split("\\.");
			fileName = split[0];
			fileNameExtension = split[1];
		}
		if (fileName.length() > 8 || fileNameExtension.length() > 3) {
			return FileSystemResult.fail(FILENAME_LENGTH_TOO_LONG);
		}
		String pathAndFile = currentPath.equals(ROOT_PATH) ? currentPath + fileName : currentPath + PATH_SPLIT + fileName;
		DirectoryEntry directoryEntry = fileSystemService.findDirectoryEntry(pathAndFile);
		if (directoryEntry == null) {
			directoryEntry = fileSystemService.saveDir(currentPath, wholeFileName, false);
		}
		fileSystemService.writeFile(directoryEntry, command.getParams().get(0).getBytes(StandardCharsets.UTF_8));
		return FileSystemResult.success();
	}


}
