package com.ke.coding.api.dto.filesystem.fat16x.dataregion;

import static com.ke.coding.common.ArrayUtils.list2Ary;
import static com.ke.coding.common.ArrayUtils.splitAry;

import com.ke.coding.api.enums.Constants;
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
	public boolean appendSave(byte[] data) {
		for (int i = 0; i < sectors.length; i++) {
			//sector为null时，初始化，并追加内容
			if (sectors[i] == null) {
				sectors[i] = new DataSector();
				sectors[i].appendSave(data, 0);
			} else {
				//sector不为null时，寻找到起始的空闲下标
				int i1 = sectors[i].freeSpaceIndexForDir(data.length);
				if (i1 != -1) {
					sectors[i].appendSave(data, i1);
					return true;
				}
			}
		}
		return false;
	}
}
