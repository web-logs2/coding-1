package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY_POS;
import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataCluster;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataSector;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.service.filesystem.action.AbstractAction;
import com.ke.risk.safety.common.util.json.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class LsAction extends AbstractAction {

	@Override
	public FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem) {
		List<String> result = new ArrayList<>();
		//step: 根目录，查询
		if (ROOT_PATH.equals(command.getCurrentPath())) {
			for (DirectoryEntry directoryEntry : fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()) {
				if (directoryEntry == null) {
					break;
				}
				result.add(buildName(directoryEntry));
			}
		} else {
			//step；非根目录查询,/test ,/test/222
			DirectoryEntry rootDirectoryEntry = findRootDirectoryEntry(command.getCurrentPath(), fat16xFileSystem);
			int beginCluster = rootDirectoryEntry.getStartingCluster();
			//找到对应的全部目录cluster
			String[] splitPath = command.getCurrentPath().split(PATH_SPLIT);
			if (2 == splitPath.length) {
				//一级目录查询， /test
				List<DirectoryEntry> directoryEntries = buildAllDirectoryEntry(beginCluster, fat16xFileSystem);
				for (DirectoryEntry entry : directoryEntries) {
					result.add(buildName(entry));
				}
			} else {
				//二级及二级以上目录查询，/test/222
				for (int i = 2; i < splitPath.length; i++) {
					//找到对应的全部目录cluster
					int[] index = fat16xFileSystem.getFatRegion().allOfFileClusterIndex(beginCluster);
					DataCluster[] clusters = fat16xFileSystem.getDataRegion().findClusters(index);
					//循环跳出label，下边循环找到match的目录时，直接跳到这一层循环
					outloop:
					for (DataCluster cluster : clusters) {
						if (cluster != null) {
							//遍历所有扇区
							for (DataSector sector : cluster.getSectors()) {
								if (sector != null) {
									//找到当前扇区的末尾
									int freeSpaceIndex = sector.freeSpaceIndexForDir(DIRECTORY_ENTRY_SIZE);
									int begin = 0;
									while (begin < freeSpaceIndex) {
										DirectoryEntry directoryEntry = new DirectoryEntry();
										System.arraycopy(sector.getData(), begin, directoryEntry.getData(), 0, DIRECTORY_ENTRY_SIZE);
										//当前directoryEntry为文件夹，并且名称可以match
										if (1 == directoryEntry.getAttribute(ATTRIBUTE_DIRECTORY_POS) && directoryEntry.getFileName()
											.equals(splitPath[i])) {
											beginCluster = directoryEntry.getStartingCluster();
											//如果是最后一层目录，读取数据
											if (i == splitPath.length - 1) {
												List<DirectoryEntry> directoryEntries = buildAllDirectoryEntry(directoryEntry.getStartingCluster(),
													fat16xFileSystem);
												for (DirectoryEntry entry : directoryEntries) {
													result.add(buildName(entry));
												}
											}
											break outloop;
										}
										begin += DIRECTORY_ENTRY_SIZE;
									}
								}
							}
						}
					}
				}
			}
		}
		return FileSystemActionResult.success(JsonUtils.parseBean2Str(result));
	}
}
