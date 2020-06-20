package org.erhun.framework.basic.utils.tree;


import org.erhun.framework.basic.utils.string.StringPool;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author weichao (gorilla@aliyun.com)
 */
public class Node <T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected Object id;
    
    protected String code;

    protected String name;

    protected String value;

    protected String path;

    protected String url;

    protected String type;

    protected String icon;

    protected String html;

    protected String formId;

    protected String showMode;

    protected Boolean selected;

    protected T model;

    protected List <Node <T>> children;

    protected Node <T> parent;

    public Node() {

    }

    public Node(T model) {
        this.model = model;
    }

    public Node(String name) {
        this.name = name;
    }

    public Node(Object id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Node(Object id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Node(Object id, String name, String value, String type) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Node(Object id, String code, String name, String type, String value, String url, String icon, String showMode) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.value = value;
        this.url = url;
        this.icon = icon;
        this.showMode = showMode;
    }

    public List <Node <T>> getChildren() {
        return children;
    }

    public void setChildren(List <Node <T>> children) {
        this.children = children;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Node <T> getParent() {
        return parent;
    }

    public void setParent(Node <T> parent) {
        this.parent = parent;
    }

    public String getParentPath() {

        if (parent == null) {
            return StringPool.EMPTY;
        }

        StringBuilder buf = new StringBuilder("/");

        Node <T> tp = parent;

        while (tp != null) {
            if (model != null) {
                buf.append("/").insert(1, model.toString());
            } else {
                buf.append("/").insert(1, id);
            }
            tp = parent.getParent();
        }

        return buf.toString();

    }

    public String getPath() {
        return path;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }

    public Boolean isSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getShowMode() {
        return showMode;
    }

    public void setShowMode(String showMode) {
        this.showMode = showMode;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

}
