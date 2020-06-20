package org.erhun.framework.rbac.services.impl;

import org.erhun.framework.domain.services.BusinessContext;
import org.erhun.framework.rbac.dao.AttributeDAO;
import org.erhun.framework.rbac.entities.AttributeInfo;
import org.erhun.framework.rbac.services.RbacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Service
public class RbacServiceImpl implements RbacService {

    @Autowired
    private AttributeDAO attributeDao;

    @Autowired
    protected BusinessContext context;

    @Override
    public List <AttributeInfo> queryAttributeByRoleIdAndEntityId(String roleId, String entityId){
        return attributeDao.queryAttributeByRoleIdAndEntityId(roleId, entityId);
    }

}
