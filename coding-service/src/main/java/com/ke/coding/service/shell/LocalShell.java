package com.ke.coding.service.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/8 12:10
 * @description:
 */
public class LocalShell extends AbstractShell {

	public LocalShell(InputStream in, OutputStream out, OutputStream err) {
		super(in, out, err);
	}

	@Override
	public void start() throws IOException {
		setCurrentUser("xyl-local");
		System.out.print("xyl-local@xyl-shell:/$");
		for (; ; ) {
			if (in.available() > 0){
				super.start();
			}
		}
	}
}
