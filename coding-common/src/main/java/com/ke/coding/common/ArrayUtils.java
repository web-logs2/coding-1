package com.ke.coding.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/2 20:21
 * @description:
 */
public class ArrayUtils {

	public static List<List<Byte>> splitAry(byte[] data, int subSize) {

		int count = data.length % subSize == 0 ? data.length / subSize
			: data.length / subSize + 1;

		List<List<Byte>> subAryList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			int index = i * subSize;
			List<Byte> list = new ArrayList<>();
			int j = 0;
			while (j < subSize && index < data.length) {
				list.add(data[index++]);
				j++;
			}
			subAryList.add(list);
		}

		return subAryList;
	}

}
