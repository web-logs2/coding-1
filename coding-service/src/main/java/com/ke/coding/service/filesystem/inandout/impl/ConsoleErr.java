package com.ke.coding.service.filesystem.inandout.impl;

import com.ke.coding.service.filesystem.inandout.Err;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/6 16:46
 * @description:
 */
public class ConsoleErr implements Err {

	/**
	 * err
	 *
	 * @param data
	 */
	@Override
	public void err(byte[] data) {
		System.out.println(new String(data));
	}
}
