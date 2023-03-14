package com.ke.coding.service.filesystem.action.impl;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.service.disk.IDisk;
import com.ke.coding.service.filesystem.action.AbstractAction;
import com.ke.coding.service.filesystem.fat16xservice.FileSystem;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xFileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class FormatAction extends AbstractAction {

	@Autowired
	Fat16xFileSystemService fileSystem;

	@Autowired
	IDisk iDisk;

	@Override
	public FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem) {
		iDisk.format();
		fileSystem.init();
		return FileSystemActionResult.success("format success");
	}
}
