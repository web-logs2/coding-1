package com.ke.coding.service.filesystem;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 11:40
 * @description: 文件系统
 */
public interface FileSystem {

	/**
	 * mkdir
	 *
	 * @param command 命令
	 */
	FileSystemActionResult mkdir(Command command);
}
