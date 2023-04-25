package com.ke.coding.api.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/7 10:45
 * @description:
 */
public enum ActionTypeEnums {
	LL("ll", "llAction"),
	MKDIR("mkdir", "mkdirAction"),
	TOUCH("touch", "touchAction"),
	FORMAT("format", "formatAction"),
	ECHO("echo", "echoAction"),
	CAT("cat", "catAction"),
	CD("cd", "cdAction"),
	PWD("pwd", "pwdAction"),
	RM("rm", "rmAction"),
	CLEAR("clear", "clearAction"),
	DEFAULT("default", "defaultAction"),

	;

	@Getter@Setter
	private String type;
	@Getter@Setter
	private String clazz;

	ActionTypeEnums(String type, String clazz) {
		this.type = type;
		this.clazz = clazz;
	}

	public static ActionTypeEnums getByType(String type) {
		return Arrays.stream(ActionTypeEnums.values()).filter(x -> x.getType().equals(type)).findFirst().orElse(DEFAULT);
	}
}
