package com.ke.coding.service.action.impl;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_ERROR;
import static com.ke.coding.api.enums.ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY;

import com.ke.coding.api.dto.filesystem.Fd;
import com.ke.coding.service.action.AbstractAction;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class CdAction extends AbstractAction {

	@Override
	public void run() {
		byte[] input = in.getInput();
		String originData = new String(input);
		String[] s1 = originData.split(" ");
		if (s1.length != 2) {
			err.err(ACTION_ERROR.message().getBytes(StandardCharsets.UTF_8));
		}
		String cdPath = s1[1];
		if (cdPath.contains("..")) {
			String[] cdPathSplit = cdPath.split(PATH_SPLIT);
			String[] currentPathSplit = currentPath.split(PATH_SPLIT);

			String result = "";
			for (int i = 0; i < currentPathSplit.length - cdPathSplit.length; i++) {
				result = ROOT_PATH + currentPathSplit[i];
			}
			currentPath = result;
			out.output(result.getBytes(StandardCharsets.UTF_8));
		} else if (cdPath.equals(ROOT_PATH)) {
			out.output(ROOT_PATH.getBytes(StandardCharsets.UTF_8));
		} else {
			if (!cdPath.startsWith(PATH_SPLIT)) {
				cdPath = currentPath.equals(ROOT_PATH) ? currentPath + cdPath : currentPath + PATH_SPLIT + cdPath;
			}
			Fd open = fileSystemService.open(cdPath);
			if (open.isEmpty()) {
				err.err(NO_SUCH_FILE_OR_DIRECTORY.message().getBytes(StandardCharsets.UTF_8));
			} else {
				currentPath = cdPath;
				out.output(cdPath.getBytes(StandardCharsets.UTF_8));
			}
		}

	}
}
