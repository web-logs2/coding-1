package com.ke.coding.service.command.impl;

import static com.ke.coding.api.enums.Constants.O_SHLOCK;

import com.google.common.base.Joiner;
import com.ke.coding.api.dto.filesystem.Fd;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntrySubInfo;
import com.ke.coding.service.command.AbstractAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
public class LlAction extends AbstractAction {

	@SneakyThrows
	@Override
	public void run() {
		List<DirectoryEntrySubInfo> result = new ArrayList<>();
		Fat16Fd open = null;
		try {
			byte[] input = readIn();
			String originData = new String(input);
			String[] s1 = originData.split(" ");
			String llPath = abstractShell.getCurrentPath();
			if (s1.length == 2){
				llPath = s1[1];
			}
			open = fileSystemService.open(llPath, O_SHLOCK);
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
			out.write(Joiner.on("\n"+Ansi.ansi().cursorLeft(100).toString()).join(lines).getBytes(StandardCharsets.UTF_8));
			out.flush();
		} finally {
			fileSystemService.close(open);
		}
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
