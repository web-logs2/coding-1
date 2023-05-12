package com.ke.coding.service.shell;

import java.io.IOException;
import java.io.OutputStream;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class JlineExample {
	public static void main(String[] args) throws IOException {
		Terminal terminal = TerminalBuilder.builder().system(false).streams(System.in, System.out).build();
		OutputStream out = terminal.output();

		// 双向通信
		while (true) {
			LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();
			String s = reader.readLine("xyl@xyl-shell:/$");
			out.write(("You entered: " + s).getBytes());
			if ("exit".equals(s)) {
				break;
			}
		}
	}
}

