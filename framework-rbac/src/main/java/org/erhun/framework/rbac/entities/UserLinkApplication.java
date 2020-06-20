package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.BaseEntity;
import org.erhun.framework.orm.annotations.Table;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Table("t_rbac_user_link_application")
public class UserLinkApplication extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;
    
    private String userId;
    
    private String applicationId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

}
