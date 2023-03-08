package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY;
import static com.ke.coding.api.enums.Constants.FAT_NC_END_OF_FILE;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_LENGTH_TOO_LONG;
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
public class MkdirAction extends AbstractAction {

	@Override
	public FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem) {
		//step: 文件名，文件后缀长度限制
		String newDir = command.getParams().get(0);
		if (newDir.length() > 8) {
			return FileSystemActionResult.fail(DIR_LENGTH_TOO_LONG);
		}
		//step: 当前路径是根目录，需要判断根目录区域空间是否充足
		String currentPath = command.getCurrentPath();
		if (ROOT_PATH.equals(currentPath) && !hasIdleRootDirectorySpace(fat16xFileSystem)) {
			return FileSystemActionResult.fail(INSUFFICIENT_SPACE);
		}
		//step: 目录数据需要写入cluster，判断剩余cluster空间是否充足
		if (fat16xFileSystem.getIdleClusterSize() <= 0) {
			return FileSystemActionResult.fail(INSUFFICIENT_SPACE);
		}

		DirectoryEntry newDirectoryEntry = DirectoryEntry.buildDir(newDir, ATTRIBUTE_DIRECTORY);
		//currentPath=/, param1=test
		//step: 根目录，不需要存储cluster。
		if (ROOT_PATH.equals(currentPath)) {
			//step:直接保存至RootDirectoryRegion
			fat16xFileSystem.getRootDirectoryRegion().
				getDirectoryEntries()[fat16xFileSystem.getRootDirectoryRegion().freeIndex()] = newDirectoryEntry;
		} else {
			//currentPath=/test, param1=111
			//step：非根目录，寻根目录节点
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(currentPath, fat16xFileSystem);
			int startingCluster = rootDirectoryEntry.getStartingCluster();
			//startingCluster==0,说明之前没有分配目录cluster
			if (startingCluster == 0) {
				//选择空闲的cluster，并把当前创建的目录数据写入
				int firstFreeFat = fat16xFileSystem.getFatRegion().firstFreeFat();
				//保存数据区域数据
				fat16xFileSystem.getDataRegion().saveDir(newDirectoryEntry.getData(), firstFreeFat, fat16xFileSystem);
				//保存fat区数据
				fat16xFileSystem.getFatRegion().save(firstFreeFat, FAT_NC_END_OF_FILE);
				rootDirectoryEntry.setStartingCluster(firstFreeFat);
			} else {
				String[] split = currentPath.split(PATH_SPLIT);
				if (split.length == 2) {
					//currentPath=/test, param1=222
					//step： 一级目录创建：之前分配过目录，那么我们根据当前路径，去寻找到对应的cluster，并把目录数据追加进去
					createFileOrDirInFirstLevelPath(startingCluster, newDirectoryEntry, fat16xFileSystem);
				} else {
					//currentPath=/test/222, param1=xyl
					//currentPath=/test/222, param1=xyl1
					createFileOrDirInSecondLevelPath(split, startingCluster, newDirectoryEntry, fat16xFileSystem);
				}
			}
		}

		return FileSystemActionResult.success();
	}
}
