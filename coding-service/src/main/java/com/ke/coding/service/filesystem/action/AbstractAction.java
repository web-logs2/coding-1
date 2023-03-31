package com.ke.coding.service.filesystem.action;

import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:40
 * @description:
 */
public abstract class AbstractAction implements Action {

	@Autowired
	public FileSystemService fileSystemService;


	public boolean hasIdleRootDirectorySpace(Fat16xFileSystem fat16xFileSystem) {
		return fat16xFileSystem.getRootDirectoryRegion().haveIdleSpace();
	}

}
