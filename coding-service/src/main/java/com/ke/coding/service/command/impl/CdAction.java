package com.ke.coding.service.command.impl;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_ERROR;
import static com.ke.coding.api.enums.ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.ke.coding.api.dto.filesystem.Fd;
import com.ke.coding.service.command.AbstractAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
public class CdAction extends AbstractAction {

	@SneakyThrows
	@Override
	public void run() {
		byte[] input = readIn();
		String originData = new String(input);
		String[] s1 = originData.split(" ");
		if (s1.length != 2) {
			err.write(ACTION_ERROR.message().getBytes(StandardCharsets.UTF_8));
		}
		String cdPath = s1[1].endsWith(PATH_SPLIT) ? s1[1].substring(0, s1[1].length() - 1) : s1[1];
		if (cdPath.contains("..")) {
			String[] cdPathSplit = cdPath.split(PATH_SPLIT);
			ArrayList<String> currentPathSplit = Lists.newArrayList(shell.getCurrentPath().split(PATH_SPLIT));
			currentPathSplit.remove("");
			String result = ROOT_PATH;
			List<String> subList = currentPathSplit.subList(0, currentPathSplit.size() - cdPathSplit.length);
			result += Joiner.on(PATH_SPLIT).join(subList);
			shell.updateCurrentPath(result);
			out.write(result.getBytes(StandardCharsets.UTF_8));
		} else if (cdPath.equals(ROOT_PATH)) {
			shell.updateCurrentPath(ROOT_PATH);
			out.write(ROOT_PATH.getBytes(StandardCharsets.UTF_8));
		} else {
			if (!cdPath.startsWith(PATH_SPLIT)) {
				cdPath = shell.getCurrentPath().equals(ROOT_PATH) ? shell.getCurrentPath() + cdPath : shell.getCurrentPath() + PATH_SPLIT + cdPath;
			}
			Fd open = fileSystemService.open(cdPath);
			if (open.isEmpty()) {
				err.write(NO_SUCH_FILE_OR_DIRECTORY.message().getBytes(StandardCharsets.UTF_8));
			} else {
				shell.updateCurrentPath(cdPath);
				out.write(cdPath.getBytes(StandardCharsets.UTF_8));
			}
		}

	}
}
