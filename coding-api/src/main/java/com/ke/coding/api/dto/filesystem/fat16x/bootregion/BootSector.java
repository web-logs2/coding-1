package com.ke.coding.api.dto.filesystem.fat16x.bootregion;

import com.ke.coding.api.dto.filesystem.fat16x.Sector;
import com.ke.coding.common.HexByteUtil;
import lombok.Getter;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/2/28 17:43
 * @description: 启动分区配置信息
 */
public class BootSector extends Sector {


	@Getter
	private byte[] data = new byte[512];

	public BootSector(byte[] data) {
		this.data = data;
	}

	public BootSector() {
		setJumpCode();
		setOemName("mos-xyl");
		setBytesPerSector(512);
		setSectorsPerCluster(64);
		setReservedSectors(1);
		setNumbersOfFatCopy(2);
		setNumberOfPossibleRootEntries(1008);
		setSmallNumberOfSectors(0);
		setMediaDescriptor("F8");
		setSectorsPerFat(256);
		setSectorsPerTrack(0);
		setNumbersOfHeads(0);
		setHiddenSectors(0);
		setLargeNumberOfSectors(4194240);
		setDriveNumber(0);
		setReserved(0);
		setExtendBootSignature(0);
		setVolumeSerialNumber(0);
		setVolumeLabe(0);
		setFileSystemType("FAT16X");
		setBootStrapCode(0);
		setBootSectorSignature("AA55");
	}

	private void setJumpCode(){
		data[0] = HexByteUtil.hexToByte("EB");
		data[1] = HexByteUtil.hexToByte("3C");
		data[2] = HexByteUtil.hexToByte("90");
	}

	private void setOemName(String name) {
		char[] array = name.toCharArray();
		byte[] bytes = new byte[8];
		for (int i = 0; i < array.length; i++) {
			String s = Integer.toHexString(array[i]);
			byte hexToByte = HexByteUtil.hexToByte(s);
			bytes[i] = hexToByte;
		}
		System.arraycopy(bytes, 0, data, 0x0003, bytes.length);
	}

	private void setBytesPerSector(int num) {
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x000B, bytes.length);
	}

	private void setSectorsPerCluster(int num) {
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x000D, bytes.length);
	}

	private void setReservedSectors(int num) {
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x000E, bytes.length);
	}

	private void setNumbersOfFatCopy(int num) {
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x0010, bytes.length);
	}

	private void setNumberOfPossibleRootEntries(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x0011, bytes.length);
	}

	private void setSmallNumberOfSectors(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x0013, bytes.length);
	}

	private void setMediaDescriptor(String mediaDescriptor){
		byte[] bytes = HexByteUtil.hexToByteArray(mediaDescriptor);
		System.arraycopy(bytes, 0, data, 0x0015, bytes.length);
	}

	private void setSectorsPerFat(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x0016, bytes.length);
	}

	private void setSectorsPerTrack(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x0018, bytes.length);
	}

	private void setNumbersOfHeads(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x001A, bytes.length);
	}
	private void setHiddenSectors(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x001C, bytes.length);
	}

	private void setLargeNumberOfSectors(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x0020, bytes.length);
	}

	private void setDriveNumber(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x0024, bytes.length);
	}

	private void setReserved(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x0025, bytes.length);
	}

	private void setExtendBootSignature(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x0026, bytes.length);
	}

	private void setVolumeSerialNumber(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x0027, bytes.length);
	}

	private void setVolumeLabe(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x002B, bytes.length);
	}

	private void setFileSystemType(String fileSystemType){
		char[] array = fileSystemType.toCharArray();
		byte[] bytes = new byte[8];
		for (int i = 0; i < array.length; i++) {
			String s = Integer.toHexString(array[i]);
			byte hexToByte = HexByteUtil.hexToByte(s);
			bytes[i] = hexToByte;
		}
		System.arraycopy(bytes, 0, data, 0x0036, bytes.length);
	}

	private void setBootStrapCode(int num){
		String s = Integer.toHexString(num);
		byte[] bytes = HexByteUtil.hexToByteArray(s);
		System.arraycopy(bytes, 0, data, 0x003E, bytes.length);
	}

	private void setBootSectorSignature(String num){
		byte[] bytes = HexByteUtil.hexToByteArray(num);
		System.arraycopy(bytes, 0, data, 0x01FE, bytes.length);
	}


	public static void main(String[] args) {
		new BootSector();
	}

}
