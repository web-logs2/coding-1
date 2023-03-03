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
public class FileSystemActionResult {

	public FileSystemActionResult(boolean success, String status, String result) {
		this.success = success;
		this.status = status;
		this.result = result;
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
	private String result;

	public static FileSystemActionResult fail(String status, String result){
		return new FileSystemActionResult(false, status, result);
	}

	public static FileSystemActionResult fail(ErrorCodeEnum errorCodeEnum){
		return new FileSystemActionResult(false, errorCodeEnum.status(), errorCodeEnum.message());
	}
	public static FileSystemActionResult success(){
		return new FileSystemActionResult(true, SYSTEM_SUCCESS.status(), SYSTEM_SUCCESS.message());
	}
}
