package com.ke.coding.service.command.impl;

import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.enums.ErrorCodeEnum;
import com.ke.coding.service.command.AbstractAction;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
public class FormatAction extends AbstractAction {

	/**
	 * 运行
	 */
	@Override
	@SneakyThrows

	public void run() {
		fileSystemService.format();
		out.write(ErrorCodeEnum.SYSTEM_SUCCESS.message().getBytes(StandardCharsets.UTF_8));
		abstractShell.updateCurrentPath(ROOT_PATH);
	}
}
