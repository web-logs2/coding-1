package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.FILENAME_LENGTH_TOO_LONG;
import static com.ke.coding.api.enums.ErrorCodeEnum.INSUFFICIENT_SPACE;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
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
	public FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem) {
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
			return FileSystemActionResult.fail(FILENAME_LENGTH_TOO_LONG);
		}
		//step: 当前路径是根目录，需要判断根目录区域空间是否充足
		String currentPath = command.getCurrentPath();
		if (ROOT_PATH.equals(currentPath) && !hasIdleRootDirectorySpace(fat16xFileSystem)) {
			return FileSystemActionResult.fail(INSUFFICIENT_SPACE);
		}
		fileSystemService.saveDir(currentPath, wholeFileName);
		return FileSystemActionResult.success();
	}
}
