package com.ke.coding.service.command.impl;

import static com.ke.coding.api.enums.Constants.O_SHLOCK;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_ERROR;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.api.enums.ErrorCodeEnum;
import com.ke.coding.service.command.AbstractAction;
import com.ke.coding.service.stream.input.Fat16InputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
public class CatAction extends AbstractAction {

	@SneakyThrows
	@Override
	public void run() {
		byte[] input = readIn();
		String originData = new String(input);
		String[] s1 = originData.split(" ");
		if (s1.length != 2) {
			err.write(ACTION_ERROR.message().getBytes(StandardCharsets.UTF_8));
			return;
		}
		String fileName = s1[1];
		String filePath = currentPath.equals(ROOT_PATH) ? currentPath + fileName : currentPath + PATH_SPLIT + fileName;
		Fat16Fd fd = fileSystemService.open(filePath, O_SHLOCK);
		if (fd.isEmpty()) {
			err.write(ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY.message().getBytes(StandardCharsets.UTF_8));
			return;
		}
		InputStream inputStream = new Fat16InputStream(fd);
		byte[] data = new byte[1024];
		int read = inputStream.read(data);
		while (-1 != read) {
			byte[] temp = new byte[read];
			System.arraycopy(data, 0, temp, 0, read);
			out.write(temp);
			read = inputStream.read(data);
		}
		fileSystemService.close(fd);
	}

}
