package com.ke.coding.service.action.impl;

import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_ERROR;
import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_LENGTH_TOO_LONG;
import static com.ke.coding.api.enums.ErrorCodeEnum.SYSTEM_SUCCESS;

import com.ke.coding.api.exception.CodingException;
import com.ke.coding.service.action.AbstractAction;
import java.nio.charset.StandardCharsets;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
public class MkdirAction extends AbstractAction {

	@Override
	public void run() {
		byte[] input = in.getInput();
		String originData = new String(input);
		String[] s1 = originData.split(" ");
		if (s1.length != 2) {
			err.err(ACTION_ERROR.message().getBytes(StandardCharsets.UTF_8));
		}

		//step: 文件名，文件后缀长度限制
		String newDir = s1[1];
		if (newDir.length() > 8) {
			err.err(DIR_LENGTH_TOO_LONG.message().getBytes(StandardCharsets.UTF_8));
		}
		try {
			fileSystemService.mkdir(currentPath, newDir, true);
		} catch (CodingException e) {
			err.err(e.getErrorCode().message().getBytes(StandardCharsets.UTF_8));
		}
		out.output(SYSTEM_SUCCESS.message().getBytes(StandardCharsets.UTF_8));
	}
}
