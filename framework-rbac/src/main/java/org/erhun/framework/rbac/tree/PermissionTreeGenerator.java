package org.erhun.framework.rbac.tree;


import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.rbac.RbacApplication;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.entities.FunctionInfo;
import org.erhun.framework.rbac.entities.ModuleInfo;
import org.erhun.framework.rbac.entities.PermissionInfo;
import org.erhun.framework.rbac.enums.ResourceType;
import org.erhun.framework.rbac.services.FunctionService;
import org.erhun.framework.rbac.services.ModuleService;
import org.erhun.framework.rbac.services.PermissionService;
import org.erhun.framework.rbac.utils.tree.Node;
import org.erhun.framework.rbac.utils.tree.RbacTree;
import org.erhun.framework.rbac.utils.tree.SimpleTree;
import org.erhun.framework.supports.spring.ApplicationContextHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@SuppressWarnings("rawtypes")
public class PermissionTreeGenerator implements TreeGenerator {

    private RbacUser user;
    
    private String modType = "1";

    private Object roles;
    
    private String applicationId;

    private List<FunctionInfo> functions;

    private List<PermissionInfo> permissions;

    public PermissionTreeGenerator(RbacUser user, String applicationId, String roles, String modType) {
        this.user = user;
        this.applicationId = applicationId;
        this.roles = roles;
        this.modType = modType;
    }

    public PermissionTreeGenerator(String roles, boolean isAdmin, String modType) {
        this.roles = roles;
        // this.isAdmin = isAdmin;
        this.modType = modType;
    }

    @SuppressWarnings("unchecked")
    public RbacTree generate() {

        if(roles == null){
            roles = user.getRoles();
        }
        
        if (roles == null) {
            return null;
        }
        
        List <RbacApplication> apps = user.getApplications();
        
        if (ListUtils.isEmpty(apps) && StringUtils.isBlank(applicationId)) {
            return null;
        }
        
        ModuleService moduleService = ApplicationContextHolder.get().getBean(ModuleService.class);
        
        Object appId = applicationId;
        
        if(StringUtils.isBlank(applicationId)){
            List <String> appIds = new ArrayList <String> (apps.size());
            for(RbacApplication a : apps){
                appIds.add(a.getId());
            }
            appId = appIds;
        }
        
        List<ModuleInfo> modules = moduleService.queryByAppId(appId);//(PV.nv("application_id", appId));

        if (ListUtils.isEmpty(modules)) {
            return null;
        }
        
        List <String> modIds = new ArrayList <String> (modules.size());
        
        for(ModuleInfo m : modules){
            modIds.add(m.getId());
        }
        
        FunctionService funService = ApplicationContextHolder.get().getBean(FunctionService.class);
        PermissionService permiService = ApplicationContextHolder.get().getBean(PermissionService.class);
        
        functions = funService.queryByModuleId(modIds);
        
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

    private Node generate(List<ModuleInfo> modules, ModuleInfo parentModule, Node node) {

        if (node == null) {
            node = new Node(parentModule.getId(), parentModule.getName(), ResourceType.MODULE.name);
        }

        for (int i = 0; i < modules.size(); i++) {

            ModuleInfo modu = modules.get(i);

            if (parentModule.getId().equals(modu.getParentId())) {

                List<Node> children = node.getChildren();

                if (children == null) {
                    children = new ArrayList<Node>();
                }

                Node child = new Node(modu.getId(), modu.getName(), ResourceType.MODULE.name);

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
                    children = new ArrayList<Node>();
                }

                Node child = new Node(fun.getId(), fun.getName(), ResourceType.FUNCTION.name);

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
