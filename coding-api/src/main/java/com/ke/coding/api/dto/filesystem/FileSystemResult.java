package com.ke.coding.api.dto.filesystem;

import static com.ke.coding.api.enums.ErrorCodeEnum.SYSTEM_SUCCESS;

import com.ke.coding.api.enums.ErrorCodeEnum;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/1 12:11
 * @description:
 */
@Data
public class FileSystemResult {

	public FileSystemResult(boolean success, String status) {
		this.success = success;
		this.status = status;
	}

	public FileSystemResult(boolean success, String status, String data) {
		this.success = success;
		this.status = status;
		this.data = data;
	}

	/**
	 * 成功
	 */
	private boolean success;

	/**
	 * 状态
	 */
	private String status;
	/**
	 * 结果
	 */
	private String data;

	public static FileSystemResult fail(String status, String message) {
		return new FileSystemResult(false, status, message);
	}

	public static FileSystemResult fail(ErrorCodeEnum errorCodeEnum) {
		return new FileSystemResult(false, errorCodeEnum.status(), errorCodeEnum.message());
	}

	public static FileSystemResult success() {
		return new FileSystemResult(true, SYSTEM_SUCCESS.status(), SYSTEM_SUCCESS.message());
	}

	public static FileSystemResult success(String data) {
		FileSystemResult fileSystemResult = new FileSystemResult(true, SYSTEM_SUCCESS.status(), SYSTEM_SUCCESS.message());
		fileSystemResult.setData(data);
		return fileSystemResult;
	}
}
