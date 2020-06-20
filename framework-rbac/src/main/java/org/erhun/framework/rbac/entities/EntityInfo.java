package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractHistoryEntity;
import org.erhun.framework.orm.annotations.*;
import org.erhun.framework.orm.annotations.validator.Duplicate;
import org.erhun.framework.orm.entities.IOrderEntity;

import java.util.List;

/**
 * 
 * @author gorilla
 *
 */
@Duplicate(value={
        @Value({"entityName", "applicationId"})
})
@Table("t_rbac_entity")
public class EntityInfo extends AbstractHistoryEntity<String> implements IOrderEntity {
	
    private static final long serialVersionUID = 1L;

    @Join(clazz = ApplicationInfo.class)
    private String applicationId;

    @Transient
    @Join(clazz = ApplicationInfo.class, value="name")
    private String applicationText;

    private String name;
	
	private String alias;

	//@AttributeDef(alias = "实体类名", item="xxx", itemkey=false)
	private String entityName;
	
    private String type;
    
    private String memo;

    private Integer showIndex;
    
    @Ignore
    private List <AttributeInfo> attributes;
	
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

    public List<AttributeInfo> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeInfo> attributes) {
        this.attributes = attributes;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
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

    @Override
    public Integer getShowIndex() {
        return showIndex;
    }

    @Override
    public void setShowIndex(Integer showIndex) {
        this.showIndex = showIndex;
    }
}
