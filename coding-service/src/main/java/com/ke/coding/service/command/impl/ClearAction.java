package com.ke.coding.service.command.impl;

import com.ke.coding.service.command.AbstractAction;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.fusesource.jansi.Ansi;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/25 16:25
 * @description:
 */
public class ClearAction extends AbstractAction {

	@SneakyThrows
	@Override
	public void run() {
		out.write(Ansi.ansi().eraseScreen().toString().getBytes(StandardCharsets.UTF_8));
		out.write("\033[1;1H".getBytes(StandardCharsets.UTF_8));
	}
}
