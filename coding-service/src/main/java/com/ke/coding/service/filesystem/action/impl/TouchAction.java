package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.FAT_NC_END_OF_FILE;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_DATA_ERROR;
import static com.ke.coding.api.enums.ErrorCodeEnum.FILENAME_LENGTH_TOO_LONG;
import static com.ke.coding.api.enums.ErrorCodeEnum.INSUFFICIENT_SPACE;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.service.filesystem.action.AbstractAction;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class TouchAction extends AbstractAction {

	@Override
	public FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem) {
		//step: 文件名，文件名后缀长度限制
		String fileName = command.getParams().get(0);
		String fileNameExtension = "";
		if (fileName.contains(".")) {
			String[] split = fileName.split("\\.");
			fileName = split[0];
			fileNameExtension = split[1];
		}
		if (fileName.length() > 8 || fileNameExtension.length() > 3) {
			return FileSystemActionResult.fail(FILENAME_LENGTH_TOO_LONG);
		}
		//step: 当前路径是根目录，需要判断根目录区域空间是否充足
		String currentPath = command.getCurrentPath();
		if (ROOT_PATH.equals(currentPath) && !hasIdleRootDirectorySpace(fat16xFileSystem)) {
			return FileSystemActionResult.fail(INSUFFICIENT_SPACE);
		}
		DirectoryEntry newDirectoryEntry = DirectoryEntry.buildFile(fileName, fileNameExtension);
		//step: 根下创建文件，不需要存储目录cluster
		//ex：/xyl.jpg
		if (ROOT_PATH.equals(currentPath)) {
			fat16xFileSystem.getRootDirectoryRegion().
				getDirectoryEntries()[fat16xFileSystem.getRootDirectoryRegion().freeIndex()] = newDirectoryEntry;
		} else {
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(currentPath, fat16xFileSystem);
			if (rootDirectoryEntry == null) {
				return FileSystemActionResult.fail(DIR_DATA_ERROR);
			} else {
				String[] split = currentPath.split(PATH_SPLIT);
				int startingCluster = rootDirectoryEntry.getStartingCluster();
				//ex：/test/xyl.jpg
				//step：一级目录创建文件
				if (split.length == 2) {
					if (startingCluster == 0) {
						//先创建目录
						int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
						//保存fat区数据
						fat16xFileSystem.getFatRegion().save(firstFreeFat, FAT_NC_END_OF_FILE);
						rootDirectoryEntry.setStartingCluster(firstFreeFat);
						startingCluster = firstFreeFat;
					}
					createFileOrDirInFirstLevelPath(startingCluster, newDirectoryEntry, fat16xFileSystem);
				} else {
					//ex：/test/222/xyl.jpg
					//step：非一级目录创建文件
					createFileOrDirInSecondLevelPath(split, startingCluster, newDirectoryEntry, fat16xFileSystem);
				}
			}
		}
		return FileSystemActionResult.success();
	}
}
