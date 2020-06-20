package org.erhun.framework.rbac.services;

import org.erhun.framework.domain.services.IBusinessService;
import org.erhun.framework.rbac.entities.FunctionInfo;
import org.erhun.framework.rbac.entities.ModuleInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
@Service
public interface FunctionService extends IBusinessService<String, FunctionInfo> {

    public List<ModuleInfo> queryModuleByRoleId(String roles, String moduleType);

    public List<FunctionInfo> queryEditFunctions(String moduleId, String roles);

    public List<FunctionInfo> queryCreateFunctions(String moduleId, String roles);

    public List<FunctionInfo> queryListFunctions(String moduleId, String roles);

    public List<FunctionInfo> queryByModuleId(String moduleId, String roles);

    @Override
    public List<FunctionInfo> queryAll();

    public List<FunctionInfo> queryByModuleId(Object moduleId);
	
}
