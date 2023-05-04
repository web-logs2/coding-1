package com.ke.coding.service.shell;

import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.common.ShellUtil.newLine;

import com.ke.coding.service.command.ActionDispatcher;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/8 12:10
 * @description:
 */
@Data
public class LocalShell {

	public LocalShell() {
	}

	public LocalShell(InputStream in, OutputStream out, OutputStream err) {
		this.in = in;
		this.out = out;
		this.err = err;
	}

	protected InputStream in;
	protected OutputStream out;
	protected OutputStream err;
	protected String currentUser;
	private static final Map<String, String> USER_PATH = new ConcurrentHashMap<>();

	public void start() throws IOException {
		setCurrentUser("xyl-local");
		System.out.print("xyl-local@xyl-shell:/$");
		for (; ; ) {
			try {
				if (in.available() > 0) {
					new ActionDispatcher(in, out, err, this).run();
					newLine(out);
					userLine(out);
				}
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getCurrentPath() {
		return USER_PATH.getOrDefault(currentUser, ROOT_PATH);
	}

	public void updateCurrentPath(String path) {
		USER_PATH.put(currentUser, path);
	}

	protected void userLine(OutputStream out) throws IOException {
		out.write(
			(currentUser + "@xyl-shell:" + USER_PATH.getOrDefault(currentUser, ROOT_PATH) + "$").getBytes(StandardCharsets.UTF_8));
		out.flush();
	}
}
