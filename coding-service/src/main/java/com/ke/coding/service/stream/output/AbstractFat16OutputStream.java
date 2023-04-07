package com.ke.coding.service.stream.output;

import com.ke.coding.api.dto.filesystem.Fd;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/6 17:41
 * @description:
 */
public abstract class AbstractFat16OutputStream<F extends Fd> implements OutputStream {

	private F fd;
}
