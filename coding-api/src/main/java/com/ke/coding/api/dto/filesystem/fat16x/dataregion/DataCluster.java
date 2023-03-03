package com.ke.coding.api.dto.filesystem.fat16x.dataregion;

import static com.ke.coding.common.ArrayUtils.splitAry;

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
		List<List<Byte>> lists = splitAry(data, 512);
		for (int i = 0; i < lists.size(); i++) {
			byte[] temp = new byte[lists.get(i).size()];
			for (int i1 = 0; i1 < lists.get(i).size(); i1++) {
				temp[i1] = lists.get(i).get(i1);
			}
			sectors[i].save(temp);
		}
	}

	/**
	 * 追加保存，在文件尾追加数据
	 *
	 * @param data 数据
	 * @return boolean
	 */
	public boolean appendSave(byte[] data) {
		for (DataSector sector : sectors) {
			int i1 = sector.freeSpaceIndex(data.length);
			if (i1 != -1) {
				sector.appendSave(data, i1);
				return true;
			}
		}
		return false;
	}
}
