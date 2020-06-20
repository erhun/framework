package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.BaseEntity;
import org.erhun.framework.orm.annotations.Table;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Table("t_rbac_user_link_group")
public class UserLinkGroup extends BaseEntity<String> {

    private String userId;
    
    private String groupId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
