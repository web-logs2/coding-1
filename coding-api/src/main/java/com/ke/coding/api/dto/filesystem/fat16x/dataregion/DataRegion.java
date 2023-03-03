package com.ke.coding.api.dto.filesystem.fat16x.dataregion;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 10:57
 * @description: 文件存储区域
 */
@Data
public class DataRegion {

	/**
	 * 集群：默认有65516个，与fat表中的数量一致
	 */
	private DataCluster[] clusters = new DataCluster[65516];

	/**
	 * 从集群尾保存数据，并返回所有的集群id；
	 * 只有存储目录信息时，才会追加保存；
	 * 如果文件的存储，应该直接从cluster头部存
	 * 目录存储: 当前cluster空间不满足时，最多跨一个cluster
	 *
	 * @param data             数据
	 * @param endOfFileCluster 集群文件结束
	 * @return {@link int[]}
	 */
	public int saveDir(byte[] data, int endOfFileCluster, Fat16xFileSystem fat16xFileSystem) {
		DataCluster cluster = clusters[endOfFileCluster];
		//先看一下最后一个节点的空间是否满足
		if (!cluster.appendSave(data)) {
			int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
			clusters[firstFreeFat].save(data);
			return firstFreeFat;
		}
		return -1;
	}
}
