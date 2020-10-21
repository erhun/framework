package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractBizEntity;
import org.erhun.framework.orm.annotations.*;
import org.erhun.framework.orm.annotations.validator.Prefix;
import org.erhun.framework.orm.annotations.validator.Validator;
import org.erhun.framework.rbac.RbacGroup;
import org.erhun.framework.rbac.RbacRole;

import java.util.List;

/**
 *
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Table(value = "t_rbac_group", alias = "用户组")
@AttributeOverrides({
    @AttrDef(value="code",
          validators={
                @Validator(clazz = Prefix.class, value = "gp_")
          }
    )
})
public class GroupInfo extends AbstractBizEntity<String> implements RbacGroup {

    private static final long serialVersionUID = 1L;

    @Join(clazz = ApplicationInfo.class)
    @QueryDelimiter
    private String applicationId;

    @Transient
    @Join(clazz = ApplicationInfo.class, value="name")
    private String applicationText;

    private String name;

    @Ignore
    private String type;

	private String memo;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

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

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public List<RbacRole> getRoles() {
        return null;
    }
}
