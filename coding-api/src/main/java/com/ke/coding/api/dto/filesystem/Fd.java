package com.ke.coding.api.dto.filesystem;

import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/3 18:18
 * @description:
 */
@Data
public abstract class Fd {
	private String fileName;
	private long fileSize;
	private String accessDate;
	private int pos;
	public abstract boolean isEmpty();

	protected Fd(String fileName, long fileSize, String accessDate) {
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.accessDate = accessDate;
	}

	protected Fd() {
	}
}
