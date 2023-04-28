package com.ke.coding.common;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.fusesource.jansi.Ansi;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/28 18:20
 * @description:
 */
public class ShellUtil {

	public static void newLine(OutputStream out) throws IOException {
		out.write(Ansi.ansi().newline().toString().getBytes(StandardCharsets.UTF_8));
		out.write(Ansi.ansi().cursorLeft(100).toString().getBytes(StandardCharsets.UTF_8));
		out.flush();
	}

}
