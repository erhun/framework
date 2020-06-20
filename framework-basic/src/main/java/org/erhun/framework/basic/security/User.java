package org.erhun.framework.basic.security;


/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public interface User {
	
	String getId();
	
	String getCode();
	
	String getName();
	
	String getAccount();
	
	String getToken();
	
	boolean isAdmin();
	
	boolean isNormalAdmin();

}
