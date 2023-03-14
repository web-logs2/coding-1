package com.ke.coding.api.dto.filesystem.fat16x.bootregion;

import com.ke.coding.api.dto.filesystem.fat16x.Sector;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 17:43
 * @description: 启动分区配置信息
 */
public class BootSector extends Sector {

	private final byte bytesPerSector = (byte) 512;
	private final byte sectorsPerCluster = (byte) 64;

	private byte[] data = new byte[512];

	public BootSector(byte[] data) {
		this.data = data;
	}
}
