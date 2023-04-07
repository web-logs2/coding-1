package com.ke.coding.service.action;

import static com.ke.coding.api.enums.Constants.ROOT_PATH;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16Fd;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xSystemServiceImpl;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.FileSystemService;
import com.ke.coding.service.filesystem.inandout.Err;
import com.ke.coding.service.filesystem.inandout.In;
import com.ke.coding.service.filesystem.inandout.Out;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:40
 * @description:
 */
@Data
public abstract class AbstractAction implements Action {

	public static String currentPath = ROOT_PATH;

	public FileSystemService<Fat16Fd> fileSystemService = new Fat16xSystemServiceImpl();

	protected In in;

	protected Out out;

	protected Err err;

}
