package org.erhun.framework.rbac.dao;

import org.apache.ibatis.annotations.Select;
import org.erhun.framework.domain.dao.BaseDAO;
import org.erhun.framework.rbac.entities.PermissionInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 * 
 */
@Repository
public interface PermissionDAO extends BaseDAO<String, PermissionInfo> {

    @Select("selelct")
    public int deleteByRoleIdAndValue(String roleId, String value);

    public int deleteByUserIdAndValue(String userId, String value);

    public List <PermissionInfo> queryByRoleId(String roleIds);
    
}
