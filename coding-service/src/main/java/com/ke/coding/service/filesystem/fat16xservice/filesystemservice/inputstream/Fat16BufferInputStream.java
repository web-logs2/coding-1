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
public class Fat16BufferInputStream extends BufferInputStream<Fat16Fd> {

	@Autowired
	FileSystemService fileSystemService;

	@Override
	int read(byte[] data) {
		byte[] bytes = fileSystemService.readFile(fd);
		if (pos >= data.length) {
			return -1;
		} else {
			System.arraycopy(bytes, pos, data, 0, bytes.length);
			pos += data.length;
			return pos;
		}
	}
}
