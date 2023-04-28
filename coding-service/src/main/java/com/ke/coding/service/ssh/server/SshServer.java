package com.ke.coding.service.ssh.server;

import java.io.IOException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/23 14:26
 * @description:
 */

@Slf4j
public class SshServer implements Runnable{

	public static void main(String[] args) throws IOException, InterruptedException {
		new SshServer().start();
	}
	public void start() throws IOException, InterruptedException {
		org.apache.sshd.server.SshServer sshd = org.apache.sshd.server.SshServer.setUpDefaultServer();
		sshd.setPort(2222);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		sshd.setPasswordAuthenticator((username, password, session) -> username.equals(password));
		sshd.setShellFactory(channel -> new SshShellWrapper());
//		sshd.setShellFactory(channel -> {
//			InvertedShell shell = new ProcessShell("/bin/sh");
//			return new InvertedShellWrapper(shell);
//		});
		sshd.setCommandFactory((channel, command) -> null);
		sshd.start(); // 启动 SSHD 服务器
		LOGGER.debug("SSH server is up and running on port 2222...");

		// 保持服务器运行状态，直到手动停止
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@SneakyThrows
	@Override
	public void run() {
		start();
	}
}

