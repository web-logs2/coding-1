package com.ke.coding.service.action.impl;

import com.google.common.base.Joiner;
import com.ke.coding.api.dto.filesystem.Fd;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntrySubInfo;
import com.ke.coding.service.action.AbstractAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class LlAction extends AbstractAction {

	@SneakyThrows
	@Override
	public void run() {
		List<DirectoryEntrySubInfo> result = new ArrayList<>();
		Fat16Fd open = fileSystemService.open(currentPath);
		List<Fat16Fd> fdList = fileSystemService.list(open);
		for (Fd fd : fdList) {
			result.add(buildSubInfo(fd));
		}
		List<String> lines = new ArrayList<>();
		for (DirectoryEntrySubInfo directoryEntrySubInfo : result) {
			String sb = StringUtils.rightPad(directoryEntrySubInfo.getFileSize() + "", 10, " ")
				+ StringUtils.rightPad(directoryEntrySubInfo.getAccessDate() + "", 14, " ")
				+ directoryEntrySubInfo.getFileName();
			lines.add(sb);
		}
		out.write(Joiner.on("\n").join(lines).getBytes(StandardCharsets.UTF_8));
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
