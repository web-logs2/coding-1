package com.ke.coding.service.ssh.server;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/24 10:46
 * @description:
 */

import com.ke.coding.service.shell.MockShell;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.common.util.threads.ThreadUtils;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.session.ServerSessionAware;

public class MockShellWrapper extends AbstractLoggingBean implements Command, ServerSessionAware {

	private InputStream in;
	private OutputStream out;
	private OutputStream err;
	private ExitCallback callback;
	private ServerSession session;


	@Override
	public void start(ChannelSession channel, Environment env) throws IOException {
		MockShell shell = new MockShell();
		shell.setIn(in);
		shell.setErr(err);
		shell.setOut(out);
		ThreadUtils.newSingleThreadExecutor("shell" + Integer.toHexString(shell.hashCode()) + "]").submit(() -> {
			try {
				shell.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	@Override
	public void destroy(ChannelSession channel) throws Exception {

	}

	@Override
	public void setExitCallback(ExitCallback callback) {
		this.callback = callback;
	}

	@Override
	public void setErrorStream(OutputStream err) {
		this.err = err;
	}

	@Override
	public void setInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void setSession(ServerSession session) {
		this.session = session;
	}
}

