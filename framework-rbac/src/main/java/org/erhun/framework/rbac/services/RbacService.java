package org.erhun.framework.rbac.services;

import org.erhun.framework.rbac.entities.AttributeInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
@Service
public interface RbacService{

    List <AttributeInfo> queryAttributeByRoleIdAndEntityId(String roleId, String entityId);

}
