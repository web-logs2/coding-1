package com.ke.coding.api.dto.filesystem.fat16x.fatregion;

import static com.ke.coding.api.enums.Constants.FAT_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.FAT_NC_END_OF_FILE;
import static com.ke.coding.api.enums.Constants.PER_CLUSTER_SECTOR;
import static com.ke.coding.api.enums.Constants.PER_SECTOR_BYTES;

import com.ke.coding.common.HexByteUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 18:33
 * @description: fat数据区
 */
@Data
public class FatRegion {

	/**
	 * fat[0] = 0xFFF8，值中的 'F8' 既 media descriptor； * fat[1] = 0xFFFF，除了标识第 2 个 cluster 被使用了以外，最高位 2 bit 还标识了当前分区的状态，不做赘述，默认取值 0xFFFF fat[2] ->
	 * fat[15], 每一个 NCx （next cluster for x）标识 cluster[x] 的存储状态，这些条目的取值范围如下： 0003h - FFEFh  ,3-65519
	 */
	private Fat[] fats;

	public FatRegion(byte[] data) {
		fats = new Fat[65536];
		for (int i = 0; i < fats.length; i++) {
			byte[] temp = new byte[FAT_ENTRY_SIZE];
			System.arraycopy(data, i * FAT_ENTRY_SIZE, temp, 0, FAT_ENTRY_SIZE);
			fats[i] = new Fat(temp);
		}
	}

	public void format() {
		fats = new Fat[65536];
	}


	/**
	 * 第一个空闲的集群
	 *
	 * @return int
	 */
	public int firstFreeFat() {
		int index = -1;
		for (int i = 2; i < fats.length; i++) {
			if (fats[i] == null || fats[i].free()) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * rm脂肪
	 *
	 * @param index 指数
	 */
	public void rmFat(int index){
		fats[index] = null;
	}


	/**
	 * 集群结尾坐标
	 *
	 * @param startingCluster 从集群
	 * @return int
	 */
	public int endOfFileCluster(int startingCluster) {
		int startIndex = startingCluster;
		//当前集群不为结束节点，继续循环寻找
		while (!fats[startIndex].isEnd()) {
			startIndex = fats[startingCluster].nextCluster();
		}
		return startIndex;
	}

	/**
	 * 集群链表坐标
	 *
	 * @param startingCluster startingCluster
	 * @return int
	 */
	public int[] allOfFileClusterIndex(int startingCluster) {
		if (startingCluster == 0) {
			return new int[1];
		}
		List<Integer> data = new ArrayList<>();
		data.add(startingCluster);
		int startIndex = startingCluster;
		//当前集群不为结束节点，继续循环寻找
		while (!fats[startIndex].isEnd()) {
			data.add(startIndex);
			startIndex = fats[startIndex].nextCluster();
		}
		int[] result = new int[data.size()];
		for (int i = 0; i < data.size(); i++) {
			result[i] = data.get(i);
		}
		return result;
	}


}
