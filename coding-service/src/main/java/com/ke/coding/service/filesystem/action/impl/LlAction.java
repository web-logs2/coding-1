package com.ke.coding.service.filesystem.action.impl;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.Fd;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntrySubInfo;
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
public class LlAction extends AbstractAction {

	@Override
	public FileSystemResult run(Command command) {
		List<DirectoryEntrySubInfo> result = new ArrayList<>();
		Fd open = fileSystemService.open(command.getCurrentPath());
		List<Fd> fdList = fileSystemService.list(open);
		for (Fd fd : fdList) {
			result.add(buildSubInfo(fd));
		}
		return FileSystemResult.success(JsonUtils.parseBean2Str(result));
	}

	/**
	 * 构建条目名称
	 *
	 * @return {@link String}
	 */
	protected DirectoryEntrySubInfo buildSubInfo(Fd fd) {
		DirectoryEntrySubInfo info = new DirectoryEntrySubInfo();
		info.setFileName(fd.getFileName());
		info.setFileSize(fd.getFileSize());
		info.setAccessDate(fd.getAccessDate());
		return info;
	}
}
