package com.ke.coding.service.filesystem.action;

import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xSystemServiceImpl;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.FileSystemService;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:40
 * @description:
 */
public abstract class AbstractAction implements Action {

	public FileSystemService fileSystemService = new Fat16xSystemServiceImpl();

}
