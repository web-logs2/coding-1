package com.ke.coding.api.dto.filesystem.fat16x.dataregion;

import static com.ke.coding.common.ArrayUtils.array2List;
import static com.ke.coding.common.ArrayUtils.list2Ary;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 20:12
 * @description: 数据集群
 */
@Data
public class DataCluster {

	/**
	 * 默认有64个扇区
	 */
	private DataSector[] sectors = new DataSector[64];

	/**
	 * 首次集群保存数据，可以快速赋值替换
	 *
	 * @param data 数据
	 */
	public void save(byte[] data) {
		List<List<Byte>> lists = Lists.partition(array2List(data), 512);
		for (int i = 0; i < lists.size(); i++) {
			sectors[i] = new DataSector();
			sectors[i].save(list2Ary(lists.get(i)));
		}
	}
}
