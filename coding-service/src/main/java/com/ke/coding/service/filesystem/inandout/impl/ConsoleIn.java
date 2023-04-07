package com.ke.coding.service.filesystem.inandout.impl;

import com.ke.coding.service.filesystem.inandout.In;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/6 16:46
 * @description:
 */
public class ConsoleIn implements In {

	public ConsoleIn(byte[] data) {
		this.data = data;
	}

	byte[] data;

	/**
	 * 得到输入
	 *
	 * @return {@link byte[]}
	 */
	@Override
	public byte[] getInput() {
		return data;
	}
}
