package org.erhun.framework.rbac.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.erhun.framework.domain.dao.BaseDAO;
import org.erhun.framework.orm.Limits;
import org.erhun.framework.rbac.RbacModule;
import org.erhun.framework.rbac.entities.ApplicationInfo;
import org.erhun.framework.rbac.entities.GroupInfo;
import org.erhun.framework.rbac.entities.RoleInfo;
import org.erhun.framework.rbac.entities.UserInfo;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public interface UserDAO extends BaseDAO<String, UserInfo> {

    List<UserInfo> queryByAppId(@Param("applicationId") String applicationId, @Param("limits") Limits limits);
    
    long countByAppId(@Param("applicationId") String applicationId);
    
    List<RbacModule> queryModuleByRoleId(String roleIds);
    
    List<ApplicationInfo> queryAppById(String userId);
    
    List<ApplicationInfo> queryGroupAppById(String userId);
    
    List<GroupInfo> queryGroupById(String userId);

    @ResultType(String[].class)
    @Select("select g.code from uac_group g where g.id in(select group_id from uac_user_link_group where user_id=#{0})")
    List<String> queryGroupCodeById(String userId);
    
    List<RoleInfo> queryRoleById(String userId);

    List<String> queryRoleIdById(String userId);

    UserInfo findByAccountAndPassword(String userName, String password);

    @ResultType(Object[].class)
    @Select("select DATE_FORMAT(event_time,'%Y-%m'),count(distinct user_id) from uac_log l where user_id is not null and " +
            "l.event_code='login' and event_time >='2018-01-01' and event_time < '2019-01-01' group by DATE_FORMAT(event_time,'%Y-%m')")
    List <Object[]> queryMau();

}
