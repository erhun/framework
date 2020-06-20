package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.BaseEntity;
import org.erhun.framework.orm.annotations.Table;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
@Table("t_rbac_group_link_role")
public class GroupLinkRole extends BaseEntity<String> {
    
    private String roleId;
    
    private String groupId;

    public GroupLinkRole(String groupId, String roleId) {
        this.roleId = roleId;
        this.groupId = groupId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


}
