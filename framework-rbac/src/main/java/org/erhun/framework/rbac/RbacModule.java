package org.erhun.framework.rbac;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public interface RbacModule {

    String getId();
    
    String getCode();
    
    String getName();
    
    String getPath();
    
    String getUrl();
    
    String getParentId();
    
    String getType();
    
    List <RbacFunction> getFunctions();
   
}
