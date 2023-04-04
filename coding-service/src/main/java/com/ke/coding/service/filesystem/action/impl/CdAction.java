package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.Fd;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.api.exception.CodingException;
import com.ke.coding.service.filesystem.action.AbstractAction;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class CdAction extends AbstractAction {

	@Override
	public FileSystemResult run(Command command) {
		String currentPath = command.getCurrentPath();
		String cdPath = command.getParams().get(0);
		if (cdPath.contains("..")) {
			String[] cdPathSplit = cdPath.split(PATH_SPLIT);
			String[] currentPathSplit = currentPath.split(PATH_SPLIT);

			String result = "";
			for (int i = 0; i < currentPathSplit.length - cdPathSplit.length; i++) {
				result = ROOT_PATH + currentPathSplit[i];
			}
			return FileSystemResult.success(result);

		} else if (cdPath.equals(ROOT_PATH)) {
			return FileSystemResult.success(ROOT_PATH);
		} else {
			if (!cdPath.startsWith(PATH_SPLIT)) {
				cdPath = currentPath.equals(ROOT_PATH) ? currentPath + cdPath : currentPath + PATH_SPLIT + cdPath;
			}
			Fd open = fileSystemService.open(cdPath);
			if (open.isEmpty()) {
				throw new CodingException(NO_SUCH_FILE_OR_DIRECTORY);
			}
			return FileSystemResult.success(cdPath);
		}
	}
}
