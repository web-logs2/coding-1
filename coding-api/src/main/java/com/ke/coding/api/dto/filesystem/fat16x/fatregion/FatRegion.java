package com.ke.coding.api.dto.filesystem.fat16x.fatregion;

import com.ke.coding.common.HexByteUtil;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 18:33
 * @description: fat数据区
 */
@Data
public class FatRegion {

	/**
	 * fat[0] = 0xFFF8，值中的 'F8' 既 media descriptor； fat[1] = 0xFFFF，除了标识第 2 个 cluster 被使用了以外，最高位 2 bit 还标识了当前分区的状态，不做赘述，默认取值 0xFFFF fat[2] -> fat[15],
	 * 每一个 NCx （next cluster for x）标识 cluster[x] 的存储状态，这些条目的取值范围如下：
	 */
	private Fat[] fats = new Fat[65516];

	public void save(int index, String ncStatus) {
		fats[index].save(HexByteUtil.hexToByteArray(ncStatus));
	}

	/**
	 * 第一个空闲的集群
	 *
	 * @return int
	 */
	public int firstFreeFat() {
		int index = -1;
		for (int i = 0; i < fats.length; i++) {
			if (fats[i].free()) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * 集群文件结尾
	 *
	 * @param startingCluster 从集群
	 * @return int
	 */
	public int endOfFileCluster(int startingCluster){
		int startIndex = startingCluster;
		//当前集群不为结束节点，继续循环寻找
		while (!fats[startIndex].isEnd()) {
			startIndex = fats[startingCluster].nextCluster();
		}
		return startIndex;
	}
}
