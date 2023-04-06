package com.ke.coding.service.filesystem.fat16xservice.filesystemservice.inputstream;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/4 11:30
 * @description:
 */
@Service
public class Fat16InputStream extends InputStream<Fat16Fd> {

	@Autowired
	FileSystemService<Fat16Fd> fileSystemService;

	@Override
	int read(byte[] data) {
		return fileSystemService.readFileBuffer(fd, data);
	}
}
