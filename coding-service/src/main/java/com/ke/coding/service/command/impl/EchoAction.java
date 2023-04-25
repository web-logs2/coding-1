package com.ke.coding.service.command.impl;

import static com.ke.coding.api.enums.Constants.O_EXLOCK;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.FILENAME_LENGTH_TOO_LONG;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.service.command.AbstractAction;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:38
 * @description:
 */
public class EchoAction extends AbstractAction {

	Pattern p = Pattern.compile("\"(.*?)\"");
	Pattern p1 = Pattern.compile("'(.*?)'");

	@SneakyThrows
	@Override
	public void run() {
		byte[] input = readIn();
		String originData = new String(input);
		String[] s = originData.split(" ");
		//回显
		if (s.length == 2 && s[1].startsWith("\"") && s[1].endsWith("\"")) {
			out.write(buildData(s[1]).getBytes(StandardCharsets.UTF_8));
		} else {
			//重定向
			String wholeFileName;
			String data;
			if (s.length == 2) {
				//无空格
				String[] split = s[1].contains(">>") ? s[1].split(">>") : s[1].split(">");
				data = split[0];
				wholeFileName = split[1];
			} else {
				//有空格
				data = s[1];
				wholeFileName = s[3];
			}
			String fileName = wholeFileName;
			String fileNameExtension = "";
			if (wholeFileName.contains(".")) {
				String[] split = wholeFileName.split("\\.");
				fileName = split[0];
				fileNameExtension = split[1];
			}
			if (fileName.length() > 8 || fileNameExtension.length() > 3) {
				err.write(FILENAME_LENGTH_TOO_LONG.message().getBytes(StandardCharsets.UTF_8));
			}
			if (!wholeFileName.startsWith("/")) {
				wholeFileName = currentPath.equals(ROOT_PATH) ? currentPath + wholeFileName : currentPath + PATH_SPLIT + wholeFileName;
			}

			Fat16Fd fat16Fd = fileSystemService.open(wholeFileName, O_EXLOCK);
			if (fat16Fd.isEmpty()) {
				fileSystemService.mkdir(wholeFileName, false);
			}
			out.write(buildData(data).getBytes(StandardCharsets.UTF_8));
			fileSystemService.close(fat16Fd);
		}
	}

	String buildData(String input) {
		if (input.contains("\"") || input.contains("'")) {
			String data = "";
			Matcher m = p.matcher(input);
			while (m.find()) {
				data = m.group();
			}
			if (StringUtils.isNotBlank(data)) {
				return data.replace("\"", "");
			} else {
				Matcher m1 = p1.matcher(input);
				while (m1.find()) {
					data = m.group();
				}
				return data.replace("'", "");
			}
		} else {
			return input;
		}
	}

}
