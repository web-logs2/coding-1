package com.ke.coding;

import com.ke.coding.service.ssh.server.SimpleSshServer;
import java.io.IOException;

/**
 * 服务启动类
 *
 * @author keboot
 */
public class Application {

	public static void main(String[] args) throws IOException, InterruptedException {
		SimpleSshServer simpleSshServer = new SimpleSshServer();
		simpleSshServer.start();
//		LocalInputResolver resolver = new LocalInputResolver();
//		resolver.run();
	}
}
