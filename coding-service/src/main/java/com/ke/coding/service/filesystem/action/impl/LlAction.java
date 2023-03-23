package com.ke.coding.service.filesystem.action.impl;

import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY_POS;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntrySubInfo;
import com.ke.coding.service.filesystem.action.AbstractAction;
import com.ke.risk.safety.common.util.date.DateUtil;
import com.ke.risk.safety.common.util.json.JsonUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class LlAction extends AbstractAction {

	@Override
	public FileSystemActionResult run(Command command, Fat16xFileSystem fat16xFileSystem) {
		List<DirectoryEntrySubInfo> result = new ArrayList<>();
		//step: 根目录，查询
		if (ROOT_PATH.equals(command.getCurrentPath())) {
			for (DirectoryEntry entry : fat16xFileSystem.getRootDirectoryRegion().getDirectoryEntries()) {
				if (entry == null) {
					break;
				}
				result.add(buildSubInfo(entry));
			}
		} else {
			DirectoryEntry directoryEntry = fileSystemService.findDirectoryEntry(command.getCurrentPath());
			List<DirectoryEntry> allDirectoryEntry = fileSystemService.getAllDirectoryEntry(directoryEntry.getStartingCluster());
			for (DirectoryEntry entry : allDirectoryEntry) {
				result.add(buildSubInfo(entry));
			}
		}
		return FileSystemActionResult.success(JsonUtils.parseBean2Str(result));
	}

	/**
	 * 构建条目名称
	 *
	 * @param directoryEntry 目录条目
	 * @return {@link String}
	 */
	protected DirectoryEntrySubInfo buildSubInfo(DirectoryEntry directoryEntry) {
		DirectoryEntrySubInfo info = new DirectoryEntrySubInfo();
		String name;
		if (1 == directoryEntry.getAttribute(ATTRIBUTE_DIRECTORY_POS)) {
			name = "/" + directoryEntry.getFileName();
		} else {
			name = directoryEntry.getFileNameExtension().equals("   ") ? directoryEntry.getFileName() :
				directoryEntry.getFileName() + "." + directoryEntry.getFileNameExtension();
		}
		info.setFileName(name);
		info.setFileSize(directoryEntry.getFileSize());
		Date date = new Date(directoryEntry.getLastAccessTimeStamp());
		info.setAccessDate(DateUtil.getDateStr(date, "yyyy/MM/dd"));

		return info;
	}
}
