package org.erhun.framework.rbac.services;

import org.erhun.framework.domain.services.BusinessException;
import org.springframework.stereotype.Service;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
@Service
public interface AuthcodeService{
    
    
    boolean send(String userId) throws BusinessException;

    boolean sendByAccount(String account) throws BusinessException;
}
