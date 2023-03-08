package com.ke.coding.api.dto.filesystem.fat16x.directoryregion;

import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/8 11:14
 * @description:
 */
@Data
public class DirectoryEntrySubInfo {

	private String fileName;

	private long fileSize;

	private String accessDate;
}
