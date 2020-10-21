package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractBizEntity;
import org.erhun.framework.orm.annotations.*;
import org.erhun.framework.orm.annotations.validator.Duplicate;
import org.erhun.framework.orm.annotations.validator.Prefix;
import org.erhun.framework.orm.annotations.validator.Validator;
import org.erhun.framework.orm.entities.IOrderEntity;
import org.erhun.framework.rbac.RbacFunction;
import org.erhun.framework.rbac.RbacModule;

import java.util.List;

/**
 *
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Duplicate(value={
    @Value({"code", "applicationId"})
})
@AttributeOverrides({
    @AttrDef(value="code",
        validators={
            @Validator(clazz = Prefix.class, value = "M")
        }
    )
})
@Table(value = "t_rbac_module", alias = "模块信息")
public class ModuleInfo extends AbstractBizEntity<String> implements RbacModule, IOrderEntity {

    @Join(clazz = ApplicationInfo.class)
    @QueryDelimiter
    private String applicationId;

    @Transient
    @Join(clazz = ApplicationInfo.class, value="name")
    private String applicationText;

    private String name;

    /**
     * 1 普通模块, 2 系统模块, 3功能模块
     */
    @AttrDef(alias="模块类型")
    private String type;

    @AttrDef(alias="图标")
    private String icon;

    @AttrDef(alias="模块路径")
    private String path;

    private String url;

    private String parentId;

    private Boolean shortcut;

    private Integer showIndex;

    private String memo;

    @Ignore
    private List <RbacFunction> functions;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean isShortcut() {
        return shortcut;
    }

    public void setShortcut( Boolean shortcut ) {
        this.shortcut = shortcut;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    @Override
    public List <RbacFunction> getFunctions() {
        return functions;
    }

    public void setFunctions( List <RbacFunction> functions ) {
        this.functions = functions;
    }

    @Override
    public Integer getShowIndex() {
        return showIndex;
    }

    @Override
    public void setShowIndex(Integer showIndex) {
        this.showIndex = showIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
