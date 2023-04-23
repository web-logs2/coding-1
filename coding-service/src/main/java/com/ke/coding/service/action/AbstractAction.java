package com.ke.coding.service.action;

import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xSystemServiceImpl;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.FileSystemService;
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

	public static String currentPath = ROOT_PATH;

	public FileSystemService<Fat16Fd> fileSystemService = new Fat16xSystemServiceImpl();

	protected InputStream in;

	protected OutputStream out;

	protected OutputStream err;

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

}
