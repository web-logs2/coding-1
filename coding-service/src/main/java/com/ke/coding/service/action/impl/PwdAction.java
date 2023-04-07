package com.ke.coding.service.action.impl;

import com.ke.coding.service.action.AbstractAction;
import java.nio.charset.StandardCharsets;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/7 11:38
 * @description:
 */
public class PwdAction extends AbstractAction {

	/**
	 * 运行
	 */
	@Override
	public void run() {
		out.output(currentPath.getBytes(StandardCharsets.UTF_8));
	}
}
