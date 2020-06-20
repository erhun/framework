package org.erhun.framework.domain.services;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class BusinessException extends RuntimeException {

	public BusinessException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	public BusinessException(Throwable arg0) {
		super(arg0);
	}
	public BusinessException(String message){
		super(message);
	}
	@Override
	public Throwable getCause() {
		return super.getCause();
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
