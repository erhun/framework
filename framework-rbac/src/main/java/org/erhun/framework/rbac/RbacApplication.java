package org.erhun.framework.rbac;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public interface RbacApplication {

    String getId();
    
    String getCode();
    
    String getName();
    
    String getLoginUrl();
    
    String getLogoutUrl();
    
    String getType();
    
    List <RbacModule> getModules();
   
}
