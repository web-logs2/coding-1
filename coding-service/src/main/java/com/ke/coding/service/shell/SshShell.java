package com.ke.coding.service.shell;

import static com.ke.coding.common.ShellUtil.newLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/24 14:44
 * @description:
 */
public class SshShell extends AbstractShell {

	public SshShell(InputStream in, OutputStream out, OutputStream err) {
		super(in, out, err);
	}

	public void echo(byte b) throws IOException {
		out.write(b);
		out.flush();
	}

	@Override
	public void beforeRun() throws IOException {
		newLine(out);
	}
}
