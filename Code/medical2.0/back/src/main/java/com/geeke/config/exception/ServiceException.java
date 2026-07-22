package com.geeke.config.exception;

/**
 * 服务层异常类
 *
 * @author hxy
 * @date 2017/11/15
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceException() {
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

}
