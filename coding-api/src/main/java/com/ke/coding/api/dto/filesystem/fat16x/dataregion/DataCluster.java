package com.ke.coding.api.dto.filesystem.fat16x.dataregion;

import static com.ke.coding.api.enums.Constants.PER_CLUSTER_SECTOR;
import static com.ke.coding.api.enums.Constants.PER_SECTOR_BYTES;
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

	/**
	 * 追加保存，在文件尾追加数据
	 *
	 * @param data 数据
	 * @return boolean
	 */
	public boolean appendSaveDir(byte[] data) {
		for (int i = 0; i < sectors.length; i++) {
			//sector为null时，初始化，并追加内容
			if (sectors[i] == null) {
				sectors[i] = new DataSector();
				sectors[i].appendSaveDir(data, 0);
				return true;
			} else {
				//sector不为null时，寻找到起始的空闲下标
				int i1 = sectors[i].freeSpaceIndexForDir(data.length);
				if (i1 != -1) {
					sectors[i].appendSaveDir(data, i1);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 添加保存文件
	 *
	 * @param data        数据
	 * @param oldFileSize 旧文件大小
	 */
	public void appendSaveFile(byte[] data, int oldFileSize) {
		int firstSectorUsedSpace = oldFileSize % PER_SECTOR_BYTES;
		int firstSectorRemainSpace = PER_SECTOR_BYTES - firstSectorUsedSpace;
		int lastSector = 0;
		while (sectors[lastSector] != null) {
			lastSector++;
		}
		//补齐第一个sector
		if (firstSectorRemainSpace > 0) {
			//第一个sector剩余的空间，可以存储保存的数据
			if (firstSectorRemainSpace > data.length) {
				sectors[lastSector - 1].appendSaveFile(data, 0, firstSectorUsedSpace, data.length);
				//要存入的数据size，大于第一个sector剩余的空间
			} else {
				sectors[lastSector - 1].appendSaveFile(data, 0, firstSectorUsedSpace, firstSectorRemainSpace);
			}

		}
		int remainSaveData = data.length - firstSectorRemainSpace;
		int needSectorCount = remainSaveData / PER_SECTOR_BYTES;
		int lastSectorSpace = remainSaveData % PER_SECTOR_BYTES;
		//把中间完整的sector数据存储
		for (int i = 0; i < needSectorCount; i++) {
			sectors[lastSector] = new DataSector();
			sectors[lastSector].appendSaveFile(data, (PER_SECTOR_BYTES - firstSectorRemainSpace) + needSectorCount * PER_SECTOR_BYTES, 0,
				PER_SECTOR_BYTES);
			lastSector++;
		}
		//尾部sector数据存储
		if (lastSectorSpace > 0){
			sectors[lastSector] = new DataSector();
			sectors[lastSector].appendSaveFile(data, (PER_SECTOR_BYTES - firstSectorRemainSpace) + needSectorCount * PER_SECTOR_BYTES, 0,
				lastSectorSpace);
		}
	}


	/**
	 * 得到所有数据
	 *
	 * @return {@link byte[]}
	 */
	public byte[] getAllData() {
		byte[] result = new byte[PER_SECTOR_BYTES * PER_CLUSTER_SECTOR];
		for (int i = 0; i < sectors.length; i++) {
			DataSector sector = sectors[i];
			System.arraycopy(sector.getData(), 0, result, i * PER_SECTOR_BYTES, PER_SECTOR_BYTES);
		}
		return result;
	}

	/**
	 * 根据大小获取集群数据
	 *
	 * @param size 大小
	 * @return {@link byte[]}
	 */
	public byte[] getDataBySize(int size) {
		byte[] result = new byte[size];
		int sectorCount = size / PER_SECTOR_BYTES;
		int lastSectorCount = size % PER_SECTOR_BYTES;
		for (int i = 0; i < sectorCount; i++) {
			DataSector sector = sectors[i];
			System.arraycopy(sector.getData(), 0, result, i * PER_SECTOR_BYTES, PER_SECTOR_BYTES);
		}
		if (lastSectorCount > 0) {
			DataSector sector = sectors[sectorCount];
			System.arraycopy(sector.getData(), 0, result, sectorCount * PER_SECTOR_BYTES, lastSectorCount);
		}

		return result;

	}
}
