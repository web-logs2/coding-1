package com.ke.coding.service.filesystem.fat16xservice;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 11:40
 * @description: 文件系统
 */
public interface FileSystem {

	/**
	 * 执行
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	default FileSystemActionResult execute(Command command) {
		return null;
	}

	/**
	 * mkdir
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	default FileSystemActionResult mkdir(Command command) {
		return null;
	}


	/**
	 * touch
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	default FileSystemActionResult touch(Command command) {
		return null;
	}

	/**
	 * ls
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	default FileSystemActionResult ls(Command command) {
		return null;
	}

	/**
	 * echo
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	default FileSystemActionResult echo(Command command) {
		return null;
	}

	/**
	 * cat
	 *
	 * @param command 命令
	 * @return {@link FileSystemActionResult}
	 */
	default FileSystemActionResult cat(Command command) {
		return null;
	}

	default FileSystemActionResult format(Command command) {
		return null;
	}
}
