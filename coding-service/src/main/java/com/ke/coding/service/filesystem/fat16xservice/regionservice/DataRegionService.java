package com.ke.coding.service.filesystem.fat16xservice.regionservice;

import static com.ke.coding.api.enums.Constants.PER_CLUSTER_SECTOR;
import static com.ke.coding.api.enums.Constants.PER_SECTOR_BYTES;
import static com.ke.coding.common.ArrayUtils.array2List;
import static com.ke.coding.common.ArrayUtils.list2Ary;

import com.google.common.collect.Lists;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataCluster;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/13 18:37
 * @description:
 */
@Service
public class DataRegionService {

	@Autowired
	DataClusterService clusterService;

	/**
	 * 获取集群数据
	 *
	 * @param index   指数
	 * @param results 结果
	 */
	public void getClustersData(int[] index, byte[] results) {
		for (int i = 0; i < index.length; i++) {
			if (i != index.length - 1) {
				byte[] clusterAllData = clusterService.getClusterData(index[i]);
				System.arraycopy(clusterAllData, 0, results, i * PER_SECTOR_BYTES * PER_CLUSTER_SECTOR, PER_SECTOR_BYTES * PER_CLUSTER_SECTOR);
			} else {
				int endClusterSize = i == 0 ? results.length : results.length % (i * PER_SECTOR_BYTES * PER_CLUSTER_SECTOR);
				byte[] dataBySize = clusterService.getDataBySize(index[i], endClusterSize);
				System.arraycopy(dataBySize, 0, results, i * PER_SECTOR_BYTES * PER_CLUSTER_SECTOR, endClusterSize);
			}
		}
	}


	/**
	 * 从集群尾保存数据，并返回所有的集群id； 如果文件的存储，应该直接从cluster头部存 目录存储: 当前cluster空间不满足时，最多跨一个cluster
	 *
	 * @param data        数据
	 * @param fileCluster 集群文件结束
	 * @return {@link int[]}
	 */
	public int saveDir(byte[] data, int fileCluster, Fat16xFileSystem fat16xFileSystem) {
		//先看一下当前节点的剩余空间是否满足
		if (clusterService.appendSaveDir(fileCluster, data)) {
			return fileCluster;
		} else {
			int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
			clusterService.appendSaveDir(firstFreeFat, data);
			return firstFreeFat;
		}
	}

	public void updateDirInfo(DataCluster dataCluster) {
		clusterService.save(dataCluster.getIndex(), dataCluster.getAllData());
	}

	/**
	 * 保存文件数据
	 *
	 * @param data     数据
	 * @param fatArray 脂肪数组
	 */
	public void saveFile(byte[] data, int[] fatArray) {
		//把数据分割为fatArray组，
		List<List<Byte>> partition = Lists.partition(array2List(data), PER_CLUSTER_SECTOR * PER_SECTOR_BYTES);
		for (int i = 0; i < fatArray.length; i++) {
			clusterService.save(fatArray[i], list2Ary(partition.get(i)));
		}
	}

	/**
	 * 当前数据节点追加数据
	 *
	 * @param data             数据
	 * @param dataClusterIndex 数据集群索引
	 * @param oldFileSize      旧文件大小
	 */
	public void appendSaveFile(byte[] data, int dataClusterIndex, int oldFileSize) {
		clusterService.appendSaveFile(dataClusterIndex, data, oldFileSize);
	}

	public DataCluster[] findClusters(int[] index) {
		return clusterService.findClusters(index);
	}


}
