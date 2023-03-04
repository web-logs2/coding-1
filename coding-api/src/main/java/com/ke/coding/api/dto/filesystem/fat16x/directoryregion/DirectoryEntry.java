package com.ke.coding.api.dto.filesystem.fat16x.directoryregion;


import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;

import com.ke.coding.api.enums.Constants;
import com.ke.coding.common.BitUtil;
import com.ke.coding.common.HexByteUtil;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 19:56
 * @description:
 */
@Data
public class DirectoryEntry {

	/**
	 * 00h	8 bytes	Filename - 文件名，ascii码表示，最多8个字符。A-Z, 0-1, #, $, %, &, ', (, ), -, @
	 * 08h	3 bytes	Filename Extension - 文件拓展名，ascii码表示，最多3个字符。A-Z, 0-1, #, $, %, &, ', (, ), -, @
	 * 0Bh	1 bytes	Attribute Byte - 文件属性
	 * 0Ch	1 bytes	Reserved for Windows NT - 置为 0，无用途
	 * 0Dh	1 bytes	Creation - 置为 0，暂不使用
	 * 0Eh	4 bytes	Creation Time Stamp - 创建时间的秒级时间戳
	 * 12h	2 bytes	Last Access Date Stamp - 上次访问日期，天级时间戳
	 * 14h	2 bytes	Reserved for FAT32 - 置为 0，不使用
	 * 16h	4 bytes	Last Write Time Stamp - 最后写时间，秒级时间戳
	 * 1Ah	2 bytes	Starting cluster - 指向该文件/文件夹起始 cluster。如果是文件，cluster 里保存的是这个文件的第一部分数据；如果是文件夹，cluster 里保存改文件的子条目项
	 * 1Ch	4 bytes	File size - 如果是文件，表示文件字节数，最大 2 的 32 次方。如果不是文件，此值为 0
	 */
	byte[] data = new byte[DIRECTORY_ENTRY_SIZE];

	public void setFileName(String fileName){
		char[] array = fileName.toCharArray();
		for (int i = 0; i < array.length; i++) {
			String s = Integer.toHexString(array[i]);
			byte hexToByte = HexByteUtil.hexToByte(s);
			data[i] = hexToByte;
		}
	}

	public String getWholeFileName(){
		return getFileName() + (StringUtils.isBlank(getFileNameExtension()) ? "" : "." + getFileNameExtension());
	}

	public String getFileName(){
		int length = 0;
		for (int i = 0; i < 8; i++) {
			if (data[i] > 0){
				length++;
			}
		}
		char[] array = new char[length];
		for (int i = 0; i < length; i++) {
				String s = HexByteUtil.byteToHex(data[i]);
				int i1 = Integer.parseInt(s, 16);
				char ch = (char) i1;
				array[i] = ch;
		}
		return new String(array);
	}

