package com.ke.coding.api.exception;

import com.ke.coding.api.enums.ErrorCodeEnum;
import lombok.Data;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/4 18:39
 * @description:
 */
@Data
public class CodingException extends RuntimeException{

	/**
	 * Constructs a new runtime exception with {@code null} as its detail message.  The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public CodingException(ErrorCodeEnum errorCode) {
		this.errorCode = errorCode;
	}

	private ErrorCodeEnum errorCode;

}
