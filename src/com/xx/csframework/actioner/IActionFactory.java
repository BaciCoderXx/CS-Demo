package com.xx.csframework.actioner;

public interface IActionFactory {
	String executeRequest(String action, String para) throws Exception;
	void executeResponse(String action, String para) throws Exception;
}
