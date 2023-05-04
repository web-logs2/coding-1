package com.ke.coding.service.filesystem.fat16xservice.filesystemservice;

import com.ke.coding.api.dto.filesystem.Fd;
import java.util.List;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/23 16:41
 * @description:
 */
public interface FileSystemService<F extends Fd> {

	F open(String fileName);

	F open(String fileName, int state);

	int readFile(F fd, byte[] data);

	void writeFile(F fd, byte[] data);

	List<F> list(F fd);

	void mkdir(String filePath, boolean dir);

	void close(F fd);

	void rm(F fd);

	void format();

}
