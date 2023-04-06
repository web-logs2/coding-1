package com.ke.coding.service.filesystem.fat16xservice.filesystemservice.inputstream;

import com.ke.coding.api.dto.filesystem.Fd;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/4 11:30
 * @description:
 */
@Data
public abstract class InputStream<F extends Fd> {

	public F fd;

	abstract int read(byte[] data);
}
