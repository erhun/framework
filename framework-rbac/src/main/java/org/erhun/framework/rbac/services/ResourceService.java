package org.erhun.framework.rbac.services;

import org.erhun.framework.domain.services.IBusinessService;
import org.erhun.framework.rbac.entities.ResourceInfo;

import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
public interface ResourceService extends IBusinessService<String, ResourceInfo> {

    /**
     * 
     * @param appId
     * @param type
     * @param value
     * @return
     */
    boolean deleteByAppIdAndTypeAndValue(String appId, String type, String value);
    
    /**
     * 
     * @param appId
     * @param type
     * @param value
     * @return
     */
    ResourceInfo findByAppIdAndTypeAndValue(String appId, String type, String value);
    
    /**
     * 
     * @param applicationId
     * @param userId
     * @param type
     * @return
     */
    List<ResourceInfo> queryResourceByUserId(String applicationId, String userId, String type);
    
    /**
     * 
     * @param applicationId
     * @param roleIds
     * @param pageNo
     * @param pageSize
     * @return
     */
    List <ResourceInfo> queryResourceByRoleId(String applicationId, String roleIds, Integer pageNo, Integer pageSize);
    
    /**
     * 
     * @param applicationId
     * @param roleIds
     * @return
     */
    long countResourceByRoleId(String applicationId, String roleIds);
    
}
