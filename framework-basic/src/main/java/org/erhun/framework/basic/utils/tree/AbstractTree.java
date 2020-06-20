package org.erhun.framework.basic.utils.tree;


import org.erhun.framework.basic.utils.collection.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 * 
 */
public abstract class AbstractTree <T> implements Tree <T> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String name;

    private List<Node<T>> children;

    public AbstractTree(String text) {
        this.name = text;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Node<T>> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<Node<T>> children) {
        this.children = children;
    }

    @Override
    public Node<T> find(String id) {

        for (Node<T> node : children) {
            if (id.equals(node.getId())) {
                return node;
            }
            node = this.find(node, id);
            if (node != null) {
                return node;
            }
        }

        return null;

    }

    @Override
    public Node <T> find(Node<T> node, String id) {

        if (!ListUtils.isEmpty(node.getChildren())) {
            for (Node<T> tn : node.getChildren()) {
                if (id.equals(tn.getId())) {
                    return tn;
                }
                tn = this.find(tn, id);
                if (tn != null) {
                    return tn;
                }
            }
        }

        return null;

    }
    
    public <E> Node<T> find(E then, Comparator<E, T> comparator) {

        for (Node<T> node : children) {
            if(comparator.compara(then, node.getModel()) == 0) {
                return node;
            };
            node = this.find(node, then, comparator);
            if (node != null) {
                return node;
            }
        }

        return null;

    }
    
    public <E> Node <T> find(Node<T> node, E then, Comparator<E, T> comparator) {

        if (!ListUtils.isEmpty(node.getChildren())) {
            for (Node<T> tn : node.getChildren()) {
                if (find(then, comparator) != null) {
                    return tn;
                }
                tn = this.find(tn, then, comparator);
                if (tn != null) {
                    return tn;
                }
            }
        }

        return null;

    }
    
    @Override
    public Node<T> findByPath(String path) {

        for (Node<T> node : children) {
            if (path.equals(node.getPath())) {
                return node;
            }
            node = this.findByPath(node, path);
            if (node != null) {
                return node;
            }
        }

        return null;

    }
    
    @Override
    public Node <T> findByPath(Node<T> node, String path) {

        if (!ListUtils.isEmpty(node.getChildren())) {
            for (Node<T> tn : node.getChildren()) {
                if (path.equals(tn.getPath())) {
                    return tn;
                }
                tn = this.findByPath(tn, path);
                if (tn != null) {
                    return tn;
                }
            }
        }

        return null;

    }

    public List<Node<T>> cloneTreeByNode(Node<T> node) {

        Tree<T> tree = new SimpleTree<T>(name);

        Node<T> parent = node.getParent();

        List<Node<T>> children = new ArrayList<Node<T>>();

        if (parent == null) {
            children.add(node);
            tree.setChildren(children);
            return null;
        }

        List<Node<T>> parents = new ArrayList<Node<T>>();

        parents.add(parent);

        Node<T> tp = parent;

        while (tp != null) {
            tp = parent.getParent();
            parents.add(tp);

        }

        return null;

    }
    
    public interface Comparator<T, E>{
        int compara(T t, E e);
    }

}
