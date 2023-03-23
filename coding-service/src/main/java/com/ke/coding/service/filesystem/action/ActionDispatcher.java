package com.ke.coding.service.filesystem.action;

import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_NOT_FOUND;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemActionResult;
import com.ke.coding.api.dto.filesystem.fat16x.Fat16xFileSystem;
import com.ke.coding.api.enums.ActionTypeEnums;
import com.ke.coding.service.filesystem.fat16xservice.FileSystem;
import com.ke.coding.service.filesystem.fat16xservice.filesystemservice.Fat16xSystemServiceImpl;
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
public class ActionDispatcher implements FileSystem, ApplicationContextAware {

	@Getter
	private static ApplicationContext applicationContext;

	@Getter
	Fat16xFileSystem fat16xFileSystem;

	@Autowired
	Fat16xSystemServiceImpl fat16XSystemServiceImpl;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (ActionDispatcher.applicationContext == null) {
			ActionDispatcher.applicationContext = applicationContext;
		}
	}

	/**
	 * 初始化文件系统
	 */
	@PostConstruct
	public void init() {
		fat16xFileSystem = fat16XSystemServiceImpl.getFat16xFileSystem();
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