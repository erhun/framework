package org.erhun.framework.rbac.services;

import org.erhun.framework.domain.services.IBusinessService;
import org.erhun.framework.rbac.entities.UserOnline;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Repository
public interface UserOnlineService extends IBusinessService<String, UserOnline> {

}
