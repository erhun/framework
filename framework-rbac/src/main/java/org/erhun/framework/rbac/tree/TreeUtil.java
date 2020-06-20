package org.erhun.framework.rbac.tree;


import org.erhun.framework.basic.utils.string.StringUtils;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 * 
 */
public class TreeUtil {

    public final static String TREE_JSON_STRING = "TREE_JSON_STR";

    public static boolean isRoot(String parent) {

        return StringUtils.isEmpty(parent) || "0".equals(parent);

    }

}
