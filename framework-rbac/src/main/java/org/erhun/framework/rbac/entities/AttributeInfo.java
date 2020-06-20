package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractHistoryEntity;
import org.erhun.framework.orm.annotations.Join;
import org.erhun.framework.orm.annotations.Table;
import org.erhun.framework.orm.annotations.Transient;
import org.erhun.framework.orm.annotations.validator.NotNull;
import org.erhun.framework.orm.entities.IOrderEntity;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 * 
 */
@Table("t_rbac_attribute")
public class AttributeInfo extends AbstractHistoryEntity<String> implements IOrderEntity {

    @NotNull
    @Join(clazz = EntityInfo.class)
    private String entityId;

    @Transient
    @Join(clazz = EntityInfo.class, value="name")
    private String entityText;

    @NotNull
    private String name;
    
    private String alias;

    private String memo;

    private Integer showIndex;

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

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityText() {
        return entityText;
    }

    public void setEntityText(String entityText) {
        this.entityText = entityText;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public Integer getShowIndex() {
        return showIndex;
    }

    @Override
    public void setShowIndex(Integer showIndex) {
        this.showIndex = showIndex;
    }
}
