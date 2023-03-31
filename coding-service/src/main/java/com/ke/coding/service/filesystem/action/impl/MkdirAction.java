package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_LENGTH_TOO_LONG;

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
public class MkdirAction extends AbstractAction {

	@Override
	public FileSystemResult run(Command command) {
		//step: 文件名，文件后缀长度限制
		String newDir = command.getParams().get(0);
		if (newDir.length() > 8) {
			return FileSystemResult.fail(DIR_LENGTH_TOO_LONG);
		}
		//step: 当前路径是根目录，需要判断根目录区域空间是否充足
		String currentPath = command.getCurrentPath();
		fileSystemService.saveDir(currentPath, newDir, true);
		return FileSystemResult.success();
	}
}
