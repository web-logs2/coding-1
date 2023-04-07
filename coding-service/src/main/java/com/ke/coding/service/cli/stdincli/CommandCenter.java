package com.ke.coding.service.cli.stdincli;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.enums.ActionTypeEnums;
import com.ke.coding.service.action.AbstractAction;
import com.ke.coding.service.action.impl.CatAction;
import com.ke.coding.service.action.impl.CdAction;
import com.ke.coding.service.action.impl.EchoAction;
import com.ke.coding.service.action.impl.FormatAction;
import com.ke.coding.service.action.impl.LlAction;
import com.ke.coding.service.action.impl.MkdirAction;
import com.ke.coding.service.action.impl.PwdAction;
import com.ke.coding.service.action.impl.TouchAction;
import com.ke.coding.service.filesystem.inandout.impl.ConsoleErr;
import com.ke.coding.service.filesystem.inandout.impl.ConsoleIn;
import com.ke.coding.service.filesystem.inandout.impl.ConsoleOut;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/6 18:18
 * @description:
 */
@Service
public class CommandCenter {

	Pattern p = Pattern.compile("\"(.*?)\"");
	Pattern p1 = Pattern.compile("'(.*?)'");

	public void run(Command command) {
		AbstractAction action = null;
		switch (ActionTypeEnums.getByType(command.getAction())) {
			case CAT:
				action = new CatAction();
				setInOut(action, command.getOriginData());
				break;
			case LL:
				action = new LlAction();
				setInOut(action, command.getOriginData());
				break;
			case FORMAT:
				action = new FormatAction();
				break;
			case PWD:
				action = new PwdAction();
				setInOut(action, command.getOriginData());
				break;
			case CD:
				action = new CdAction();
				setInOut(action, command.getOriginData());
				break;
			case ECHO:
				action = new EchoAction();
				setInOut(action, command.getOriginData());
				break;
			case MKDIR:
				action = new MkdirAction();
				setInOut(action, command.getOriginData());
				break;
			case TOUCH:
				action = new TouchAction();
				setInOut(action, command.getOriginData());
				break;
			default:
				String[] s1 = command.getOriginData().split(" ");
				command.setParams(Collections.singletonList(s1[1]));
		}
		action.run();
	}

	private void setInOut(AbstractAction action, String input) {
		action.setIn(new ConsoleIn(input.getBytes(StandardCharsets.UTF_8)));
		action.setOut(new ConsoleOut());
		action.setErr(new ConsoleErr());
	}

	public String echo(Command command) {
		command.setParams(buildParams(command.getOriginData()));
		return "";
	}

	List<String> buildParams(String input) {
		List<String> result = new ArrayList<>();
		if (input.contains("\"") || input.contains("'")) {
			String data = "";
			Matcher m = p.matcher(input);
			while (m.find()) {
				data = m.group();
			}
			if (StringUtils.isNotBlank(data)) {
				result.add(data.replace("\"", ""));
				String replace = input.replace(data, "");
				String[] split = replace.split(" ");
				result.addAll(Arrays.asList(split).subList(1, split.length));
				result.remove("");
			} else {
				Matcher m1 = p1.matcher(input);
				while (m1.find()) {
					data = m.group();
				}
				result.add(data.replace("'", ""));
				String replace = input.replace(data, "");
				String[] split = replace.split(" ");
				result.addAll(Arrays.asList(split).subList(1, split.length));
			}
		} else {
			String[] split = input.split(" ");
			result.addAll(Arrays.asList(split).subList(1, split.length));
		}
		return result;
	}
}
