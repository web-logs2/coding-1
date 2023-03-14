package com.ke.coding.api.dto.filesystem.fat16x.dataregion;

import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 10:57
 * @description: 文件存储区域
 */
@Data
public class DataRegion {

	public DataRegion() {
	}

	/**
	 * 集群：默认有65516个，与fat表中的数量一致
	 */
	private DataCluster[] clusters = new DataCluster[65519];

	public void format() {
		clusters = new DataCluster[65519];
	}


}
