package com.ke.coding.api.dto.cli;

import java.util.List;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 11:36
 * @description:
 */
@Data
public class CommandContext {

	private String action;

	private List<String> params;

	private String currentPath;

	private String originData;

	public static CommandContext build(String action, String currentPath, List<String> params){
		CommandContext commandContext = new CommandContext();
		commandContext.setAction(action);
		commandContext.setCurrentPath(currentPath);
		commandContext.setParams(params);
		return commandContext;
	}

	public static CommandContext build(String currentPath, List<String> params){
		CommandContext commandContext = new CommandContext();
		commandContext.setCurrentPath(currentPath);
		commandContext.setParams(params);
		return commandContext;
	}

	public static CommandContext build(String action, String currentPath){
		CommandContext commandContext = new CommandContext();
		commandContext.setCurrentPath(currentPath);
		commandContext.setAction(action);
		return commandContext;
	}

	public static CommandContext build(String currentPath){
		CommandContext commandContext = new CommandContext();
		commandContext.setCurrentPath(currentPath);
		return commandContext;
	}

}
