package com.ke.coding.common;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 18:20
 * @description:
 */
public class BitUtil {

	//b为传入的字节，i为第几位（范围0-7），如要获取bit0，则i=0
	public static int getBit(byte b,int i) {
		return ((b>>i) & 0x1);
	}

	//b为传入的字节，start是起始位，length是长度，如要获取bit0-bit4的值，则start为0，length为5
	public int getBits(byte b,int start,int length) {
		return (b>>start)&(0xFF>>(8-length));
	}

	public static void main(String[] args) {
		int leftNum = getBit((byte) 0x00, 8);
		System.out.println(leftNum);
	}

}
