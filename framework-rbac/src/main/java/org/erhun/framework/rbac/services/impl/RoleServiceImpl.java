package org.erhun.framework.rbac.services.impl;

import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.domain.services.AbstractBusinessService;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.rbac.dao.GroupLinkRoleDAO;
import org.erhun.framework.rbac.dao.RoleDAO;
import org.erhun.framework.rbac.dao.UserLinkRoleDAO;
import org.erhun.framework.rbac.entities.RoleInfo;
import org.erhun.framework.rbac.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Service
public class RoleServiceImpl extends AbstractBusinessService<String, RoleInfo> implements RoleService {

    private final String sysRoles = "rl_admin,rl_normal_admin";

    @Autowired
    private UserLinkRoleDAO ulrDao;
    
    @Autowired
    private GroupLinkRoleDAO rlgDao;
    
    @Autowired
    private RoleDAO roleDao;
    
    @Override
    @Transactional
    public boolean delete(String id) throws BusinessException {
        RoleInfo role = roleDao.get(id);
        if(sysRoles.contains(role.getCode())){
            throw new BusinessException("系统角色不允许删除！");
        }
        ulrDao.deleteByColumn(PV.of("role_id", id));
        rlgDao.deleteByColumn(PV.of("role_id", id));
        RoleInfo roleInfo = roleDao.get(id);
        //log(null, "REMOVE", "删除角色", "删除", "成功", "[删除]角色["+roleInfo.getName()+"("+roleInfo.getCode()+")"+"]被用户["+context.getUser().getAccount()+"]删除");
        return super.delete(id);
    }

    @Override
    public RoleInfo update(RoleInfo entity) throws BusinessException {
        if(sysRoles.contains(entity.getCode())){
            throw new BusinessException("系统角色不允许修改！");
        }
        return super.update(entity);
    }
}
