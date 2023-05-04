package com.ke.coding.service.command.impl;

import static com.ke.coding.api.enums.Constants.O_EXLOCK;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.service.command.AbstractAction;
import com.lianjia.infrastructure.org.apache.commons.lang.StringEscapeUtils;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

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
		String extraData = extraData(originData);
		originData = originData.replace(extraData, "");
		extraData = StringEscapeUtils.unescapeJava(extraData);
		String[] s = originData.split(" ");
		//回显
		if (!originData.contains(">>") && !originData.contains(">")) {
			out.write(extraData.getBytes(StandardCharsets.UTF_8));
		} else {
			//重定向
			String wholeFileName;
			if (s.length == 2) {
				//无空格
				String[] split = s[1].contains(">>") ? s[1].split(">>") : s[1].split(">");
				wholeFileName = split[1];
			} else {
				//有空格
				wholeFileName = s[3];
			}
			if (!wholeFileName.startsWith("/")) {
				wholeFileName = abstractShell.getCurrentPath().equals(ROOT_PATH) ? abstractShell.getCurrentPath() + wholeFileName
					: abstractShell.getCurrentPath() + PATH_SPLIT + wholeFileName;
			}
			Fat16Fd fat16Fd = null;
			try {
				fat16Fd = fileSystemService.open(wholeFileName, O_EXLOCK);
				if (fat16Fd.isEmpty()) {
					fileSystemService.mkdir(wholeFileName, false);
				}
				out.write(extraData.getBytes(StandardCharsets.UTF_8));
			} finally {
				fileSystemService.close(fat16Fd);
			}
		}
	}

	String extraData(String input) {
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

	}

}
