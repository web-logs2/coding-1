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
	LL("ll", "LlAction"),
	MKDIR("mkdir", "MkdirAction"),
	TOUCH("touch", "TouchAction"),
	FORMAT("format", "FormatAction"),
	ECHO("echo", "EchoAction"),
	CAT("cat", "CatAction"),
	CD("cd", "CdAction"),
	PWD("pwd", "PwdAction"),
	RM("rm", "RmAction"),
	CLEAR("clear", "ClearAction"),
	DEFAULT("default", "DefaultAction"),

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
