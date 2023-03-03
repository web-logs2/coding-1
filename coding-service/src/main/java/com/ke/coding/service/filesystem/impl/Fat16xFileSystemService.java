package com.ke.coding.service.filesystem.impl;

import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY_POS;
import static com.ke.coding.api.enums.Constants.FAT_NC_END_OF_FILE;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_DATA_ERROR;
import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_LENGTH_TOO_LONG;
import static com.ke.coding.api.enums.ErrorCodeEnum.INSUFFICIENT_SPACE;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataCluster;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.service.filesystem.AbstractFileSystem;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 11:17
 * @description:
 */
@Service
public class Fat16xFileSystemService extends AbstractFileSystem {

	Fat16xFileSystem fat16xFileSystem;

	/**
	 * 初始化文件系统
	 */
	@PostConstruct
	void init() {
		fat16xFileSystem = new Fat16xFileSystem();
	}

	/**
	 * mkdir
	 *
	 * @param command 命令
	 */
	@Override
	public FileSystemActionResult mkdir(Command command) {
		//step: 文件名，文件后缀长度限制
		String newDir = command.getParams().get(0);
		if (newDir.length() > 8) {
			return FileSystemActionResult.fail(DIR_LENGTH_TOO_LONG);
		}
		//step: 当前路径是根目录，需要判断根目录区域空间是否充足
		String currentPath = command.getCurrentPath();
		if (ROOT_PATH.equals(currentPath) && !hasIdleRootDirectorySpace()) {
			return FileSystemActionResult.fail(INSUFFICIENT_SPACE);
		}
		//step: 目录数据需要写入cluster，判断剩余cluster空间是否充足
		if (fat16xFileSystem.getIdleClusterSize() <= 0) {
			return FileSystemActionResult.fail(INSUFFICIENT_SPACE);
		}

		DirectoryEntry newDirectoryEntry = DirectoryEntry.build(newDir, ATTRIBUTE_DIRECTORY_POS);
		//step: 根下创建一级目录，不需要存储cluster
		if (ROOT_PATH.equals(currentPath)) {
			//step:准备基础信息
			fat16xFileSystem.getRootDirectoryRegion().
				getDirectoryEntries()[fat16xFileSystem.getRootDirectoryRegion().getUsedSize() + 1] = newDirectoryEntry;
		} else {
			//step：非根下创建一级目录，则寻找其一级目录对应的directoryEntry,必然能找到一个，因为想切到下层目录，上层目录必然已经创建并保存
			String[] split = currentPath.split(PATH_SPLIT);
			String path = split[1];
			DirectoryEntry directoryEntry = null;
			for (DirectoryEntry tempEntry : fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()) {
				if (tempEntry != null && tempEntry.getFileName().equals(path)) {
					directoryEntry = tempEntry;
				}
			}

			if (directoryEntry == null) {
				return FileSystemActionResult.fail(DIR_DATA_ERROR);
			} else {
				//step: 一级目录下，如果是第一次创建资源，会存在没有cluster的情况
				int startingCluster = directoryEntry.getStartingCluster();
				//startingCluster==0,说明之前没有分配cluster
				if (startingCluster == 0) {
					//选择空闲的cluster，并把当前创建的目录数据写入，注意，如果资源过大，需要在fat区保存下一个引用的cluster(目录名有长度，第一次存储肯定不会超长)
					int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
					//保存数据区域数据
					DataCluster cluster = fat16xFileSystem.getDataRegion().getClusters()[firstFreeFat];
					//存储DirectoryEntry
					cluster.save(newDirectoryEntry.getData());
					//保存fat区数据
					fat16xFileSystem.getFatRegion().save(firstFreeFat, FAT_NC_END_OF_FILE);
				} else {
					//startingCluster不为0，则说明之前已经有目录写入，那么通过startingCluster去查询fat区，查询后续的cluster
					int endOfFileCluster = fat16xFileSystem.getFatRegion().endOfFileCluster(startingCluster);
					//保存数据区域数据
					int newEndOfFileCluster = fat16xFileSystem.getDataRegion().saveDir(newDirectoryEntry.getData(), endOfFileCluster,
						fat16xFileSystem);
					//保存fat区数据,新cluster置为尾部，老cluster指向新的cluster
					fat16xFileSystem.getFatRegion().save(newEndOfFileCluster, FAT_NC_END_OF_FILE);
					fat16xFileSystem.getFatRegion().save(endOfFileCluster, String.format("%04x", newEndOfFileCluster));
				}
			}
		}
		return FileSystemActionResult.success();


	}

	/**
	 * 空闲目录空间
	 *
	 * @return boolean
	 */
	@Override
	protected boolean hasIdleRootDirectorySpace() {
		return fat16xFileSystem.getRootDirectoryRegion().haveIdleSpace();
	}
}
