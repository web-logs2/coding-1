package com.ke.coding.api.enums;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 15:25
 * @description:
 */
public class Constants {

	private Constants() {
		throw new IllegalStateException("Utility class");
	}

	public static final String ROOT_PATH = "/";
	public static final String PATH_SPLIT = "/";

	public static final int ATTRIBUTE_READ_ONLY = 0x01;
	public static final int ATTRIBUTE_HIDDEN = 0x02;
	public static final int ATTRIBUTE_SYSTEM = 0x04;
	public static final int ATTRIBUTE_V = 0x08;
	public static final int ATTRIBUTE_DIRECTORY = 0x10;
	public static final int ATTRIBUTE_ACHIEVE_FLAG = 0x20;

	public static final int ATTRIBUTE_READ_ONLY_POS = 0;
	public static final int ATTRIBUTE_HIDDEN_POS = 1;
	public static final int ATTRIBUTE_SYSTEM_POS = 2;
	public static final int ATTRIBUTE_V_POS = 3;
	public static final int ATTRIBUTE_DIRECTORY_POS = 4;
	public static final int ATTRIBUTE_ACHIEVE_FLAG_POS = 5;


	public static final int PER_SECTOR_BYTES = 512;
	public static final int PER_CLUSTER_SECTOR = 64;

	public static final String FAT_NC_FREE_CLUSTER = "0000";
	public static final String FAT_NC_NOT_ALLOWED = "0001";
	public static final String FAT_NC_BAD_SECTOR = "FFF7";
	public static final String FAT_NC_END_OF_FILE = "FFF8";

}
