package com.ke.coding.api.dto.filesystem.fat16x.dataregion;

import com.ke.coding.api.dto.filesystem.fat16x.Sector;
import lombok.Data;

/**
 * @author xueyunlong
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 18:18
 * @description: 数据分区
 * @date 2023/03/01
 */
@Data
public class DataSector extends Sector {

	/**
	 * 扇区大小默认512bytes
	 */
	private byte[] data = new byte[512];

	public void save(byte[] newData) {
		System.arraycopy(newData, 0, data, 0, newData.length);
	}

	public void appendSaveDir(byte[] newData, int startIndex) {
		System.arraycopy(newData, 0, data, startIndex, newData.length);
	}

	public void appendSaveFile(byte[] newData, int srcStartIndex, int desStartIndex, int size) {
		System.arraycopy(newData, srcStartIndex, data, desStartIndex, size);
	}

	/**
	 * 返回空闲空间的起始坐标，不为空返回 -1
	 * 该方法仅限创建目录时使用
	 * @param size 大小
	 * @return boolean
	 */
	public int freeSpaceIndexForDir(int size) {
		//通过size分片，比如size=32，可以分为16片
		int count = 512 / size;
		for (int i = 0; i < count; i++) {
			//遍历每一片空间，如果全为0，说明当前区域为空，可以保存
			int begin = i * size;
			boolean empty = true;
			for (int j = begin; j < begin + size; j++) {
				//元素不为0，说明有值，直接跳出当前分片
				if (data[j] != 0) {
					empty = false;
					break;
				}
			}
			//当前分片全为空，返回
			if (empty) {
				return begin;
			}
		}
		return -1;
	}
}
