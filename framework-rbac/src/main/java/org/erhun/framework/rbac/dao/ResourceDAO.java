package org.erhun.framework.rbac.dao;

import org.apache.ibatis.annotations.Param;
import org.erhun.framework.domain.dao.BaseDAO;
import org.erhun.framework.orm.Limits;
import org.erhun.framework.rbac.entities.ResourceInfo;

import java.util.List;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
public interface ResourceDAO extends BaseDAO<String, ResourceInfo> {
	
    public List <ResourceInfo> queryByRoleId(@Param("applicationId") String applicationId, @Param("roleId") String roleId, @Param("limit") Limits limit);
    
    public long countByRoleId(@Param("applicationId") String applicationId, @Param("roleId") String roleId);

    public List <ResourceInfo> queryByUserId(@Param("applicationId") String applicationId, @Param("userId") String userId, @Param("resType") String resType);

    public List <String> queryResourceIdByUserId(@Param("applicationId") String applicationId, @Param("userId") String userId, @Param("resType") String resType);

}
