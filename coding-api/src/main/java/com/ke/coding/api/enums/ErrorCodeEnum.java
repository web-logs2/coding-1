package com.ke.coding.api.enums;

public enum ErrorCodeEnum {

	SYSTEM_ERROR("0", "系统异常"),
	SYSTEM_SUCCESS("1", "操作成功"),
	//
	INSUFFICIENT_SPACE("1001", "空间不足"),
	DIR_LENGTH_TOO_LONG("1002", "目录名称过长"),
	FILENAME_LENGTH_TOO_LONG("1003", "文件名称过长"),
	DIR_DATA_ERROR("1004", "目录数据异常"),
	NO_SUCH_FILE_OR_DIRECTORY("1005", "No such file or directory"),
	ACTION_NOT_FOUND("1006", "当前命令不存在"),
	ACTION_ERROR("1007", "当前命令存在错误"),
	NOT_RM_ROOT("1008", "根目录无法删除"),


	FILE_HAS_OPEN("2001", "文件被占用，请稍后再试"),

	;

	/**
	 * 返回码
	 */
	private final String status;
	/**
	 * 消息
	 */
	private final String message;

	ErrorCodeEnum(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public static String message(String status) {
		ErrorCodeEnum[] secretEnums = values();
		for (ErrorCodeEnum secretEnum : secretEnums) {
			if (secretEnum.status().equals(status)) {
				return secretEnum.message();
			}
		}
		return null;
	}

	public String status() {
		return status;
	}

	public String message() {
		return message;
	}

	public static ErrorCodeEnum getEnum(String status) {
		ErrorCodeEnum[] secretEnums = values();
		for (ErrorCodeEnum secretEnum : secretEnums) {
			if (secretEnum.status().equals(status)) {
				return secretEnum;
			}
		}
		return null;
	}

}
