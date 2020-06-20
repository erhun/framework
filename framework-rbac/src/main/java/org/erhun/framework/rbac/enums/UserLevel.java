package org.erhun.framework.rbac.enums;

/**
 * 
 * @author weichao <gorilla@aliyun.com>
 * @date 2017年6月2日
 */
public enum UserLevel {

	Normal("普通用户", "1"),

	Administrator("管理员", "2");

	private String name;

	private String value;

	private UserLevel(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
