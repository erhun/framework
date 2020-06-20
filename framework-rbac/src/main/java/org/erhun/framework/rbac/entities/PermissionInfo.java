package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractHistoryEntity;
import org.erhun.framework.orm.annotations.Table;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
@Table(value = "t_rbac_permission", alias = "权限")
public class PermissionInfo extends AbstractHistoryEntity<String> {
	
    private String permission;
    
    private String value;
    
    private String type;
    
    private String roleId;
    
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
	
}
