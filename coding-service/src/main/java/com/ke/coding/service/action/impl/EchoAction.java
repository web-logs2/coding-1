package com.ke.coding.service.action.impl;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.FILENAME_LENGTH_TOO_LONG;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.service.action.AbstractAction;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
@Service
public class EchoAction extends AbstractAction {

	@Override
	public void run() {
		byte[] input = in.getInput();
		String originData = new String(input);
		String[] s1 = originData.split(" ");
		String wholeFileName = s1[3];
		String fileName = wholeFileName;
		String fileNameExtension = "";
		if (wholeFileName.contains(".")) {
			String[] split = wholeFileName.split("\\.");
			fileName = split[0];
			fileNameExtension = split[1];
		}
		if (fileName.length() > 8 || fileNameExtension.length() > 3) {
			err.err(FILENAME_LENGTH_TOO_LONG.message().getBytes(StandardCharsets.UTF_8));
		}
		if (!wholeFileName.startsWith("/")) {
			wholeFileName = currentPath.equals(ROOT_PATH) ? currentPath + wholeFileName : currentPath + PATH_SPLIT + wholeFileName;
		}
		String pathAndFile = currentPath.equals(ROOT_PATH) ? currentPath + fileName : currentPath + PATH_SPLIT + fileName;
		Fat16Fd fat16Fd = fileSystemService.open(pathAndFile);
		if (fat16Fd.isEmpty()) {
			fileSystemService.mkdir(currentPath, wholeFileName, false);
		}
		out.output(s1[1].getBytes(StandardCharsets.UTF_8));
	}

}
