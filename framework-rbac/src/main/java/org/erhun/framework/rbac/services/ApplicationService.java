package org.erhun.framework.rbac.services;


import org.erhun.framework.domain.services.IBusinessService;
import org.erhun.framework.rbac.entities.ApplicationInfo;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
public interface ApplicationService extends IBusinessService<String, ApplicationInfo> {

    /**
     * 跟据appId查找
     * @param appId
     * @return
     */
    ApplicationInfo findByAppId(String appId);


    /**
     * 跟据code查找
     * @param code
     * @return
     */
    ApplicationInfo findByCode(String code);
}
