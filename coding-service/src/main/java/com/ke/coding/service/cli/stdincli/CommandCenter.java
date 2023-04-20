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
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/6 18:18
 * @description:
 */
public class CommandCenter {

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
				if (s.length == 2 && s[1].startsWith("\"") && s[1].endsWith("\"")) {
					buildAction(action, commandContext.getOriginData());
				} else {
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
			if (redirectPath.startsWith(" ")) {
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

}
