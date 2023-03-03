package com.ke.coding.api.dto.filesystem.fat16x.directoryregion;

import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 19:55
 * @description:
 */
@Data
public class RootDirectoryRegion {

	private static final int MAX_SIZE = 512;

	private int usedSize = 0;

	DirectoryEntry[] directoryEntries = new DirectoryEntry[MAX_SIZE];

	public boolean haveIdleSpace() {
		return usedSize < MAX_SIZE;
	}
}
