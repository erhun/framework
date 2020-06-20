package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractBizEntity;
import org.erhun.framework.orm.annotations.AttributeDef;
import org.erhun.framework.orm.annotations.Condition;
import org.erhun.framework.orm.annotations.Table;
import org.erhun.framework.orm.annotations.Transient;
import org.erhun.framework.orm.annotations.validator.Unique;

import java.util.Date;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Table(value="t_rbac_user", alias="用户信息")
public class UserInfo extends AbstractBizEntity<String> {
    
    private static final long serialVersionUID = 1L;

    private String name;
    
    private String alias;
    
    private String nickname;
    
    private String email;
    
    @Unique
    @AttributeDef(alias="手机号")
    private String mobile;
    
    private String photo;
    
    @Unique
    @AttributeDef(updatable=false, alias="帐号")
    private String account;
    
    private String password;
    
    @AttributeDef(creatable=false)
    private String password2;
    
    @AttributeDef(updatable=false)
    private Integer errorCount;
    
    @AttributeDef(creatable=false, updatable=false)
    private Date loginTime;
    
    private Boolean admin;
    
    /**
     * 1 活动， 2 锁定
     */
    @AttributeDef(updatable=false, item = "YHZT", text=true)
    private String status;
    
    /**
     * 1 内部、2 外部
     */
    private String type;
    
    private String memo;

    @Transient
    @Condition("t.id in(select t.user_id from uac_user_link_group t inner join uac_group g on g.is_valid='1' and g.id=t.group_id where g.application_id in(${applicationId}))")
    private String applicationId;
    
    @Transient
    private String groupId;

    private Boolean online;
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Boolean isAdmin() {
        return admin;
    }
    public void setAdmin( Boolean admin ) {
        this.admin = admin;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getPassword2() {
        return password2;
    }
    public void setPassword2(String password2) {
        this.password2 = password2;
    }
    public Integer getErrorCount() {
        return errorCount;
    }
    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }
    public Date getLoginTime() {
        return loginTime;
    }
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
    public String getMemo() {
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getApplicationId() {
        return applicationId;
    }
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Boolean isOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
