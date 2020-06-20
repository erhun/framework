package org.erhun.framework.rbac;

import org.erhun.framework.basic.utils.ResultPack;
import org.springframework.stereotype.Service;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
@Service
public interface RbacAuthentication {

    /**
     * 
     * @param user
     * @param requestMethod
     * @param uri
     * @return
     * @throws RbacAuthenticationException
     */
    ResultPack authorized(RbacUser user, String requestMethod, String uri) throws RbacAuthenticationException;

}
