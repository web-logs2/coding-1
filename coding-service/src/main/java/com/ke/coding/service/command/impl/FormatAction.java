package com.ke.coding.service.command.impl;

import com.ke.coding.service.command.AbstractAction;
import com.ke.coding.service.disk.FileDisk;
import com.ke.coding.service.disk.IDisk;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
public class FormatAction extends AbstractAction {

	IDisk iDisk;

	/**
	 * 运行
	 */
	@Override
	public void run() {
		iDisk = new FileDisk(StringUtils.isEmpty(System.getProperty("filePath")) ? "123" : System.getProperty("filePath"));
		iDisk.format();
	}
}
