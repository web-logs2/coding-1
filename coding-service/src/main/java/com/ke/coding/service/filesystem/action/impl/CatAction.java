package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY_POS;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.service.filesystem.action.AbstractAction;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class CatAction extends AbstractAction {

	@Override
	public FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem) {
		String currentPath = command.getCurrentPath();
		String fileName = command.getParams().get(0);
		//cat /xyl.dat
		if (ROOT_PATH.equals(currentPath)) {
			for (DirectoryEntry directoryEntry : fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()) {
				if (directoryEntry == null) {
					break;
					//匹配到对应的文件
				} else if (fileName.equals(directoryEntry.getWholeFileName())) {
					int beginCluster = directoryEntry.getStartingCluster();
					//文件之前未写入过内容
					if (beginCluster == 0) {
						return FileSystemActionResult.success("");
					} else {
						int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(beginCluster);
						byte[] dataBytes = new byte[(int) directoryEntry.getFileSize()];
						dataRegionService.getClustersData(index, dataBytes);
						directoryEntry.setLastAccessTimeStamp();
						return FileSystemActionResult.success(new String(dataBytes));
					}
				}
			}
		} else {
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(currentPath, fat16xFileSystem);
			int startingCluster = rootDirectoryEntry.getStartingCluster();
			if (startingCluster != 0) {
				//cat /test/xyl1.dat
				String[] splitPath = command.getCurrentPath().split(PATH_SPLIT);
				if (splitPath.length == 2) {
					//一级目录全部元素
					List<DirectoryEntry> directoryEntries = buildAllDirectoryEntry(startingCluster, fat16xFileSystem);
					for (DirectoryEntry directoryEntry : directoryEntries) {
						//找到对应文件，返回数据
						if (directoryEntry.getWholeFileName().equals(fileName)) {
							//文件之前未写入过内容
							if (directoryEntry.getStartingCluster() == 0) {
								return FileSystemActionResult.success("");
							} else {
								int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(directoryEntry.getStartingCluster());
								byte[] dataBytes = new byte[(int) directoryEntry.getFileSize()];
								dataRegionService.getClustersData(index, dataBytes);
								directoryEntry.setLastAccessTimeStamp();
								return FileSystemActionResult.success(new String(dataBytes));
							}
						}
					}
				} else {
					//cat /test/111/xyl2.dat
					//二级目录遍历match
					for (int i = 2; i < splitPath.length; i++) {
						//找到对应的全部目录cluster
						List<DirectoryEntry> directoryEntries = buildAllDirectoryEntry(startingCluster, fat16xFileSystem);
						for (DirectoryEntry directoryEntry : directoryEntries) {
							//匹配目录名称
							if (1 == directoryEntry.getAttribute(ATTRIBUTE_DIRECTORY_POS) && directoryEntry.getFileName().equals(splitPath[i])) {
								if (i == splitPath.length - 1) {
									//找到对应文件，返回数据
									List<DirectoryEntry> currentPathDirectoryEntries = buildAllDirectoryEntry(directoryEntry.getStartingCluster(),
										fat16xFileSystem);
									for (DirectoryEntry currentPathDirectoryEntry : currentPathDirectoryEntries) {
										if (currentPathDirectoryEntry.getWholeFileName().equals(fileName)) {
											//文件之前未写入过内容
											if (currentPathDirectoryEntry.getStartingCluster() == 0) {
												return FileSystemActionResult.success("");
											} else {
												int[] index = fat16xFileSystem.getFatRegion()
													.allOfFileClusterIndex(currentPathDirectoryEntry.getStartingCluster());
												byte[] dataBytes = new byte[(int) currentPathDirectoryEntry.getFileSize()];
												dataRegionService.getClustersData(index, dataBytes);
												currentPathDirectoryEntry.setLastAccessTimeStamp();
												return FileSystemActionResult.success(new String(dataBytes));
											}
										}
									}
								} else {
									startingCluster = directoryEntry.getStartingCluster();
								}
							}
						}
					}
				}
			}
		}
		return FileSystemActionResult.fail(NO_SUCH_FILE_OR_DIRECTORY);
	}
}
