package com.ke.coding.api.dto.filesystem.fat16x.fatregion;

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
	private Fat[] fats = new Fat[65536];

	public void save(int index, String ncStatus) {
		fats[index] = fats[index] == null ? new Fat() : fats[index];
		fats[index].save(HexByteUtil.hexToByteArray(ncStatus));
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
	 * 根据数据大小，分配一组Fat
	 *
	 * @param fileSize 文件大小
	 * @return {@link int[]}
	 */
	public int[] freeFatArray(long fileSize) {
		int needCount = (int) (fileSize / (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR) + (fileSize % (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR) > 0 ? 1 : 0));
		int[] result = new int[needCount];
		//找空间
		for (int i = 0; i < needCount; i++) {
			int firstFreeFat = firstFreeFat();
			result[i] = firstFreeFat;
		}
		//建链
		if (needCount == 1){
			save(result[0], FAT_NC_END_OF_FILE);
		}else {
			for (int i = 0; i < result.length - 1; i++) {
				save(result[i], String.format("%04x", result[i+1]));
			}
			save(result[result.length - 1], FAT_NC_END_OF_FILE);
		}

		return result;
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
	 * 集群链表坐标,不包含当前传入坐标
	 *
	 * @param startingCluster startingCluster
	 * @return int
	 */
	public int[] allOfFileClusterIndex(int startingCluster) {
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
