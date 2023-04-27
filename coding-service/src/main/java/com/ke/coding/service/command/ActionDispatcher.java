package com.ke.coding.service.command;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.service.command.AbstractAction.currentPath;

import com.baomidou.mybatisplus.extension.api.R;
import com.ke.coding.api.dto.cli.CommandContext;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.api.enums.ActionTypeEnums;
import com.ke.coding.service.command.impl.CatAction;
import com.ke.coding.service.command.impl.CdAction;
import com.ke.coding.service.command.impl.EchoAction;
import com.ke.coding.service.command.impl.FormatAction;
import com.ke.coding.service.command.impl.LlAction;
import com.ke.coding.service.command.impl.MkdirAction;
import com.ke.coding.service.command.impl.PwdAction;
import com.ke.coding.service.command.impl.RmAction;
import com.ke.coding.service.command.impl.TouchAction;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xSystemServiceImpl;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.FileSystemService;
import com.ke.coding.service.stream.output.Fat16xAndResponseOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/6 18:18
 * @description:
 */
public class ActionDispatcher {

	private InputStream in;
	private OutputStream out;
	private OutputStream err;

	public ActionDispatcher() {
	}

	public ActionDispatcher(InputStream in, OutputStream out, OutputStream err) {
		this.in = in;
		this.out = out;
		this.err = err;
	}

	public FileSystemService<Fat16Fd> fileSystemService = new Fat16xSystemServiceImpl();

	private String readIn(InputStream in) {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while (in.available() > 0 && (line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	public void run(CommandContext commandContext) {
		AbstractAction action = null;
		String input = readIn(in);
		String[] split = input.split(" ");
		switch (ActionTypeEnums.getByType(split[0])) {
			case CAT:
				action = new CatAction();
				buildRedirectAction(action, input);
				break;
			case LL:
				action = new LlAction();
				buildRedirectAction(action, input);
				break;
			case FORMAT:
				action = new FormatAction();
				break;
			case PWD:
				action = new PwdAction();
				buildAction(action);
				break;
			case CD:
				action = new CdAction();
				buildAction(action);
				break;
			case ECHO:
				action = new EchoAction();
				String[] s = input.split(" ");
				if (s.length == 2 && s[1].startsWith("\"") && s[1].endsWith("\"")) {
					buildAction(action);
				} else {
					buildRedirectAction(action, input);
				}
				break;
			case MKDIR:
				action = new MkdirAction();
				buildAction(action);
				break;
			case TOUCH:
				action = new TouchAction();
				buildAction(action);
				break;
			case RM:
				action = new RmAction();
				buildAction(action);
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
		action.setIn(in);
		if (StringUtils.isNotBlank(redirectPath)) {
			Fat16Fd open = fileSystemService.open(redirectPath);
			if (open.isEmpty()) {
				fileSystemService.mkdir(redirectPath, false);
				open = fileSystemService.open(redirectPath);
			}
			action.setOut(new Fat16xAndResponseOutputStream(open, out));
		} else {
			action.setOut(out);
		}

		action.setErr(err);
	}

	private void buildAction(AbstractAction action) {
		action.setIn(in);
		action.setOut(out);
		action.setErr(err);
	}


}
