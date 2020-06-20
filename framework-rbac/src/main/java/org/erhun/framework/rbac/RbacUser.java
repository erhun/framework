package org.erhun.framework.rbac;

import org.erhun.framework.basic.security.User;
import org.erhun.framework.rbac.utils.tree.RbacTree;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public interface RbacUser extends User {

    List<RbacRole> getRoles();

    List <RbacApplication> getApplications();

    RbacTree<RbacModule> getModuleTree();
    
}
