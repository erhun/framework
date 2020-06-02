package org.erhun.framework.orm.exception;

/**
 * 
 * @author weichao
 *
 */
public class ParsedException extends RuntimeException {
	
	private static final long serialVersionUID = 8529292638737423181L;

	public ParsedException(String error) {
		super(error);
	}
	
	public ParsedException(Throwable cause) {
		super(cause);		
	}
	
	public ParsedException(String error, Throwable cause) {
		super(error, cause);
	}
}
