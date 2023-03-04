package com.ke.coding.api.dto.filesystem.fat16x.dataregion;

import static com.ke.coding.common.ArrayUtils.list2Ary;
import static com.ke.coding.common.ArrayUtils.splitAry;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import java.util.List;
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

	public DataCluster[] findClusters(int[] index){
		DataCluster[] result = new DataCluster[index.length];
		for (int i = 0; i < index.length; i++) {
			result[i] = clusters[index[i]];
		}
		return result;
	}
	/**
	 * 从集群尾保存数据，并返回所有的集群id；
	 * 如果文件的存储，应该直接从cluster头部存
	 * 目录存储: 当前cluster空间不满足时，最多跨一个cluster
	 *
	 * @param data             数据
	 * @param fileCluster 集群文件结束
	 * @return {@link int[]}
	 */
	public int saveDir(byte[] data, int fileCluster, Fat16xFileSystem fat16xFileSystem) {
		if (clusters[fileCluster] == null) {
			clusters[fileCluster] = new DataCluster();
		}
		//先看一下当前节点的剩余空间是否满足
		if (clusters[fileCluster].appendSave(data)){
			return fileCluster;
		}else {
			int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
			clusters[firstFreeFat] = new DataCluster();
			clusters[firstFreeFat].save(data);
			return firstFreeFat;
		}
	}

	/**
	 * 保存文件数据
	 *
	 * @param data             数据
	 * @param fatArray         脂肪数组
	 */
	public void saveFile(byte[] data, int[] fatArray) {
		//把数据分割为fatArray组，
		List<List<Byte>> lists = splitAry(data, fatArray.length);
		for (int i = 0; i < fatArray.length; i++) {
			if (clusters[fatArray[i]] == null) {
				clusters[fatArray[i]] = new DataCluster();
			}
			clusters[fatArray[i]].save(list2Ary(lists.get(i)));
		}
	}

	/**
	 * 添加保存文件 当前数据节点追加数据
	 *
	 * @param data             数据
	 * @param dataClusterIndex 数据集群索引
	 */
	public void appendSaveFile(byte[] data, int dataClusterIndex) {
		clusters[dataClusterIndex].appendSave(data);
	}
}
