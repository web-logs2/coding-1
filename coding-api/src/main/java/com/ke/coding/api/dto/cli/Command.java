package com.ke.coding.api.dto.cli;

import java.util.List;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 11:36
 * @description:
 */
@Data
public class Command {

	private String action;

	private List<String> params;

	private String currentPath;

	private String originData;

	public static Command build(String action, String currentPath, List<String> params){
		Command command = new Command();
		command.setAction(action);
		command.setCurrentPath(currentPath);
		command.setParams(params);
		return command;
	}

	public static Command build(String currentPath, List<String> params){
		Command command = new Command();
		command.setCurrentPath(currentPath);
		command.setParams(params);
		return command;
	}

	public static Command build(String action, String currentPath){
		Command command = new Command();
		command.setCurrentPath(currentPath);
		command.setAction(action);
		return command;
	}

	public static Command build(String currentPath){
		Command command = new Command();
		command.setCurrentPath(currentPath);
		return command;
	}

}
