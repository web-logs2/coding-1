package com.ke.coding.service.stream.output;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/6 17:42
 * @description:
 */
public class ConsoleOutputStream extends OutputStream {

	@Override
	public void write(int b) throws IOException {

	}

	/**
	 * 写
	 *
	 * @param data 数据
	 */
	@Override
	public void write(byte[] data) {
		System.out.println(new String(data).replace("\\n", "\n"));
	}
}
