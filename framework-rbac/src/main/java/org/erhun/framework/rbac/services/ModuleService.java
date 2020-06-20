package org.erhun.framework.rbac.services;

import org.erhun.framework.domain.services.IBusinessService;
import org.erhun.framework.rbac.entities.ModuleInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
@Service
public interface ModuleService extends IBusinessService<String, ModuleInfo> {

    List <ModuleInfo> queryIdAndNameByAppId(Object appId);
    
    List <ModuleInfo> queryByAppId(Object appId);

    List<ModuleInfo> queryByRoleId(String applicationId, String roleId, String moduleType);
}
