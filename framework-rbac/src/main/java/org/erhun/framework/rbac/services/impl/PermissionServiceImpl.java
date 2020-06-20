package org.erhun.framework.rbac.services.impl;

import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.basic.utils.datetime.DateUtils;
import org.erhun.framework.basic.utils.uuid.ObjectId;
import org.erhun.framework.domain.services.AbstractBusinessService;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.rbac.RbacModule;
import org.erhun.framework.rbac.dao.*;
import org.erhun.framework.rbac.entities.ModuleInfo;
import org.erhun.framework.rbac.entities.PermissionInfo;
import org.erhun.framework.rbac.entities.RoleInfo;
import org.erhun.framework.rbac.enums.ResourceType;
import org.erhun.framework.rbac.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Service
public class PermissionServiceImpl extends AbstractBusinessService<String, PermissionInfo> implements PermissionService {

    @Autowired
    private PermissionDAO permiDao;
    
    @Autowired
    private ModuleDAO moduleDao;
    
    @Autowired
    private FunctionDAO funcDao;
    
    @Autowired
    private ResourceDAO resDao;

    @Autowired
    private EntityDAO entityDao;
    
    @Autowired
    private AttributeDAO attrDao;
    
    @Value("${application.id}")
    private String applicationId;
    
    @Override
    public PermissionInfo save(RoleInfo role, PermissionInfo entity) throws BusinessException {

        if(ResourceType.MODULE.name.equals(entity.getType())){
            RbacModule module = moduleDao.get(entity.getValue());
            if(module != null){
                if(applicationId.equals(((ModuleInfo) module).getApplicationId())){
                    if(!"rl_normal_admin".equalsIgnoreCase(role.getCode())
                        && !"rl_admin".equalsIgnoreCase(role.getCode())){
                        throw new BusinessException("只有管理员角色才能分配用户中心权限!");
                    }
                }
            }
        }

        add(entity);
        return entity;
    }
    
    @Override
    public PermissionInfo add(PermissionInfo entity) throws BusinessException {
        
        entity.setId(ObjectId.id());
        entity.setCreateTime(Instant.now());
        
        if(context != null && context.getUser() != null) {
            entity.setCreator(context.getUser().getId());
        }
        
        permiDao.add(entity);
        
        return entity;
    }
        
    @Override
    public int deleteByRoleIdAndValue(RoleInfo role, String type, String value){
        int rs = permiDao.deleteByRoleIdAndValue(role.getId(), value);
        //logRemove("[删除权限]", role, type, value);
        return rs;
    }
    

    @Override
    public List <PermissionInfo> queryByRoleId(String roleIds) {
        return permiDao.queryByRoleId(roleIds);
    }

    @Override
    public List<PermissionInfo> queryByRoleIdAndType(Object roles, Object types) {
        return permiDao.queryByColumn(PV.of("role_id", roles, "type", (String[])PV.as("function","module")));
    }
    
}