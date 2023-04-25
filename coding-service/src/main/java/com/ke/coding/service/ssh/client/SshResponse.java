package com.ke.coding.service.ssh.client;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/4/24 10:20
 * @description:
 */
public  class SshResponse {

	private String stdOutput;
	private String errOutput;
	private int returnCode;

	SshResponse(String stdOutput, String errOutput, int returnCode) {
		this.stdOutput = stdOutput;
		this.errOutput = errOutput;
		this.returnCode = returnCode;
	}

	public String getStdOutput() {
		return stdOutput;
	}

	public String getErrOutput() {
		return errOutput;
	}

	public int getReturnCode() {
		return returnCode;
	}

}

