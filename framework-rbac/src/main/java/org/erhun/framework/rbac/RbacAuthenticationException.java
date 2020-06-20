package org.erhun.framework.rbac;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class RbacAuthenticationException extends RuntimeException {

	public RbacAuthenticationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	public RbacAuthenticationException(Throwable arg0) {
		super(arg0);
	}
	public RbacAuthenticationException(String message){
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
