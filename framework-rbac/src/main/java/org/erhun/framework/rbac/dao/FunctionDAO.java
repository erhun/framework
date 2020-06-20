package org.erhun.framework.rbac.dao;


import org.erhun.framework.domain.dao.BaseDAO;
import org.erhun.framework.rbac.entities.FunctionInfo;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public interface FunctionDAO extends BaseDAO<String, FunctionInfo> {
    
    public List <FunctionInfo> queryByModuleId(String moduleId, String roleIds);
    
    public List <FunctionInfo> queryByShowMode(String showMode, String moduleId, String roleIds);
    
}
