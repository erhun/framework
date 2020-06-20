package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractBizEntity;
import org.erhun.framework.orm.annotations.*;
import org.erhun.framework.orm.annotations.validator.NotNull;
import org.erhun.framework.orm.annotations.validator.Prefix;
import org.erhun.framework.orm.annotations.validator.Validator;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Table(value="t_rbac_role", alias="角色")
@AttributeOverrides({
    @AttributeDef(value="code",
        validators={
            @Validator(clazz = NotNull.class),
            @Validator(clazz = Prefix.class, value = "rl_")
        }
    )
})
public class RoleInfo extends AbstractBizEntity<String> {

    @Join(clazz = ApplicationInfo.class)
    @QueryDelimiter
    private String applicationId;

    @Transient
    @Join(clazz = ApplicationInfo.class, value = "name")
    private String applicationText;
    
    private String name;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplicationText() {
        return applicationText;
    }

    public void setApplicationText(String applicationText) {
        this.applicationText = applicationText;
    }

}
