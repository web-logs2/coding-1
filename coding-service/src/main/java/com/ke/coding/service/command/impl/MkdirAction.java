package com.ke.coding.service.command.impl;

import static com.ke.coding.api.enums.Constants.O_EXLOCK;
import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_ERROR;
import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_LENGTH_TOO_LONG;
import static com.ke.coding.api.enums.ErrorCodeEnum.SYSTEM_SUCCESS;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.api.exception.CodingException;
import com.ke.coding.service.command.AbstractAction;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
public class MkdirAction extends AbstractAction {

	@SneakyThrows
	@Override
	public void run() {
		byte[] input = readIn();
		String originData = new String(input);
		String[] s1 = originData.split(" ");
		if (s1.length != 2) {
			err.write(ACTION_ERROR.message().getBytes(StandardCharsets.UTF_8));
		}

		//step: 文件名，文件后缀长度限制
		String newDir = s1[1];
		Fat16Fd fd = null;
		try {
			String filePathName = buildFilePathName(newDir);
			fd = fileSystemService.open(filePathName, O_EXLOCK);
			if (fd == null || fd.isEmpty()) {
				fileSystemService.mkdir(filePathName, true);
			}
			out.write(SYSTEM_SUCCESS.message().getBytes(StandardCharsets.UTF_8));
		} catch (CodingException e) {
			err.write(e.getErrorCode().message().getBytes(StandardCharsets.UTF_8));
		}finally {
			fileSystemService.close(fd);
		}
	}
}
