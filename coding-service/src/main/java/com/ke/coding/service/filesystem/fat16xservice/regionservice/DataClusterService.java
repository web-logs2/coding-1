package com.ke.coding.service.filesystem.fat16xservice.regionservice;

import static com.ke.coding.api.enums.Constants.DATA_REGION_START;
import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.PER_CLUSTER_SECTOR;
import static com.ke.coding.api.enums.Constants.PER_SECTOR_BYTES;

import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataCluster;
import com.ke.coding.common.ArrayUtils;
import com.ke.coding.service.disk.IDisk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/13 18:28
 * @description:
 */
@Service
public class DataClusterService {

	@Autowired
	IDisk disk;

	/**
	 * 得到所有数据
	 *
	 * @return {@link byte[]}
	 */
	public byte[] getAllData(int clusterIndex) {
		return disk.readSector(289 + clusterIndex * PER_CLUSTER_SECTOR, PER_SECTOR_BYTES * PER_CLUSTER_SECTOR);
	}

	/**
	 * 获取数据大小 根据大小获取集群数据
	 *
	 * @param size         大小
	 * @param clusterIndex 集群指数
	 * @return {@link byte[]}
	 */
	public byte[] getDataBySize(int clusterIndex, int size) {
		byte[] result = new byte[size];
		byte[] allData = getAllData(clusterIndex);
		System.arraycopy(allData, 0, result, 0, size);
		return result;
	}

	/**
	 * 追加保存，在文件尾追加数据
	 *
	 * @param data 数据
	 * @return boolean
	 */
	public boolean appendSaveDir(int clusterIndex, byte[] data) {
		byte[] allData = getAllData(clusterIndex);
		byte[] temp = new byte[DIRECTORY_ENTRY_SIZE];
		System.arraycopy(allData, allData.length - DIRECTORY_ENTRY_SIZE, temp, 0, DIRECTORY_ENTRY_SIZE);
		//末尾的空间不为空，则说明该cluster空间已满
		if (!ArrayUtils.isEmpty(temp)) {
			return false;
		} else {
			for (int i = 0; i < (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR / DIRECTORY_ENTRY_SIZE); i++) {
				System.arraycopy(allData, i * DIRECTORY_ENTRY_SIZE, temp, 0, DIRECTORY_ENTRY_SIZE);
				if (ArrayUtils.isEmpty(temp)) {
					System.arraycopy(data, DIRECTORY_ENTRY_SIZE, allData, 0, i * DIRECTORY_ENTRY_SIZE);
					disk.writeSector(DATA_REGION_START + clusterIndex * PER_CLUSTER_SECTOR, data);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 首次集群保存数据，可以快速赋值替换
	 *
	 * @param data 数据
	 */
	public void save(int clusterIndex, byte[] data) {
		disk.writeSector(DATA_REGION_START + clusterIndex * PER_CLUSTER_SECTOR, data);
	}

	/**
	 * 添加保存文件
	 *
	 * @param data         正好能把当前集群填满的数据
	 * @param oldFileSize  旧文件大小
	 * @param clusterIndex 集群指数
	 */
	public void appendSaveFile(int clusterIndex, byte[] data, int oldFileSize) {
		//最后一个sector使用的空间
		int lastSectorUsedSpace = oldFileSize % PER_SECTOR_BYTES;
		//最后一个sector剩余的空间
		int lastSectorRemainSpace = PER_SECTOR_BYTES - lastSectorUsedSpace;
		//最后一个集群，已使用的容量
		int lastClusterUsedSize = oldFileSize % (PER_CLUSTER_SECTOR * PER_CLUSTER_SECTOR);
		//最后一个集群，占用的sector数量
		int lastClusterUsedSectorSize = lastClusterUsedSize / PER_SECTOR_BYTES;
		//补齐第一个sector
		if (lastSectorUsedSpace > 0) {
			//第一个sector剩余的空间，可以存储保存的数据
			if (lastSectorRemainSpace > data.length) {
				disk.appendWriteSector(DATA_REGION_START + clusterIndex * PER_CLUSTER_SECTOR + lastClusterUsedSectorSize, data, lastSectorUsedSpace);
				//要存入的数据size，大于第一个sector剩余的空间
			} else {
				byte[] temp = new byte[lastSectorRemainSpace];
				System.arraycopy(data, 0, temp, 0, lastSectorRemainSpace);
				disk.appendWriteSector(DATA_REGION_START + clusterIndex * PER_CLUSTER_SECTOR + lastClusterUsedSectorSize, temp, lastSectorUsedSpace);
			}
			lastClusterUsedSectorSize++;
		}
		int remainSaveData = data.length - lastSectorRemainSpace;
		if (remainSaveData > 0) {
			byte[] temp = new byte[remainSaveData];
			System.arraycopy(data, lastSectorRemainSpace, temp, 0, remainSaveData);
			disk.writeSector(DATA_REGION_START + clusterIndex * PER_CLUSTER_SECTOR + lastClusterUsedSectorSize, temp);
		}
	}

	public DataCluster[] findClusters(int[] index) {
		DataCluster[] result = new DataCluster[index.length];
		for (int i = 0; i < index.length; i++) {
			byte[] allData = getAllData(index[i]);
			DataCluster dataCluster = new DataCluster();
			dataCluster.save(allData);
			result[i] = dataCluster;
		}
		return result;
	}
}
