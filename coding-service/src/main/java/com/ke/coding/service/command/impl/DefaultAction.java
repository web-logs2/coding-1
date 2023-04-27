package com.ke.coding.service.command.impl;

import com.ke.coding.api.enums.ErrorCodeEnum;
import com.ke.coding.service.command.AbstractAction;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/7 11:38
 * @description:
 */
public class DefaultAction extends AbstractAction {

	/**
	 * 运行
	 */
	@SneakyThrows
	@Override
	public void run() {
		byte[] bytes = readIn();
		String s = new String(bytes);
		if (!StringUtils.isBlank(s)) {
			err.write(ErrorCodeEnum.ACTION_ERROR.message().getBytes(StandardCharsets.UTF_8));
		}
	}
}
