package org.erhun.framework.rbac.services;


import org.erhun.framework.domain.services.IBusinessService;
import org.erhun.framework.rbac.entities.EntityInfo;

import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
public interface EntityService extends IBusinessService<String, EntityInfo> {

    public EntityInfo findByEntityName(String entityName);

    List<EntityInfo> queryByAppId(String appId);
}
