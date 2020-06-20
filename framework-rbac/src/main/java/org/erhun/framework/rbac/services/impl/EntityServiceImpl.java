package org.erhun.framework.rbac.services.impl;

import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.domain.services.AbstractBusinessService;
import org.erhun.framework.rbac.dao.EntityDAO;
import org.erhun.framework.rbac.entities.EntityInfo;
import org.erhun.framework.rbac.services.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Service
public class EntityServiceImpl extends AbstractBusinessService<String, EntityInfo> implements EntityService {

    @Autowired
    private EntityDAO entityDao;
    
    @Override
    public EntityInfo findByEntityName(String entityName) {
        return entityDao.findByColumn(PV.nv("entity_name", entityName));
    }

    @Override
    public List<EntityInfo> queryByAppId(String appId) {
        return entityDao.queryByColumn(PV.nv("application_id", appId));
    }

}
