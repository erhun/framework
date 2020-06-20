package org.erhun.framework.rbac.dao;

import org.apache.ibatis.annotations.Param;
import org.erhun.framework.domain.dao.BaseDAO;
import org.erhun.framework.rbac.entities.ModuleInfo;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
public interface ModuleDAO extends BaseDAO<String, ModuleInfo> {
    
    ModuleInfo findByCode(String code);
    
    List <ModuleInfo> queryByType(String type);
    
    List <ModuleInfo> queryRootModules();
    
    List <ModuleInfo> queryByRoleId(@Param("applicationId") String applicationId, @Param("roleId") String roleId, @Param("moduleType") String moduleType);

    
}
