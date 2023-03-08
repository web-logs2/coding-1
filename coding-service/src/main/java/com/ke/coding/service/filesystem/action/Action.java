package com.ke.coding.service.filesystem.action;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;

/**
 * 行动
 *
 * @author xueyunlong
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:35
 * @description:
 * @date 2023/03/07
 */
public interface Action {
	FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem);
}
