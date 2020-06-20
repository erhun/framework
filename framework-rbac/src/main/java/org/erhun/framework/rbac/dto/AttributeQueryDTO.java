package org.erhun.framework.rbac.dto;

import org.erhun.framework.orm.dto.BaseDTO;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2020/6/11
 */
public class AttributeQueryDTO implements BaseDTO {

    private String entityId;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
