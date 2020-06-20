package org.erhun.framework.basic.utils.tree;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 * @param <T>
 */
public interface Tree <T> extends Serializable{
    
    String getName();
    
    List <Node<T>> getChildren();
    
    void setChildren(List<Node<T>> childrend);
    
    Node <T> findByPath(Node<T> node, String url);

    Node<T> findByPath(String url);

    Node <T> find(Node<T> node, String id);

    Node<T> find(String id);

}
