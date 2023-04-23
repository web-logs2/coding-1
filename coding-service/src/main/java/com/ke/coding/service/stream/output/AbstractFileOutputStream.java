package com.ke.coding.service.stream.output;

import com.ke.coding.api.dto.filesystem.Fd;
import java.io.OutputStream;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/6 17:41
 * @description:
 */
public abstract class AbstractFileOutputStream<F extends Fd> extends OutputStream {

	AbstractFileOutputStream(F fd) {
		this.fd = fd;
	}

	public F fd;
}
