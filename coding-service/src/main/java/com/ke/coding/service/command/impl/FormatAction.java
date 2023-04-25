package com.ke.coding.service.command.impl;

import com.ke.coding.service.command.AbstractAction;
import com.ke.coding.service.disk.FileDisk;
import com.ke.coding.service.disk.IDisk;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
public class FormatAction extends AbstractAction {

	IDisk iDisk = new FileDisk();

	/**
	 * 运行
	 */
	@Override
	public void run() {
		iDisk.format();
	}
}
