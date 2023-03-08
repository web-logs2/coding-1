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

	DirectoryEntry[] directoryEntries = new DirectoryEntry[MAX_SIZE];

	public boolean haveIdleSpace() {
		return freeIndex() < MAX_SIZE;
	}

	public int freeIndex(){
		int index;
		for (int i = 0; i < directoryEntries.length; i++) {
			if (directoryEntries[i] == null){
				index = i;
				return index;
			}
		}
		return -1;
	}

	public void format(){
		directoryEntries = new DirectoryEntry[MAX_SIZE];
	}
}
