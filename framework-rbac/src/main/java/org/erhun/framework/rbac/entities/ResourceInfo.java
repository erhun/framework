package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractHistoryEntity;
import org.erhun.framework.orm.annotations.QueryDelimiter;
import org.erhun.framework.orm.annotations.Table;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Table(value = "t_rbac_resource", alias = "资源")
public class ResourceInfo extends AbstractHistoryEntity<String> {

    private static final long serialVersionUID = 1L;
    
    //@NotEmpty(message="用户名不能为空！")
    //@Min(3)
    private String name;
    
    private String type;
    
    private String value;

    private String parentValue;
    
    @QueryDelimiter
    private String applicationId;
    
    private String memo;
    
    private Integer showIndex;
    
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getShowIndex() {
        return showIndex;
    }

    public void setShowIndex(Integer showIndex) {
        this.showIndex = showIndex;
    }

    public String getParentValue() {
        return parentValue;
    }

    public void setParentValue(String parentValue) {
        this.parentValue = parentValue;
    }
}
