package org.erhun.framework.rbac.enums;

/**
 * 
 * @author weichao <gorilla@aliyun.com>
 * @date 2017年6月2日
 */
public enum PermissionType {
	
	/** 
	 * 
	 * 禁止 
	 */
	REJECT("禁止", 0),

	/** 
	 * 
	 * 只读 
	 */
	READONLY("只读", 1),

	/** 
	 * 
	 * 控制 
	 */
	ALL("控制", 9);

	public final String name;

	public final int value;

	private PermissionType(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public static PermissionType fromValue(int value) {
		switch (value) {
		case 0:
			return REJECT;
		case 1:
			return READONLY;
		case 9:
			return ALL;
		default:
			StringBuilder err = new StringBuilder();
			err.append("No enum const class ");
			err.append(PermissionType.class.getName());
			err.append(" which value is : ").append(value);
			throw new IllegalArgumentException(err.toString());
		}
	}

	public static PermissionType fromName(String name) {
		if (REJECT.name.equals(name))
			return REJECT;
		if (READONLY.name.equals(name))
			return READONLY;
		if (ALL.name.equals(name))
			return ALL;
		
		StringBuilder err = new StringBuilder();
		err.append("No enum const class ");
		err.append(PermissionType.class.getName());
		err.append(name);
		throw new IllegalArgumentException(err.toString());
	}


}
