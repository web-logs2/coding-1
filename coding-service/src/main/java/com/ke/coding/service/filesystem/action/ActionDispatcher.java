package com.ke.coding.service.filesystem.action;

import static com.ke.coding.api.enums.ErrorCodeEnum.ACTION_NOT_FOUND;

import com.ke.coding.api.dto.cli.Command;
import com.ke.coding.api.dto.filesystem.FileSystemResult;
import com.ke.coding.api.enums.ActionTypeEnums;
import com.ke.coding.service.filesystem.fat16xservice.FileSystem;
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
public class ActionDispatcher implements FileSystem, ApplicationContextAware {

	@Getter
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (ActionDispatcher.applicationContext == null) {
			ActionDispatcher.applicationContext = applicationContext;
		}
	}

	@Override
	public FileSystemResult execute(Command command) {
		Action action = getBean(command.getAction(), Action.class);
		return action == null ? FileSystemResult.fail(ACTION_NOT_FOUND) : action.run(command);
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
