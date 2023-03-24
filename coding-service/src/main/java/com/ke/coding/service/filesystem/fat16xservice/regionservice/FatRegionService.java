package com.ke.coding.service.filesystem.fat16xservice.regionservice;

import static com.ke.coding.api.enums.Constants.FAT_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.FAT_NC_END_OF_FILE;
import static com.ke.coding.api.enums.Constants.FAT_START;
import static com.ke.coding.api.enums.Constants.PER_CLUSTER_SECTOR;
import static com.ke.coding.api.enums.Constants.PER_SECTOR_BYTES;

import com.ke.coding.api.dto.filesystem.fat16x.fatregion.Fat;
import com.ke.coding.api.dto.filesystem.fat16x.fatregion.FatRegion;
import com.ke.coding.common.HexByteUtil;
import com.ke.coding.service.disk.IDisk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/14 15:11
 * @description:
 */
@Service
public class FatRegionService {

	@Autowired
	IDisk disk;

	public void save(int index, String ncStatus, FatRegion fatRegion) {
		fatRegion.getFats()[index] = fatRegion.getFats()[index] == null ? new Fat() : fatRegion.getFats()[index];
		fatRegion.getFats()[index].save(HexByteUtil.hexToByteArray(ncStatus));
		int sectorIndex = (index * FAT_ENTRY_SIZE) / disk.sectorSize();
		int sectorDataSize = (index * FAT_ENTRY_SIZE) % disk.sectorSize();
		byte[] bytes = disk.readSector(FAT_START + sectorIndex);
		System.arraycopy(fatRegion.getFats()[index].getFatData(), 0, bytes, sectorDataSize, FAT_ENTRY_SIZE);
		disk.writeSector(FAT_START + sectorIndex, bytes);
	}

	public void relink(int oldCluster, int newCluster, FatRegion fatRegion){
		if (oldCluster != newCluster) {
			//保存fat区数据,新cluster置为尾部，老cluster指向新的cluster
			save(newCluster, FAT_NC_END_OF_FILE, fatRegion);
			save(oldCluster, String.format("%04x", newCluster), fatRegion);
		}
	}

	/**
	 * 根据数据大小，分配一组Fat
	 *
	 * @param fileSize 文件大小
	 * @return {@link int[]}
	 */
	public int[] freeFatArray(long fileSize, FatRegion fatRegion) {
		int needCount = (int) (fileSize / (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR) + (fileSize % (PER_SECTOR_BYTES * PER_CLUSTER_SECTOR) > 0 ? 1 : 0));
		int[] result = new int[needCount];
		//找空间
		for (int i = 0; i < needCount; i++) {
			int firstFreeFat = fatRegion.firstFreeFat();
			result[i] = firstFreeFat;
		}
		//建链
		if (needCount == 1) {
			save(result[0], FAT_NC_END_OF_FILE, fatRegion);
		} else {
			for (int i = 0; i < result.length - 1; i++) {
				save(result[i], String.format("%04x", result[i + 1]), fatRegion);
			}
			save(result[result.length - 1], FAT_NC_END_OF_FILE, fatRegion);
		}

		return result;
	}
}
