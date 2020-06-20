package org.erhun.framework.rbac;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public interface RbacGroup {

    String getId();
    
    String getCode();
    
    String getName();
    
    String getType();
    
    String getMemo();
    
    List <RbacRole> getRoles();
}
