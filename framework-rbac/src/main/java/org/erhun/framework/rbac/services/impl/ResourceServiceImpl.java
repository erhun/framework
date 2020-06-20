package org.erhun.framework.rbac.services.impl;

import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.domain.services.AbstractBusinessService;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.orm.Limits;
import org.erhun.framework.rbac.dao.ResourceDAO;
import org.erhun.framework.rbac.entities.ResourceInfo;
import org.erhun.framework.rbac.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Service
public class ResourceServiceImpl extends AbstractBusinessService<String, ResourceInfo> implements ResourceService {

    @Autowired
    private ResourceDAO resourceDao;
    
    @Override
    public ResourceInfo add(ResourceInfo entity) throws BusinessException {
        long count = resourceDao.duplicate(entity.getId(), PV.of("type", entity.getType(), "value", entity.getValue()));
        if(count > 0) {
            throw new BusinessException("资源ID重复");
        }
        return super.add(entity);
    }
    
    @Override
    public ResourceInfo update(ResourceInfo entity) throws BusinessException {
        long count = resourceDao.duplicate(entity.getId(), PV.of("type", entity.getType(), "value", entity.getValue()));
        if(count > 0) {
            throw new BusinessException("资源ID重复");
        }
        return super.update(entity);
    }
    
    @Override
    public boolean deleteByAppIdAndTypeAndValue(String appId, String type, String value) {
        int rs = resourceDao.deleteByColumn(PV.of("application_id", appId, "type", type, "value", value));
        return rs > 0;
    }

    @Override
    public ResourceInfo findByAppIdAndTypeAndValue(String appId, String type, String value) {
        return resourceDao.findByColumn(PV.of("application_id", appId, "type", type, "value", value));
    }
    
    @Override
    public List<ResourceInfo> queryResourceByUserId(String applicationId, String userId, String resType) {
        return resourceDao.queryByUserId(applicationId, userId, resType);
    }

    @Override
    public List<ResourceInfo> queryResourceByRoleId(String applicationId, String roleIds, Integer pageNo, Integer pageSize) {
        return resourceDao.queryByRoleId(applicationId, roleIds, Limits.of(pageNo, pageSize));
    }

    @Override
    public long countResourceByRoleId(String applicationId, String roleIds) {
        return resourceDao.countByRoleId(applicationId, roleIds);
    }
}
