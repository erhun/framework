package org.erhun.framework.rbac.tree;


import org.erhun.framework.rbac.utils.tree.RbacTree;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
public interface TreeGenerator <T extends Object> {
    
    /**
     * 
     * @param elements
     * @return
     */
    RbacTree<T> generate(List<T> elements);
    
}
