package com.ke.coding.api.dto.filesystem.fat16x;

import com.ke.coding.api.dto.filesystem.fat16x.bootregion.BootSector;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataRegion;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.RootDirectoryRegion;
import com.ke.coding.api.dto.filesystem.fat16x.fatregion.FatRegion;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 17:33
 * @description:
 */
@Data
public class Fat16xFileSystem {


	/**
	 * 启动分区配置信息
	 */
	private BootSector reservedRegion;

	/**
	 * fat区域
	 */
	private FatRegion fatRegion;

	/**
	 * 目录区域
	 */
	private RootDirectoryRegion rootDirectoryRegion;

	/**
	 * 数据区域
	 */
	private DataRegion dataRegion;


	public Fat16xFileSystem() {
	}

	public void format(){
		fatRegion.format();
		rootDirectoryRegion.format();
		dataRegion.format();
	}
}
