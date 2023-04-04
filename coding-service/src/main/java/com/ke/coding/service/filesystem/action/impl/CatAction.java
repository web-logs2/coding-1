package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.Fd;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
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
	public FileSystemResult run(Command command) {
		String currentPath = command.getCurrentPath();
		String fileName = command.getParams().get(0);
		currentPath = currentPath.equals(ROOT_PATH) ? currentPath + fileName : currentPath + PATH_SPLIT + fileName;
		Fd open = fileSystemService.open(currentPath);
		if (open.isEmpty()) {
			return FileSystemResult.fail(ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY);
		}

		return FileSystemResult.success(new String(fileSystemService.readFile(open)));
	}
}
