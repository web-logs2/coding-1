package com.ke.coding;

import com.ke.coding.service.shell.LocalShell;
import com.ke.coding.service.ssh.server.SshServer;
import java.io.IOException;
import org.apache.sshd.common.util.threads.ThreadUtils;

/**
 * 服务启动类
 *
 * @author keboot
 */
public class Application {

	public static void main(String[] args) throws IOException, InterruptedException {
		ThreadUtils.newSingleThreadExecutor("sshServer").submit(() -> {
			try {
				new SshServer().start();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});
		Thread.sleep(1000);
		new LocalShell(System.in, System.out, System.out).start();

	}
}
