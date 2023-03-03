package com.ke.coding.service.filesystem;


/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 12:06
 * @description:
 */
public abstract class AbstractFileSystem implements FileSystem{


	/**
	 * 空闲目录空间
	 *
	 * @return boolean
	 */
	protected abstract boolean hasIdleRootDirectorySpace();
}
