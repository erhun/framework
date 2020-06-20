package org.erhun.framework.rbac.services.impl;

import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.basic.utils.uuid.ObjectId;
import org.erhun.framework.domain.services.AbstractBusinessService;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.rbac.dao.GroupDAO;
import org.erhun.framework.rbac.dao.GroupLinkRoleDAO;
import org.erhun.framework.rbac.dao.RoleDAO;
import org.erhun.framework.rbac.dao.UserLinkGroupDAO;
import org.erhun.framework.rbac.entities.GroupInfo;
import org.erhun.framework.rbac.entities.GroupLinkRole;
import org.erhun.framework.rbac.entities.RoleInfo;
import org.erhun.framework.rbac.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 * 
 */
@Service
public class GroupServiceImpl extends AbstractBusinessService<String, GroupInfo> implements GroupService {

    private final String sysGroups = "gp_admin,gp_normal_admin";

    @Autowired
    private GroupLinkRoleDAO rlgDao;
    
    @Autowired
    private UserLinkGroupDAO ulgDao;
    
    @Autowired
    private GroupDAO groupDao;
    
    @Autowired
    private RoleDAO roleDao;
    
    @Override
    @Transactional
    public boolean delete(String id) throws BusinessException {
        GroupInfo group = groupDao.get(id);
        if(sysGroups.contains(group.getCode())){
            throw new BusinessException("系统用户组不允许删除！");
        }
        rlgDao.deleteByColumn(PV.of("group_id", id));
        ulgDao.deleteByColumn(PV.of("groupId", id));
        return super.delete(id);
    }

    @Override
    public GroupInfo update(GroupInfo entity) throws BusinessException {
        if(sysGroups.contains(entity.getCode())){
            throw new BusinessException("系统用户组不允许修改！");
        }
        return super.update(entity);
    }

    @Override
    public boolean addRoles(String groupId, String roleId) throws BusinessException{
        String ar [] = roleId.split(",");
        for(String rId : ar ){
            GroupLinkRole rlg = new GroupLinkRole(groupId, rId);
            rlg.setId(ObjectId.id());
            rlgDao.add(rlg);
            GroupInfo group = groupDao.get(groupId);
            RoleInfo role = roleDao.get(roleId);
            if(StringUtils.isNotEmpty(group.getApplicationId()) && !group.getApplicationId().equals(role.getApplicationId())) {
                throw new BusinessException("用户组必须与角色所属同一个应用");
            }
            //log(rlg, "GROUP->ROLE", "分配角色", "设置", "成功", "[分配角色]组["+group.getName()+"("+group.getCode()+")"+"]被用户["+context.getUser().getAccount()+"]分配角色["+role.getName()+"("+role.getCode()+")"+"]");
        }
        return true;
    }

    @Override
    public List<String> queryRoleIdByGroupId(String groupId) {
        return rlgDao.queryByColumn("role_id", PV.of("group_id", groupId));
    }

    @Override
    public boolean saveRole(String groupId, String roleId, String opr) throws BusinessException {
        if ("1".equals(opr)) {
            return addRoles(groupId, roleId);
        } else {
            GroupLinkRole glr = new GroupLinkRole(groupId, roleId);
            int rs = rlgDao.deleteByColumn(PV.of("group_id", groupId, "role_id", roleId));
            GroupInfo group = groupDao.get(groupId);
            RoleInfo role = roleDao.get(roleId);

            if("rl_admin".equals(role.getCode()) || "rl_normal_admin".equals(role.getCode())){
                if(!"gp_admin".equals(group.getCode()) && !"gp_normal_admin".equals(group.getCode())) {
                    throw new BusinessException("普通用户组不能分配管理员角色");
                }
            }

            if("rl_admin".equals(role.getCode())){
                if(!"gp_admin".equalsIgnoreCase(group.getCode())) {
                    throw new BusinessException("只有管理员用户组才能分配管理员角色");
                }
            }

            if("rl_normal_admin".equals(role.getCode())){
                if(!"gp_admin".equals(group.getCode()) && !"gp_normal_admin".equals(group.getCode())) {
                    throw new BusinessException("只有管理员用户组才能分配管理员角色");
                }
            }

            //log(glr, "GROUP->ROLE", "分配角色", "设置", "成功", "[分配角色]组["+group.getName()+"("+group.getCode()+")"+"]被用户["+context.getUser().getAccount()+"]移除角色["+role.getName()+"("+role.getCode()+")"+"]");
            return rs != 0;
        }
    }

}
