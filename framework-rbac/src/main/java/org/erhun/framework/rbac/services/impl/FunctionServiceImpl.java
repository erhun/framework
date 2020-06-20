package org.erhun.framework.rbac.services.impl;

import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.domain.services.AbstractBusinessService;
import org.erhun.framework.rbac.dao.FunctionDAO;
import org.erhun.framework.rbac.entities.FunctionInfo;
import org.erhun.framework.rbac.entities.ModuleInfo;
import org.erhun.framework.rbac.services.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 * 
 */
@Service
public class FunctionServiceImpl extends AbstractBusinessService<String, FunctionInfo> implements FunctionService {

    @Autowired
    private FunctionDAO funDao;

    @Override
    public List<FunctionInfo> queryByModuleId(Object moduleId) {
        return funDao.queryByColumn(PV.of("module_id", moduleId));
    }

    @Override
    public List<FunctionInfo> queryAll() {
        return funDao.queryAll();
    }

    @Override
    public List<FunctionInfo> queryByModuleId(String moduleId, String roles){
        return funDao.queryByModuleId(moduleId, roles);
    }

    @Override
    public List<FunctionInfo> queryListFunctions(String moduleId, String roles){
        return funDao.queryByShowMode("'1','5','6','7'", moduleId, roles);
    }

    @Override
    public List<FunctionInfo> queryCreateFunctions(String moduleId, String roles){
        return funDao.queryByShowMode("'2','4','5','7'", moduleId, roles);
    }

    @Override
    public List<FunctionInfo> queryEditFunctions(String moduleId, String roles){
        return funDao.queryByShowMode("'3','4','6','7'", moduleId, roles);
    }

    @Override
    public List<ModuleInfo> queryModuleByRoleId(String roles, String moduleType) {
        // TODO Auto-generated method stub
        return null;
    }

}
