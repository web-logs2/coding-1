package com.ke.coding.service.ssh.server;

import static com.ke.coding.common.ShellUtil.newLine;

import com.ke.coding.service.shell.SshShell;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.common.util.threads.ThreadUtils;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.session.ServerSessionAware;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/24 10:46
 * @description:
 */
public class SshShellWrapper extends AbstractLoggingBean implements Command, ServerSessionAware {

	private InputStream in;
	private OutputStream out;
	private OutputStream err;
	private ExitCallback callback;
	private ServerSession session;
	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	@Override
	public void start(ChannelSession channel, Environment env) throws IOException {

		SshShell shell = new SshShell(in, out, err);
		ThreadUtils.newSingleThreadExecutor("shell" + Integer.toHexString(shell.hashCode()) + "]").submit(() -> {
			String username = channel.getSession().getUsername();
			try {
				out.write((username + "@xyl-shell:/$").getBytes(StandardCharsets.UTF_8));
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			shell.setCurrentUser(username);
			try {
				pumpStreams(shell, channel);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void pumpStreams(SshShell shell, ChannelSession channel) throws IOException {
		for (; ; ) {
			if (in.available() > 0) {
				byte read = (byte) in.read();
				if (read == '\r') {
					try {
						if ("exit".equals(buffer.toString())) {
							destroy(channel);
						} else {
							shell.setIn(new ByteArrayInputStream(buffer.toByteArray()));
							shell.start();
						}
					} finally {
						buffer.reset();
					}
				} else {
					buffer.write(read);
					shell.echo(read);
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	@Override
	public void destroy(ChannelSession channel) throws IOException {
		newLine(out);
		out.write("bye~".getBytes(StandardCharsets.UTF_8));
		out.flush();
		newLine(out);
		session.close();
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

