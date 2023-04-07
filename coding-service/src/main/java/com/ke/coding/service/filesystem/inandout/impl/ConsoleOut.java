package com.ke.coding.service.filesystem.inandout.impl;

import com.ke.coding.service.stream.output.ConsoleOutputStream;
import com.ke.coding.service.stream.output.OutputStream;
import com.ke.coding.service.filesystem.inandout.Out;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/6 16:46
 * @description:
 */
public class ConsoleOut implements Out {

	private final OutputStream outputStream = new ConsoleOutputStream();

	/**
	 * 输出
	 *
	 * @param data 数据
	 */
	@Override
	public void output(byte[] data) {
		outputStream.write(data);
	}
}
