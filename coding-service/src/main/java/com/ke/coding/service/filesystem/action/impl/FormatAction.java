package com.ke.coding.service.filesystem.action.impl;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.service.disk.IDisk;
import com.ke.coding.service.filesystem.action.AbstractAction;
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
	IDisk iDisk;

	@Override
	public FileSystemResult run(Command command) {
		iDisk.format();
		fileSystemService.init();
		return FileSystemResult.success("format success");
	}
}
