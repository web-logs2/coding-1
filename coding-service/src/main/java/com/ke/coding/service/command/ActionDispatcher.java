package com.ke.coding.service.command;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.api.enums.ActionTypeEnums;
import com.ke.coding.service.command.impl.DefaultAction;
import com.ke.coding.service.command.impl.EchoAction;
import com.ke.coding.service.shell.AbstractShell;
import com.ke.coding.service.stream.output.Fat16xAndResponseOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/6 18:18
 * @description:
 */
public class ActionDispatcher {

	private final InputStream in;
	private final OutputStream out;
	private final OutputStream err;

	private InputStream actionIn;

	private final AbstractShell abstractShell;

	public ActionDispatcher(InputStream in, OutputStream out, OutputStream err, AbstractShell abstractShell) {
		this.in = in;
		this.out = out;
		this.err = err;
		this.abstractShell = abstractShell;
	}

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

	@SneakyThrows
	public void run() {
		AbstractAction action;
		String input = readIn(in);
		actionIn = IOUtils.toInputStream(input);
		String[] split = input.split(" ");
		ActionTypeEnums actionTypeEnums = ActionTypeEnums.getByType(split[0]);
		switch (actionTypeEnums) {
			case CAT:
			case LL:
				Class<?> cls = Class.forName("com.ke.coding.service.command.impl." + actionTypeEnums.getClazz());
				Constructor<?> ctor = cls.getDeclaredConstructor();
				action = (AbstractAction) ctor.newInstance();
				buildRedirectAction(action, input);
				break;
			case FORMAT:
			case PWD:
			case CD:
			case MKDIR:
			case TOUCH:
			case RM:
			case CLEAR:
				cls = Class.forName("com.ke.coding.service.command.impl." + actionTypeEnums.getClazz());
				ctor = cls.getDeclaredConstructor();
				action = (AbstractAction) ctor.newInstance();
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
			default:
				action = new DefaultAction();
				buildAction(action);
				break;
		}
		action.run();
		out.flush();
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
				redirectPath = abstractShell.getCurrentPath().equals(ROOT_PATH) ? abstractShell.getCurrentPath() + redirectPath
					: abstractShell.getCurrentPath() + PATH_SPLIT + redirectPath;
			}
		}
		action.setIn(actionIn);
		if (StringUtils.isNotBlank(redirectPath)) {
			Fat16Fd open = AbstractAction.fileSystemService.open(redirectPath);
			action.setOut(new Fat16xAndResponseOutputStream(open, out));
		} else {
			action.setOut(out);
		}

		action.setErr(err);
		action.setAbstractShell(abstractShell);
	}

	private void buildAction(AbstractAction action) {
		action.setIn(actionIn);
		action.setOut(out);
		action.setErr(err);
		action.setAbstractShell(abstractShell);
	}


}