	public void setFileNameExtension(String fileNameExtension){
		char[] array = fileNameExtension.toCharArray();
		byte[] bytes = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			String s = Integer.toHexString(array[i]);
			byte hexToByte = HexByteUtil.hexToByte(s);
			bytes[i] = hexToByte;
		}
		System.arraycopy(bytes, 0, data, 8, 3);
	}

	public String getFileNameExtension(){
		byte[] bytes = new byte[3];
		char[] array = new char[3];
		System.arraycopy(data, 8, bytes, 0, 3);
		for (int i = 0; i < bytes.length; i++) {
			array[i] = (char) Integer.parseInt(HexByteUtil.byteToHex(bytes[i]),16);
		}
		return new String(array);
	}

	public void setAttribute(int hexType){
		data[11] = (byte) (data[11] | hexType);
	}

	public int getAttribute(int pos){
		return BitUtil.getBit(data[11], pos);
	}

	public void setCreateTimeStamp(){
		String format = String.format("%08x", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
		byte[] bytes = HexByteUtil.hexToByteArray(format);
		System.arraycopy(bytes, 0, data, 14, 4);
	}

	public long getCreateTimeStamp(){
		byte[] bytes = new byte[4];
		System.arraycopy(data, 14, bytes, 0, 4);
		String s = HexByteUtil.bytesToHex(bytes);
		return Long.parseLong(s, 16);
	}

	public void setLastAccessTimeStamp(){
		String format = String.format("%04x", TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
		byte[] bytes = HexByteUtil.hexToByteArray(format);
		System.arraycopy(bytes, 0, data, 18, 2);
	}

	public long getLastAccessTimeStamp(){
		byte[] bytes = new byte[2];
		System.arraycopy(data, 18, bytes, 0, 2);
		String s = HexByteUtil.bytesToHex(bytes);
		return Long.parseLong(s, 16);
	}

	public void setLastWriteTimeStamp(){
		String format = String.format("%08x", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
		byte[] bytes = HexByteUtil.hexToByteArray(format);
		System.arraycopy(bytes, 0, data, 22, 4);
	}

	public long getLastWriteTimeStamp(){
		byte[] bytes = new byte[4];
		System.arraycopy(data, 22, bytes, 0, 4);
		String s = HexByteUtil.bytesToHex(bytes);
		return Long.parseLong(s, 16);
	}

	public void setStartingCluster(int clusterPos){
		String format = String.format("%04x", clusterPos);
		byte[] bytes = HexByteUtil.hexToByteArray(format);
		System.arraycopy(bytes, 0, data, 26, 2);
	}

	public int getStartingCluster(){
		byte[] bytes = new byte[2];
		System.arraycopy(data, 26, bytes, 0, 2);
		String s = HexByteUtil.bytesToHex(bytes);
		return Integer.parseInt(s, 16);
	}

	public void setFileSize(long fileSize){
		String format = String.format("%08x", fileSize);
		byte[] bytes = HexByteUtil.hexToByteArray(format);
		System.arraycopy(bytes, 0, data, 28, 4);
	}

	public long getFileSize(){
		byte[] bytes = new byte[4];
		System.arraycopy(data, 28, bytes, 0, 4);
		String s = HexByteUtil.bytesToHex(bytes);
		return Long.parseLong(s, 16);
	}

	public static DirectoryEntry buildDir(String fileName, int attr){
		DirectoryEntry directoryEntry = new DirectoryEntry();
		//step:准备基础信息
		directoryEntry.setFileName(fileName);
		directoryEntry.setAttribute(attr);
		directoryEntry.setLastWriteTimeStamp();
		directoryEntry.setCreateTimeStamp();
		return directoryEntry;
	}

	public static DirectoryEntry buildFile(String fileName, String fileNameExtension){
		DirectoryEntry directoryEntry = new DirectoryEntry();
		//step:准备基础信息
		directoryEntry.setFileName(fileName);
		directoryEntry.setFileNameExtension(fileNameExtension);
		directoryEntry.setLastWriteTimeStamp();
		directoryEntry.setCreateTimeStamp();
		directoryEntry.setFileSize(0);
		//touch文件，不需要绑定cluster
		directoryEntry.setStartingCluster(0);
		return directoryEntry;
	}

	public void updateWriteInfo(long fileSize){
		setFileSize(fileSize);
		setLastWriteTimeStamp();
	}

	public static void main(String[] args) {
		DirectoryEntry directoryEntry = new DirectoryEntry();
		directoryEntry.setFileName("hello");
		System.out.println(directoryEntry.getFileName());
		directoryEntry.setAttribute(Constants.ATTRIBUTE_DIRECTORY);
		System.out.println(directoryEntry.getAttribute(4));
		System.out.println(directoryEntry.getAttribute(0));

		directoryEntry.setCreateTimeStamp();
		System.out.println(directoryEntry.getCreateTimeStamp());

		directoryEntry.setLastAccessTimeStamp();
		System.out.println(directoryEntry.getLastAccessTimeStamp());

		directoryEntry.setLastWriteTimeStamp();
		System.out.println(directoryEntry.getLastWriteTimeStamp());

		directoryEntry.setStartingCluster(65535);
		System.out.println(directoryEntry.getStartingCluster());

		directoryEntry.setFileSize(65535123);
		System.out.println(directoryEntry.getFileSize());

		directoryEntry.setFileNameExtension("jpg");
		System.out.println(directoryEntry.getFileNameExtension());
	}

}
