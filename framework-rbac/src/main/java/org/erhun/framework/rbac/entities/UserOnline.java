package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.BaseEntity;
import org.erhun.framework.orm.annotations.Join;
import org.erhun.framework.orm.annotations.QueryDelimiter;
import org.erhun.framework.orm.annotations.Table;
import org.erhun.framework.orm.annotations.Transient;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Table(value="t_rbac_user_online", alias="用户在线状态")
public class UserOnline extends BaseEntity<String> {
    
    @Join(clazz = UserInfo.class)
    @QueryDelimiter
    private String userId;
    
    @Transient
    @Join(clazz = UserInfo.class, value = "name")
    private String userText;

    @Join(clazz = ApplicationInfo.class)
    @QueryDelimiter
    private String applicationId;

    @Transient
    @Join(clazz = ApplicationInfo.class, value = "name")
    private String applicationText;
    
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationText() {
        return applicationText;
    }

    public void setApplicationText(String applicationText) {
        this.applicationText = applicationText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }
}
