package com.ke.coding.service.stream.output;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/6 17:42
 * @description:
 */
public class ConsoleOutputStream implements OutputStream {

	/**
	 * 写
	 *
	 * @param data 数据
	 */
	@Override
	public void write(byte[] data) {
		System.out.println(new String(data));
	}
}
