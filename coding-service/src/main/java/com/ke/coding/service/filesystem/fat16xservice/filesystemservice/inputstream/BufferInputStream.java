package com.ke.coding.service.filesystem.fat16xservice.filesystemservice.inputstream;

import com.ke.coding.api.dto.filesystem.Fd;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/4 11:30
 * @description:
 */
@Data
public class BufferInputStream<F extends Fd> {

	public int pos;

	public F fd;

	int read(byte[] data) {
		return -1;
	}
}
