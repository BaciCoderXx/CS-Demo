package com.xx.csframework.core;

public class NonSocketException extends Exception {
	private static final long serialVersionUID = -7989556183045905954L;

	public NonSocketException() {
	}

	public NonSocketException(String message) {
		super(message);
	}

	public NonSocketException(Throwable cause) {
		super(cause);
	}

	public NonSocketException(String message, Throwable cause) {
		super(message, cause);
	}

	public NonSocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
