package com.ke.coding.service.stream.output;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/6 16:16
 * @description:
 */
public interface OutputStream {

	/**
	 * 写
	 *
	 * @param data 数据
	 */
	void write(byte[] data);
}
