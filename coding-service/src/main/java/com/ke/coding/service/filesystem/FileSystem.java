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
	 * @return {@link FileSystemActionResult}
	 */
	FileSystemActionResult mkdir(Command command);


	/**
	 * touch
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	FileSystemActionResult touch(Command command);

	/**
	 * ls
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	FileSystemActionResult ls(Command command);

	/**
	 * echo
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	FileSystemActionResult echo(Command command);
}
