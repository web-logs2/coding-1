package com.ke.coding.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/2 20:21
 * @description:
 */
public class ArrayUtils {

	public static List<Byte> array2List(byte[] data){
		List<Byte> bytes = new ArrayList<>();
		for (byte datum : data) {
			bytes.add(datum);
		}
		return bytes;

	}

	public static byte[] list2Ary(List<Byte> bytes){
		byte[] result = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			result[i] = bytes.get(i);
		}
		return result;
	}

}
