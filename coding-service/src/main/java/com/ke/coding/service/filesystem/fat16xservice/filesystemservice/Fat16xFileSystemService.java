package com.ke.coding.service.filesystem.fat16xservice.filesystemservice;

import static com.ke.coding.api.enums.Constants.BOOT_SECTOR_SIZE;
import static com.ke.coding.api.enums.Constants.BOOT_SECTOR_START;
import static com.ke.coding.api.enums.Constants.FAT_SIZE;
import static com.ke.coding.api.enums.Constants.FAT_START;
import static com.ke.coding.api.enums.Constants.ROOT_DIRECTORY_SIZE;
import static com.ke.coding.api.enums.Constants.ROOT_DIRECTORY_START;
import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_NOT_FOUND;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.bootregion.BootSector;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.RootDirectoryRegion;
import com.ke.coding.api.dto.filesystem.fat16x.fatregion.FatRegion;
import com.ke.coding.api.enums.ActionTypeEnums;
import com.ke.coding.service.disk.IDisk;
import com.ke.coding.service.filesystem.action.Action;
import com.ke.coding.service.filesystem.fat16xservice.FileSystem;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 11:17
 * @description:
 */
@Service
public class Fat16xFileSystemService implements FileSystem, ApplicationContextAware {

	@Getter
	private static ApplicationContext applicationContext;

	@Getter
	Fat16xFileSystem fat16xFileSystem;

	@Autowired
	IDisk iDisk;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (Fat16xFileSystemService.applicationContext == null) {
			Fat16xFileSystemService.applicationContext = applicationContext;
		}
	}

	/**
	 * 初始化文件系统
	 */
	@PostConstruct
	public void init() {
		fat16xFileSystem = new Fat16xFileSystem();
		fat16xFileSystem.setReservedRegion(new BootSector(iDisk.readSector(BOOT_SECTOR_START, BOOT_SECTOR_SIZE)));
		fat16xFileSystem.setRootDirectoryRegion(new RootDirectoryRegion(iDisk.readSector(ROOT_DIRECTORY_START, ROOT_DIRECTORY_SIZE)));
		fat16xFileSystem.setFatRegion(new FatRegion(iDisk.readSector(FAT_START, FAT_SIZE)));
	}

	@Override
	public FileSystemActionResult execute(Command command) {
		Action action = getBean(command.getAction(), Action.class);
		return action == null ? FileSystemActionResult.fail(ACTION_NOT_FOUND) : action.run(command, fat16xFileSystem);
	}

	public static <T> T getBean(String action, Class<T> clazz) {
		ActionTypeEnums[] actionTypeEnums = ActionTypeEnums.values();
		for (ActionTypeEnums actionTypeEnum : actionTypeEnums) {
			if (actionTypeEnum.getType().equals(action)) {
				return getApplicationContext().getBean(actionTypeEnum.getClazz(), clazz);
			}
		}
		return null;
	}


}
