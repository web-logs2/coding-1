package com.ke.coding.service.shell;

import java.io.IOException;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/8 12:10
 * @description:
 */
public class LocalShell extends MockShell {

	public LocalShell() {
		super.setIn(System.in);
		super.setOut(System.out);
		super.setErr(System.out);
	}

	@Override
	public void start() throws IOException {
		System.out.print("root@xyl-shell:/$");
		super.start();
	}
}
