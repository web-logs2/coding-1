package com.ke.coding.service.cli.stdincli;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.service.action.AbstractAction.currentPath;

import com.ke.coding.api.dto.cli.CommandContext;
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
import com.ke.coding.service.filesystem.inandout.impl.FileOut;
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

	public void run(CommandContext commandContext) {
		AbstractAction action = null;
		switch (ActionTypeEnums.getByType(commandContext.getAction())) {
			case CAT:
				action = new CatAction();
				buildRedirectAction(action, commandContext.getOriginData());
				break;
			case LL:
				action = new LlAction();
				buildRedirectAction(action, commandContext.getOriginData());
				break;
			case FORMAT:
				action = new FormatAction();
				break;
			case PWD:
				action = new PwdAction();
				buildAction(action, commandContext.getOriginData());
				break;
			case CD:
				action = new CdAction();
				buildAction(action, commandContext.getOriginData());
				break;
			case ECHO:
				action = new EchoAction();
				String[] s = commandContext.getOriginData().split(" ");
				if (s.length == 2 && s[1].startsWith("\"") && s[1].endsWith("\"")){
					buildAction(action, commandContext.getOriginData());
				}else {
					buildRedirectAction(action, commandContext.getOriginData());
				}
				break;
			case MKDIR:
				action = new MkdirAction();
				buildAction(action, commandContext.getOriginData());
				break;
			case TOUCH:
				action = new TouchAction();
				buildAction(action, commandContext.getOriginData());
				break;
			default:
				String[] s1 = commandContext.getOriginData().split(" ");
				commandContext.setParams(Collections.singletonList(s1[1]));
		}
		action.run();
	}

	private void buildRedirectAction(AbstractAction action, String input) {
		String redirectPath = "";
		if (input.contains(">>")) {
			String[] split = input.split(">>");
			redirectPath = split[1];
		} else if (input.contains(">")) {
			String[] split = input.split(">");
			redirectPath = split[1];
		}

		if (StringUtils.isNotBlank(redirectPath)) {
			if (redirectPath.startsWith(" ")){
				redirectPath = redirectPath.substring(1);
			}
			if (!redirectPath.startsWith("/")) {
				redirectPath = currentPath.equals(ROOT_PATH) ? currentPath + redirectPath : currentPath + PATH_SPLIT + redirectPath;
			}
		}
		action.setIn(new ConsoleIn(input.getBytes(StandardCharsets.UTF_8)));
		action.setOut(StringUtils.isNotBlank(redirectPath) ? new FileOut(redirectPath) : new ConsoleOut());
		action.setErr(new ConsoleErr());
	}

	private void buildAction(AbstractAction action, String input) {
		action.setIn(new ConsoleIn(input.getBytes(StandardCharsets.UTF_8)));
		action.setOut(new ConsoleOut());
		action.setErr(new ConsoleErr());
	}

	public String echo(CommandContext commandContext) {
		commandContext.setParams(buildParams(commandContext.getOriginData()));
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
