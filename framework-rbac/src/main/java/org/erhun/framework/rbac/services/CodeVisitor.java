package org.erhun.framework.rbac.services;

/**
 * 
 * @author weichao (gorilla@aliyun.com) 
 */
public interface CodeVisitor {
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	public boolean codeExists(String code);
	
}
