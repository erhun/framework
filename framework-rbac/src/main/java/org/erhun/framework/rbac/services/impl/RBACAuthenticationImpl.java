package org.erhun.framework.rbac.services.impl;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.rbac.RbacAuthentication;
import org.erhun.framework.rbac.RbacAuthenticationException;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.enums.ResourceType;
import org.erhun.framework.rbac.utils.tree.Node;
import org.erhun.framework.rbac.utils.tree.RbacTree;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
@Service
public class RBACAuthenticationImpl implements RbacAuthentication {
    
    @Override
    @SuppressWarnings("rawtypes")
    public ResultPack authorized(RbacUser user, String requestMethod, String uri) throws RbacAuthenticationException {
        
        RbacTree tree = user.getModuleTree();
        
        if(tree == null){
            throw new RbacAuthenticationException("${rights.not.operate}");
        }
        
        String modulePath = uri;
        String funcCode = null;

        Node node = tree.findByPath(modulePath);
        
        if(node == null){
            int idx = uri.lastIndexOf("/");
            modulePath = uri.substring(0, idx);
            funcCode = uri.substring(idx+1);
            if("edit".equals(funcCode) || "view".equals(funcCode)){
                idx = uri.lastIndexOf("/", idx-1);
                modulePath = uri.substring(0, idx);
            }
            node = tree.findByPath(modulePath);
            if(node == null || ListUtils.isEmpty(node.getChildren())){
                return ResultPack.succeed().set("modulePath", modulePath).set("currentNode", node);
            }
        }else{
            funcCode = convertFunction(requestMethod, funcCode);
        }

        if(node == null || (node.isSelected() == null || !node.isSelected())){
            throw new RbacAuthenticationException("${rights.not.operate}");
        }
        
        List functions = node.getChildren();

        authFunction(functions, funcCode);
        
        return ResultPack.succeed().set("modulePath", modulePath).set("functions", functions).set("currentModule", node.getModel()).set("currentNode", node);
    }

    @SuppressWarnings("rawtypes")
    private void authFunction(List functions, String funcCode) throws RbacAuthenticationException {
        if(StringUtils.isNotBlank(funcCode) && !ListUtils.isEmpty(functions)){
            Node nod = null;
            for (Object o : functions) {
                Node n = (Node)o;
                if(ResourceType.FUNCTION.name.equals(n.getType()) && (funcCode.equals(n.getCode()) || funcCode.equals(n.getType()))){
                    nod = n;
                    break;
                }
            }
            if(nod != null && (nod.isSelected() == null || !nod.isSelected())){
                throw new RbacAuthenticationException("${rights.not.operate}");
            }
        }
    }

    private String convertFunction(String requestMethod, String funcCode) {
        if("GET".equals(requestMethod)){
            funcCode = "query";
        }else if("POST".equals(requestMethod)){
            funcCode = "save";
        }else if("DELETE".equals(requestMethod)){
            funcCode = "remove";
        }
        return funcCode;
    }

	
}
