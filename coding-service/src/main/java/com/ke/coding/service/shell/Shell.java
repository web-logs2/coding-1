package com.ke.coding.service.shell;

import java.io.IOException;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/5/4 17:06
 * @description:
 */
public interface Shell {
	void start() throws IOException;
	void beforeRun() throws IOException;
	void afterRun(byte[] bufferBytes) throws IOException;
}
