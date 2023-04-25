package com.ke.coding.service.command.impl;

import com.ke.coding.service.command.AbstractAction;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/7 11:38
 * @description:
 */
public class PwdAction extends AbstractAction {

	/**
	 * 运行
	 */
	@SneakyThrows
	@Override
	public void run() {
		out.write(currentPath.getBytes(StandardCharsets.UTF_8));
	}
}
