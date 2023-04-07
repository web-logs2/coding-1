package com.ke.coding.service.stream.input;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/6 17:38
 * @description:
 */
public interface InputStream {

	/**
	 * 读
	 *
	 * @param data 数据
	 * @return int
	 */
	int read(byte[] data);
}
