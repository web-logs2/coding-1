package com.ke.coding.service.stream.input;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xSystemServiceImpl;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.FileSystemService;
import java.io.IOException;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/4 11:30
 * @description:
 */
public class Fat16InputStream extends AbstractInputStream<Fat16Fd> {

	public Fat16InputStream(Fat16Fd fd) {
		super.fd = fd;
	}

	FileSystemService<Fat16Fd> fileSystemService = new Fat16xSystemServiceImpl();


	@Override
	public int read() throws IOException {
		return 0;
	}

	@Override
	public int read(byte[] data) {
		return fileSystemService.readFile(fd, data);
	}
}
