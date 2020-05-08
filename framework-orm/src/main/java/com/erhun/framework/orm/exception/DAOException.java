package com.erhun.framework.orm.exception;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class DAOException extends RuntimeException {

	public DAOException() {
		super();
	}
	public DAOException(String arg0, Throwable arg1) {
		super( arg0, arg1);
	}
	public DAOException(Throwable arg0) {
		super(arg0);
	}
	public DAOException(String message){
		super( "error:" + message );
	}
	public Throwable getCause() {
		return super.getCause();
	}

	public String getMessage() {
		return super.getMessage();
	}

	public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();
	}

	public String toString() {
		return super.toString();
	}
	
}
