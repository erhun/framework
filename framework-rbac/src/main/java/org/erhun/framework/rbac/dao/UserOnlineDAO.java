package org.erhun.framework.rbac.dao;

import org.apache.ibatis.annotations.Param;
import org.erhun.framework.domain.dao.BaseDAO;
import org.erhun.framework.rbac.entities.UserOnline;

/**
 * @author weichao<groilla@aliyun.com>
 */
public interface UserOnlineDAO extends BaseDAO<String, UserOnline> {

    public UserOnline find(@Param("userId") String userId, @Param("applicationId") String applicationId);

}
