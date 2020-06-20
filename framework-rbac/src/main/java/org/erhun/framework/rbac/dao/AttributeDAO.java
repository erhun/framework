package org.erhun.framework.rbac.dao;

import org.apache.ibatis.annotations.Param;
import org.erhun.framework.domain.dao.BaseDAO;
import org.erhun.framework.rbac.entities.AttributeInfo;

import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
public interface AttributeDAO extends BaseDAO<String, AttributeInfo> {

    public List<AttributeInfo> queryAttributeByRoleIdAndEntityId(@Param("roleId") String roleId, @Param("entityId") String entityId);

}
