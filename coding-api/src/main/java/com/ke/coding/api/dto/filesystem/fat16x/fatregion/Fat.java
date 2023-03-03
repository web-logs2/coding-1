package com.ke.coding.api.dto.filesystem.fat16x.fatregion;

import static com.ke.coding.api.enums.Constants.FAT_NC_END_OF_FILE;
import static com.ke.coding.api.enums.Constants.FAT_NC_FREE_CLUSTER;

import com.ke.coding.common.HexByteUtil;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 19:30
 * @description:
 */
@Data
public class Fat {
	/**
	 * 0000h	Free cluster - 对应的 cluster 是空的，没有文件占用
	 * 0001h - 0002h	Not allowed - 不允许存在的值
	 * 0003h - FFEFh ，3-65519	Number of the next cluster - 对应 cluster 只是一个文件的部分内容，后续内容在下一个 cluster，这个值就是下一个 cluster 的编码
	 * FFF7h	One or more bad sectors in cluster - 对应 cluster 中有损坏扇区，不能使用
	 * FFF8h	End-of-file - 对应 cluster 是文件的结尾，没有后续数据了
	 */
	byte[] fatData = new byte[2];

	public boolean free(){
		return FAT_NC_FREE_CLUSTER.equals(HexByteUtil.bytesToHex(fatData));
	}

	public boolean isEnd(){
		return FAT_NC_END_OF_FILE.equals(HexByteUtil.bytesToHex(fatData));
	}

	public int nextCluster(){
		String s = HexByteUtil.bytesToHex(fatData);
		return Integer.parseInt(s, 16);
	}
	public void save(byte[] data){
		fatData = data;
	}

	public static void main(String[] args) {
		Fat fat = new Fat();
		System.out.println(fat.free());
	}


}
