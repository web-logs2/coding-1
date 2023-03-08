package com.ke.coding.service.filesystem.impl;

import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY;
import static com.ke.coding.api.enums.Constants.ATTRIBUTE_DIRECTORY_POS;
import static com.ke.coding.api.enums.Constants.DIRECTORY_ENTRY_SIZE;
import static com.ke.coding.api.enums.Constants.FAT_NC_END_OF_FILE;
import static com.ke.coding.api.enums.Constants.PATH_SPLIT;
import static com.ke.coding.api.enums.Constants.PER_CLUSTER_SECTOR;
import static com.ke.coding.api.enums.Constants.PER_SECTOR_BYTES;
import static com.ke.coding.api.enums.Constants.ROOT_PATH;
import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_NOT_FOUND;
import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_DATA_ERROR;
import static com.ke.coding.api.enums.ErrorCodeEnum.DIR_LENGTH_TOO_LONG;
import static com.ke.coding.api.enums.ErrorCodeEnum.FILENAME_LENGTH_TOO_LONG;
import static com.ke.coding.api.enums.ErrorCodeEnum.INSUFFICIENT_SPACE;
import static com.ke.coding.api.enums.ErrorCodeEnum.NO_SUCH_FILE_OR_DIRECTORY;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataCluster;
import com.ke.coding.api.dto.filesystem.fat16x.dataregion.DataSector;
import com.ke.coding.api.dto.filesystem.fat16x.directoryregion.DirectoryEntry;
import com.ke.coding.api.enums.ActionTypeEnums;
import com.ke.coding.service.filesystem.FileSystem;
import com.ke.coding.service.filesystem.action.Action;
import com.ke.risk.safety.common.util.json.JsonUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 11:17
 * @description:
 */
@Service
public class Fat16xFileSystemService implements FileSystem , ApplicationContextAware {

	@Getter
	private static ApplicationContext applicationContext;

	Fat16xFileSystem fat16xFileSystem;

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
	void init() {
		fat16xFileSystem = new Fat16xFileSystem();
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
