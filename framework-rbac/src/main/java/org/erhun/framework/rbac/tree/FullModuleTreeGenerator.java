package org.erhun.framework.rbac.tree;


import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.utils.EntityUtils;
import org.erhun.framework.rbac.entities.FunctionInfo;
import org.erhun.framework.rbac.entities.ModuleInfo;
import org.erhun.framework.rbac.entities.PermissionInfo;
import org.erhun.framework.rbac.enums.ResourceType;
import org.erhun.framework.rbac.services.FunctionService;
import org.erhun.framework.rbac.services.ModuleService;
import org.erhun.framework.rbac.services.PermissionService;
import org.erhun.framework.rbac.utils.tree.Node;
import org.erhun.framework.rbac.utils.tree.SimpleTree;
import org.erhun.framework.rbac.utils.tree.RbacTree;
import org.erhun.framework.supports.spring.ApplicationContextHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * 包含应用的所有模块，模块下的功能，用于用户权限判断
 * @author weichao<gorilla@gliyun.com>
 *
 */
@SuppressWarnings("rawtypes")
public class FullModuleTreeGenerator implements TreeGenerator <Object>{

    private Object roles;
    
    private Object applications;

    private List <ModuleInfo> modules;

    private List<FunctionInfo> functions;

    private List<PermissionInfo> permissions;

    public FullModuleTreeGenerator(List <String> applications, List <String> roles) {
        this.applications = applications;
        this.roles = roles;
    }

    public FullModuleTreeGenerator(List <String> applications, List <ModuleInfo> modules, List <String> roles) {
        this.applications = applications;
        this.modules = modules;
        this.roles = roles;
    }
    
    public FullModuleTreeGenerator(String applicationId, String roleId) {
        this.applications = applicationId;
        this.roles = roleId;
    }
    
    public FullModuleTreeGenerator(String applicationId, List <String> roles) {
        this.applications = applicationId;
        this.roles = roles;
    }

    @SuppressWarnings("unchecked")
    public RbacTree generate() {

        if (roles == null) {
            return null;
        }
        
        if (applications == null) {
            return null;
        }

        if(modules == null) {
            ModuleService moduleService = ApplicationContextHolder.get().getBean(ModuleService.class);
            modules = moduleService.queryByAppId(applications);
            if (ListUtils.isEmpty(modules)) {
                return null;
            }
        }
        
        FunctionService funService = ApplicationContextHolder.get().getBean(FunctionService.class);
        PermissionService permiService = ApplicationContextHolder.get().getBean(PermissionService.class);
        
        functions = funService.queryByModuleId(EntityUtils.idList(modules));
        permissions = permiService.queryByRoleIdAndType(roles, ListUtils.as("function","module"));
                
        RbacTree tree = new SimpleTree("模块权限");

        List<Node<?>> children = new ArrayList<Node<?>>();

        for (int i = 0; i < modules.size(); i++) {
            ModuleInfo mod = (ModuleInfo) modules.get(i);
            if (TreeUtil.isRoot(mod.getParentId())) {
                children.add(generate(modules, mod, null));
            }
        }

        tree.setChildren(children);

        modules = null;
        permissions = null;

        return tree;

    }

    @SuppressWarnings("unchecked")
    private Node generate(List<ModuleInfo> modules, ModuleInfo parentModule, Node node) {

        if (node == null) {
            String url = parentModule.getUrl();
            node = new Node(parentModule.getId(), parentModule.getCode(), parentModule.getName(), ResourceType.MODULE.name, null, url,parentModule.getIcon(), parentModule.getType());
            node.setPath(parentModule.getPath());
            if(StringUtils.isNotBlank(parentModule.getUrl()) ) {
                if(!parentModule.getUrl().startsWith("/") && parentModule.getUrl().indexOf("http:") == -1) {
                    String p = node.getPath();
                    if(!p.endsWith("/")) {
                        p += "/";
                    }
                    node.setUrl(p + parentModule.getUrl());
                }
            }else {
                node.setUrl(node.getPath());
            }
            node.setShowMode(parentModule.getType());
        }
        
        for (int i = 0; i < modules.size(); i++) {

            ModuleInfo modu = modules.get(i);

            if (parentModule.getId().equals(modu.getParentId())) {

                List<Node> children = node.getChildren();

                if (children == null) {
                    children = new ArrayList<Node>();
                }
                
                Node child = new Node(modu.getId(), modu.getCode(), modu.getName(), ResourceType.MODULE.name, null, modu.getUrl(), modu.getIcon(), modu.getType());
                child.setPath(modu.getPath());
                if(StringUtils.isNotBlank(modu.getUrl()) ) {
                    if(!modu.getUrl().startsWith("/") && modu.getUrl().indexOf("http:") == -1) {
                        String p = modu.getPath();
                        if(!p.endsWith("/")) {
                            p += "/";
                        }
                        child.setUrl(p + modu.getUrl());
                    }
                }else {
                    child.setUrl(modu.getPath());
                }
                child.setShowMode(modu.getType());

                children.add(child);

                generate(modules, modu, child);

                node.setChildren(children);

            }

        }

        addFunctionNodes(node, parentModule);

        return node;

    }

    @SuppressWarnings("unchecked")
    private void addFunctionNodes(Node node, ModuleInfo module) {

        for (int i = 0; i < functions.size(); i++) {

            FunctionInfo fun = functions.get(i);

            if (module.getId().equals(fun.getModuleId())) {

                List<Node> children = node.getChildren();

                if (children == null) {
                    children = new ArrayList<Node>(5);
                }

                Node child = new Node(fun.getId(), fun.getCode(), fun.getName(), ResourceType.FUNCTION.name, null,null, fun.getIcon(), fun.getShowMode());
                child.setHtml(fun.getHtml());
                child.setFormId(fun.getFormId());
                child.setPath(fun.getUrl());

                PermissionInfo right = this.getRight(fun);

                if (right != null) {
                    child.setValue(right.getId());
                    if ("1".equals(right.getPermission())) {
                        child.setSelected(true);
                    }
                }

                children.add(child);

                node.setChildren(children);

            }
        }

        Iterator<PermissionInfo> iter = permissions.iterator();

        while (iter.hasNext()) {
            PermissionInfo r = iter.next();
            if (module.getId().equals(r.getValue())) {
                node.setValue(r.getId());
                if ("1".equals(r.getPermission())) {
                    node.setSelected(true);
                }
                iter.remove();
                break;
            }
        }

    }

    private PermissionInfo getRight(FunctionInfo fun) {

        Iterator<PermissionInfo> iter = permissions.iterator();

        while (iter.hasNext()) {

            PermissionInfo r = iter.next();

            if (fun.getId().equals(r.getValue())) {
                iter.remove();
                return r;
            }

        }

        return null;
    }

    @Override
    public RbacTree generate(List elements) {
        // TODO Auto-generated method stub
        return null;
    }
}
