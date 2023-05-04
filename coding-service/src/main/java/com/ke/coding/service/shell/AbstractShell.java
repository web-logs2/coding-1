package com.ke.coding.service.shell;

import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.common.ShellUtil.newLine;

import com.ke.coding.api.enums.ActionTypeEnums;
import com.ke.coding.api.enums.ErrorCodeEnum;
import com.ke.coding.service.command.ActionDispatcher;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/5/4 16:57
 * @description:
 */
@Data
public abstract class AbstractShell implements Shell{

	protected InputStream in;
	protected OutputStream out;
	protected OutputStream err;
	protected String currentUser;
	protected static final Map<String, String> USER_PATH = new ConcurrentHashMap<>();

	protected AbstractShell(InputStream in, OutputStream out, OutputStream err) {
		this.in = in;
		this.out = out;
		this.err = err;
	}

	@Override
	public void start() throws IOException {
		try {
			byte[] bufferBytes = readIn(in);
			beforeRun();
			new ActionDispatcher(new ByteArrayInputStream(bufferBytes), out, err, this).run();
			afterRun(bufferBytes);
		} catch (Exception e) {
			e.printStackTrace();
			err.write(ErrorCodeEnum.ACTION_ERROR.message().getBytes(StandardCharsets.UTF_8));
			newLine(err);
			userLine(err);
		}
	}

	@Override
	public void beforeRun() throws IOException {
	}

	@Override
	public void afterRun(byte[] bufferBytes) throws IOException {
		if (!new String(bufferBytes).equals(ActionTypeEnums.CLEAR.getType())) {
			newLine(out);
		}
		userLine(out);
	}

	protected void userLine(OutputStream out) throws IOException {
		out.write(
			(currentUser + "@xyl-shell:" + USER_PATH.getOrDefault(currentUser, ROOT_PATH) + "$").getBytes(StandardCharsets.UTF_8));
		out.flush();
	}

	protected byte[] readIn(InputStream in) {
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
		return builder.toString().getBytes(StandardCharsets.UTF_8);
	}


	public String getCurrentPath() {
		return USER_PATH.getOrDefault(currentUser, ROOT_PATH);
	}

	public void updateCurrentPath(String path) {
		USER_PATH.put(currentUser, path);
	}

}
