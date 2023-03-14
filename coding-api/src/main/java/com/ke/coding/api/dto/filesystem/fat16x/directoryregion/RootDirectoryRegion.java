package com.ke.coding.api.dto.filesystem.fat16x.directoryregion;

import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;

import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 19:55
 * @description:
 */
@Data
public class RootDirectoryRegion {

	private static final int MAX_SIZE = 512;

	DirectoryEntry[] directoryEntries;

	public boolean haveIdleSpace() {
		return freeIndex() < MAX_SIZE;
	}

	public int freeIndex() {
		int index;
		for (int i = 0; i < directoryEntries.length; i++) {
			if (directoryEntries[i] == null) {
				index = i;
				return index;
			}
		}
		return -1;
	}

	public void format() {
		directoryEntries = new DirectoryEntry[MAX_SIZE];
	}

	public RootDirectoryRegion(byte[] data) {
		directoryEntries = new DirectoryEntry[MAX_SIZE];
		for (int i = 0; i < directoryEntries.length; i++) {
			byte[] temp = new byte[DIRECTORY_ENTRY_SIZE];
			System.arraycopy(data, i * DIRECTORY_ENTRY_SIZE, temp, 0, DIRECTORY_ENTRY_SIZE);
			if (isEmpty(temp)){
				break;
			}
			directoryEntries[i] = new DirectoryEntry(temp);
		}
	}

	public static boolean isEmpty(byte[] data) {
		if (data == null) {
			return true;
		} else {
			boolean empty = true;
			for (byte datum : data) {
				if (datum != 0) {
					empty = false;
					break;
				}
			}
			return empty;
		}
	}
}
