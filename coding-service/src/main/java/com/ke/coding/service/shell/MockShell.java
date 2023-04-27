package com.ke.coding.service.shell;

import com.ke.coding.api.dto.cli.CommandContext;
import com.ke.coding.api.enums.ErrorCodeEnum;
import com.ke.coding.service.command.AbstractAction;
import com.ke.coding.service.command.ActionDispatcher;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import lombok.Data;
import org.apache.sshd.common.channel.ChannelPipedInputStream;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/24 14:44
 * @description:
 */
@Data
public class MockShell {

	private InputStream in;
	private OutputStream out;
	private OutputStream err;

	ActionDispatcher actionDispatcher;

	public void start() throws IOException {
		actionDispatcher = new ActionDispatcher(in, out, err);
		for (; ; ) {
			if (in.available() > 0) {
				CommandContext commandContext = new CommandContext();
				try {
					actionDispatcher.run(commandContext);
					out.write("\n".getBytes(StandardCharsets.UTF_8));
					out.write(("root@xyl-shell:" + AbstractAction.currentPath + "$").getBytes(StandardCharsets.UTF_8));
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
					err.write(ErrorCodeEnum.ACTION_ERROR.message().getBytes(StandardCharsets.UTF_8));
					err.write(("root@xyl-shell:" + AbstractAction.currentPath + "$").getBytes(StandardCharsets.UTF_8));
					err.flush();
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}


}
