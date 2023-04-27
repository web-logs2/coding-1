package com.ke.coding.service.ssh.server;

import java.io.IOException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/23 14:26
 * @description:
 */

@Slf4j
public class SimpleSshServer implements Runnable{

	public static void main(String[] args) throws IOException, InterruptedException {
		new SimpleSshServer().start();
	}

	public void start() throws IOException, InterruptedException {
		SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setPort(2222);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider()); // 设置主机密钥生成器
		sshd.setPasswordAuthenticator((username, password, session) -> {
			// 实现密码认证逻辑
			return true;
		}); // 设置密码认证器
		sshd.setPublickeyAuthenticator((username, key, session) -> {
			// 实现公钥认证逻辑
			return true;
		}); // 设置公钥认证器
		sshd.setShellFactory(channel -> new MockShellWrapper());
//		sshd.setShellFactory(channel -> {
//			InvertedShell shell = new ProcessShell("/bin/sh");
//			return new InvertedShellWrapper(shell);
//		});
		sshd.setCommandFactory((channel, command) -> {
			return null;
		});
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

