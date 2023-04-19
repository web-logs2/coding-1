package com.ke.coding.service.filesystem.inandout.impl;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xSystemServiceImpl;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.FileSystemService;
import com.ke.coding.service.filesystem.inandout.Out;
import com.ke.coding.service.stream.output.Fat16xOutputStream;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/13 16:34
 * @description:
 */
public class FileOut implements Out {

	FileSystemService<Fat16Fd> fileSystemService = new Fat16xSystemServiceImpl();

	private final String filePath;

	public FileOut(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 输出
	 *
	 * @param data 数据
	 */
	@Override
	public void output(byte[] data) {
		Fat16Fd fd = fileSystemService.open(filePath);
		if (fd.isEmpty()){
			fileSystemService.mkdir(filePath, false);
			fd = fileSystemService.open(filePath);
		}
		Fat16xOutputStream stream = new Fat16xOutputStream(fd);
		stream.write(data);
	}
}
