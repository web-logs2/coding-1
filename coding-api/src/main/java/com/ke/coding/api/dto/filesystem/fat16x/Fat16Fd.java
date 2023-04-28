package com.ke.coding.api.dto.filesystem.fat16x;

import com.ke.coding.api.dto.filesystem.Fd;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import java.io.Closeable;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/3 18:30
 * @description:
 */
@Data
public class Fat16Fd extends Fd {

	private String filePathName;

	private DirectoryEntry directoryEntry;

	@Override
	public boolean isEmpty() {
		return directoryEntry == null;
	}


	public Fat16Fd(DirectoryEntry directoryEntry, String fileName, long fileSize, String accessDate) {
		super(fileName, fileSize, accessDate);
		this.directoryEntry = directoryEntry;
	}

	public Fat16Fd(DirectoryEntry directoryEntry, String filePathName) {
		this.directoryEntry = directoryEntry;
		this.filePathName = filePathName;
	}

	public Fat16Fd(int startingCluster) {
		directoryEntry = new DirectoryEntry();
		directoryEntry.setStartingCluster(startingCluster);
	}

	public Fat16Fd(String filePathName) {
		this.filePathName = filePathName;
	}

}
