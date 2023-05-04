package com.ke.coding.service.ssh.server;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/23 14:26
 * @description:
 */

@Slf4j
public class SshServer {

	public static void main(String[] args) throws IOException, InterruptedException {
		new SshServer().start();
	}

	public void start() throws IOException, InterruptedException {
		org.apache.sshd.server.SshServer sshd = org.apache.sshd.server.SshServer.setUpDefaultServer();
		sshd.setPort(2222);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		sshd.setPasswordAuthenticator((username, password, session) -> username.equals(password));
		sshd.setShellFactory(channel -> new SshShellWrapper());
		sshd.setCommandFactory((channel, command) -> null);
		sshd.start();
		LOGGER.debug("SSH server is up and running on port 2222...");
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

