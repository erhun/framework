package org.erhun.framework.rbac.services.impl;

import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.domain.services.AbstractBusinessService;
import org.erhun.framework.rbac.entities.ApplicationInfo;
import org.erhun.framework.rbac.services.ApplicationService;
import org.springframework.stereotype.Service;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Service
public class ApplicationServiceImpl extends AbstractBusinessService<String, ApplicationInfo> implements ApplicationService {

    @Override
    public ApplicationInfo findByAppId(String appId) {
        return baseDao.findByColumn(PV.nv("app_id", appId));
    }

    @Override
    public ApplicationInfo findByCode(String appId) {
        return baseDao.findByColumn(PV.nv("code", appId));
    }
    
}
