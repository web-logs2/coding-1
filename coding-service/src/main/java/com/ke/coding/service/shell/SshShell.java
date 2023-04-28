package com.ke.coding.service.shell;

import static com.ke.coding.common.ShellUtil.newLine;

import com.ke.coding.api.enums.ActionTypeEnums;
import com.ke.coding.api.enums.ErrorCodeEnum;
import com.ke.coding.service.command.ActionDispatcher;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import lombok.Data;
import org.apache.commons.io.IOUtils;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/24 14:44
 * @description:
 */
@Data
public class SshShell extends LocalShell {

	public SshShell(InputStream in, OutputStream out, OutputStream err) {
		super(in, out, err);
	}

	ActionDispatcher actionDispatcher;

	@Override
	public void start() throws IOException {
		try {
			byte[] bufferBytes = IOUtils.toByteArray(in);
			beforeRun();
			new ActionDispatcher(new ByteArrayInputStream(bufferBytes), out, err, this).run();
			afterRun(bufferBytes);
		} catch (Exception e) {
			e.printStackTrace();
			err.write(ErrorCodeEnum.ACTION_ERROR.message().getBytes(StandardCharsets.UTF_8));
			newLine(err);
			userLine(err);
		}
	}

	public void echo(byte b) throws IOException {
		out.write(b);
		out.flush();
	}

	void beforeRun() throws IOException {
		newLine(out);
	}

	void afterRun(byte[] bufferBytes) throws IOException {
		if (!new String(bufferBytes).equals(ActionTypeEnums.CLEAR.getType())) {
			newLine(out);
		}
		userLine(out);
	}


}
