package com.ke.coding.api.dto.filesystem.fat16x;

import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 20:06
 * @description:
 */
@Data
public class Fd {

	private String path;

	/**
	 * 类型: 0 文件 1 目录
	 */
	private byte type;

}
