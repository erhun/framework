package org.erhun.framework.rbac.enums;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public enum ResourceType {

	APPLICATION(1, "application"),

	MODULE(2, "module"),
	
	FUNCTION(3, "function"),

	ENTITY(6, "entity"),

	ATTRIBUTE(4, "attribute"),

	RESOURCE(5, "resource");

	public final String name;

	public final int value;

	private ResourceType(int value, String name) {
		this.name = name;
		this.value = value;
	}
}
