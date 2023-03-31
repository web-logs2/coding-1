package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.ErrorCodeEnum.FILENAME_LENGTH_TOO_LONG;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.service.filesystem.action.AbstractAction;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class TouchAction extends AbstractAction {

	@Override
	public FileSystemResult run(Command command) {
		//step: 文件名，文件名后缀长度限制
		String wholeFileName = command.getParams().get(0);
		String fileName = "";
		String fileNameExtension = "";
		if (wholeFileName.contains(".")) {
			String[] split = wholeFileName.split("\\.");
			fileName = split[0];
			fileNameExtension = split[1];
		}
		if (fileName.length() > 8 || fileNameExtension.length() > 3) {
			return FileSystemResult.fail(FILENAME_LENGTH_TOO_LONG);
		}
		String currentPath = command.getCurrentPath();
		fileSystemService.saveDir(currentPath, wholeFileName, false);
		return FileSystemResult.success();
	}
}
