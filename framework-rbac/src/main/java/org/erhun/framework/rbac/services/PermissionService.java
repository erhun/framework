package org.erhun.framework.rbac.services;


import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.domain.services.IBusinessService;
import org.erhun.framework.rbac.entities.RoleInfo;
import org.erhun.framework.rbac.entities.PermissionInfo;

import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
public interface PermissionService extends IBusinessService<String, PermissionInfo> {

    /**
     * 
     * @param role
     * @param type TODO
     * @param value
     * @return
     */
    int deleteByRoleIdAndValue(RoleInfo role, String type, String value);
    
    /**
     * 
     * @param roleIds
     * @return
     */
    List <PermissionInfo> queryByRoleId(String roleIds);
    
    /**
     * 
     * @param roles
     * @param types
     * @return
     */
    List <PermissionInfo> queryByRoleIdAndType(Object roles, Object types);

    /**
     *
     * @param role
     * @param entity
     * @return
     * @throws BusinessException
     */
    PermissionInfo save(RoleInfo role, PermissionInfo entity) throws BusinessException;
    
}