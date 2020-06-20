package org.erhun.framework.rbac.tree;


import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.entities.ModuleInfo;
import org.erhun.framework.rbac.enums.ResourceType;
import org.erhun.framework.rbac.services.ModuleService;
import org.erhun.framework.rbac.utils.tree.Node;
import org.erhun.framework.rbac.utils.tree.RbacTree;
import org.erhun.framework.rbac.utils.tree.SimpleTree;
import org.erhun.framework.supports.spring.ApplicationContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author weichao<gorilla@gliyun.com>
 *
 */
@SuppressWarnings("rawtypes")
public class ModuleTreeGenerator implements TreeGenerator {

    private RbacUser user;

    private String modType = "1";

    private String roles;

    private String applicationId;

    private List<ModuleInfo> modules;

    private ModuleService moduleService;

    public ModuleTreeGenerator(RbacUser user, String applicationId, String roles, String modType) {
        this.user = user;
        this.applicationId = applicationId;
        this.roles = roles;
        this.modType = modType;
        this.moduleService = ApplicationContextHolder.get().getBean(ModuleService.class);
        if(user.isAdmin()) {
            modules = moduleService.queryByAppId(applicationId);
        }else{
            if(StringUtils.isNotBlank(applicationId) && StringUtils.isNotBlank(roles)) {
                modules = moduleService.queryByRoleId(applicationId, roles, "1");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public RbacTree generate() {

        if (ListUtils.isEmpty(modules)) {
            return null;
        }

        RbacTree tree = new SimpleTree("模块");

        List<Node<?>> children = new ArrayList<Node<?>>(8);

        for (int i = 0; i < modules.size(); i++) {
            ModuleInfo mod = (ModuleInfo) modules.get(i);
            if (TreeUtil.isRoot(mod.getParentId())) {
                children.add(generate(modules, mod, null));
            }
        }

        tree.setChildren(children);

        modules = null;

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
        return node;

    }

    @Override
    public RbacTree generate(List elements) {
        return null;
    }
}
