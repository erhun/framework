package org.erhun.framework.rbac.services.impl;

import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.services.AbstractBusinessService;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.rbac.dao.ModuleDAO;
import org.erhun.framework.rbac.entities.ModuleInfo;
import org.erhun.framework.rbac.services.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Service
public class ModuleServiceImpl extends AbstractBusinessService<String, ModuleInfo> implements ModuleService {

    @Autowired
    private ModuleDAO moduleDao;

    @Override
    public ModuleInfo add(ModuleInfo entity) throws BusinessException {
        if(StringUtils.isBlank(entity.getParentId())){
            entity.setParentId("0");
        }
        return super.add(entity);
    }

    @Override
    public ModuleInfo update(ModuleInfo entity) throws BusinessException {
        if(StringUtils.isBlank(entity.getParentId())){
            entity.setParentId("0");
        }
        return super.update(entity);
    }

    @Override
    public boolean delete(String id) throws BusinessException {
        long count = moduleDao.count(PV.of("parent_id", id, "is_valid", 1));
        if(count > 0) {
            throw new BusinessException("该模块包含下级模块不允许删除!");
        }
        return super.delete(id);
    }
    
    @Override
    public List <ModuleInfo> queryIdAndNameByAppId(Object appId){
        return moduleDao.queryByColumn("id,name", PV.of("application_id", appId, "type", "1"));
    }

    @Override
    public List<ModuleInfo> queryByAppId(Object appId) {
        return moduleDao.queryByColumn(PV.of("application_id", appId));
    }

    @Override
    public List<ModuleInfo> queryByRoleId(String applicationId, String roleId, String moduleType) {
        return moduleDao.queryByRoleId(applicationId, StringUtils.singleQuotes(roleId), moduleType);
    }

}
