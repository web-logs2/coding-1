package com.ke.coding.service.command;

import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xSystemServiceImpl;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.FileSystemService;
import com.ke.coding.service.shell.AbstractShell;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:40
 * @description:
 */
@Data
public abstract class AbstractAction implements Action {

	public static final FileSystemService<Fat16Fd> fileSystemService = new Fat16xSystemServiceImpl();

	protected InputStream in;

	protected OutputStream out;

	protected OutputStream err;

	protected AbstractShell abstractShell;

	@SneakyThrows
	public byte[] readIn() {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		return result.toByteArray();
	}

	public String buildFilePathName(String fileName) {
		if (fileName.startsWith(ROOT_PATH)) {
			return fileName;
		} else {
			if (abstractShell.getCurrentPath().equals(ROOT_PATH)) {
				return abstractShell.getCurrentPath() + fileName;
			} else {
				return abstractShell.getCurrentPath() + PATH_SPLIT + fileName;
			}
		}
	}

}
