package com.ke.coding.service.stream.output;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.api.enums.ErrorCodeEnum;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/6 17:37
 * @description:
 */
public class Fat16xAndResponseOutputStream extends Fat16xOutputStream {

	private OutputStream output;

	public Fat16xAndResponseOutputStream(Fat16Fd fd, OutputStream output) {
		super(fd);
		this.output = output;
	}

	@Override
	public void write(int b) throws IOException {

	}

	/**
	 * 写
	 *
	 * @param data 数据
	 */
	@SneakyThrows
	@Override
	public void write(byte[] data) {
		super.write(data);
		output.write(ErrorCodeEnum.SYSTEM_SUCCESS.message().getBytes(StandardCharsets.UTF_8));
	}
}
