package org.erhun.framework.rbac.services;

import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.domain.services.IBusinessService;
import org.erhun.framework.rbac.entities.GroupInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
@Service
public interface GroupService extends IBusinessService<String, GroupInfo> {

    public boolean saveRole(String groupId, String roleId, String opr) throws BusinessException;

    public List<String> queryRoleIdByGroupId(String groupId);

    public boolean addRoles(String groupId, String roleId) throws BusinessException;
	
}