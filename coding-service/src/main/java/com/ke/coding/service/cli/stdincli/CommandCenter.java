package com.ke.coding.service.cli.springshell;

import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_ERROR;
import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_NOT_FOUND;

import com.google.common.base.Joiner;
import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntrySubInfo;
import com.ke.coding.api.enums.ActionTypeEnums;
import com.ke.coding.service.filesystem.fatservice.FileSystem;
import com.ke.risk.safety.common.util.json.JsonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/6 18:18
 * @description:
 */
@Service
public class CommandCenter {

	@Autowired
	FileSystem fileSystem;

	@Getter
	private String currentPath = ROOT_PATH;

	Pattern p = Pattern.compile("\"(.*?)\"");
	Pattern p1 = Pattern.compile("'(.*?)'");

	public String run(Command command) {
		String result = "";
		command.setCurrentPath(currentPath);
		switch (ActionTypeEnums.getByType(command.getAction())) {
			case DEFAULT:
				result = ACTION_NOT_FOUND.message();
				break;
			case LL:
				result = ll();
				break;
			case FORMAT:
				result = format();
				break;
			case PWD:
				result = pwd();
				break;
			case CD:
				String[] s = command.getOriginData().split(" ");
				if (s.length != 2){
					return ACTION_ERROR.message();
				}
				command.setParams(Collections.singletonList(s[1]));
				FileSystemActionResult fileSystemActionResult = fileSystem.execute(command);
				if (fileSystemActionResult.isSuccess()){
					currentPath = fileSystemActionResult.getData();
				}
				result = fileSystemActionResult.getData();
				break;
			case ECHO:
				result = echo(command);
				break;
			default:
				String[] s1 = command.getOriginData().split(" ");
				if (s1.length != 2){
					return ACTION_ERROR.message();
				}
				command.setParams(Collections.singletonList(s1[1]));
				return fileSystem.execute(command).getData();
		}
		return result;
	}


	public String ll() {
		FileSystemActionResult result = fileSystem.execute(Command.build("ll", currentPath));
		List<DirectoryEntrySubInfo> directoryEntrySubInfos = JsonUtils.parseStr2List(result.getData(), DirectoryEntrySubInfo.class);
		List<String> lines = new ArrayList<>();
		for (DirectoryEntrySubInfo directoryEntrySubInfo : directoryEntrySubInfos) {
			String sb = StringUtils.rightPad(directoryEntrySubInfo.getFileSize() + "", 10, " ")
				+ StringUtils.rightPad(directoryEntrySubInfo.getAccessDate() + "", 14, " ")
				+ directoryEntrySubInfo.getFileName();
			lines.add(sb);
		}
		return Joiner.on("\n").join(lines);
	}

	public String format() {
		fileSystem.execute(Command.build("format", currentPath));
		return fileSystem.execute(Command.build("cd", currentPath, Collections.singletonList(ROOT_PATH))).getData();
	}

	public String pwd() {
		return currentPath;
	}


	public String echo(Command command){
		command.setParams(buildParams(command.getOriginData()));
		return fileSystem.execute(command).getData();
	}

	List<String> buildParams(String input){
		List<String> result = new ArrayList<>();
		if (input.contains("\"") | input.contains("'")){
			String data = "";
			Matcher m= p.matcher(input);
			while(m.find())
			{
				data = m.group();
			}
			if (StringUtils.isNotBlank(data)){
				result.add(data.replace("\"",""));
				String replace = input.replace(data, "");
				String[] split = replace.split(" ");
				result.addAll(Arrays.asList(split).subList(1, split.length));
				result.remove("");
			}else {
				Matcher m1 = p1.matcher(input);
				while(m1.find())
				{
					data = m.group();
				}
				result.add(data.replace("'",""));
				String replace = input.replace(data, "");
				String[] split = replace.split(" ");
				result.addAll(Arrays.asList(split).subList(1, split.length));
			}
		}else {
			String[] split = input.split(" ");
			result.addAll(Arrays.asList(split).subList(1, split.length));
		}
		return result;
	}
}
