package org.erhun.framework.rbac.tree;


import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.dao.EntityDAO;
import org.erhun.framework.rbac.dao.PermissionDAO;
import org.erhun.framework.rbac.dao.ResourceDAO;
import org.erhun.framework.rbac.entities.EntityInfo;
import org.erhun.framework.rbac.entities.PermissionInfo;
import org.erhun.framework.rbac.entities.ResourceInfo;
import org.erhun.framework.rbac.enums.ResourceType;
import org.erhun.framework.rbac.utils.tree.Node;
import org.erhun.framework.rbac.utils.tree.RbacTree;
import org.erhun.framework.rbac.utils.tree.SimpleTree;
import org.erhun.framework.supports.spring.ApplicationContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
public class ResourceTreeGenerator implements TreeGenerator<Object> {

    private Object roles;

    private Collection<EntityInfo> entities;

    private List<PermissionInfo> permissions;
    
    private List <ResourceInfo> resources;

    public ResourceTreeGenerator(String roles) {
        this.roles = roles;
    }

    public ResourceTreeGenerator(String roles, boolean isAdmin) {
        this.roles = roles;
    }

    public RbacTree<?> generate(RbacUser info) {
        if(roles == null){
            roles = info.getRoles();
        }
        return this.generate();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public RbacTree<?> generate() {

        if (roles == null) {
            return null;
        }

        entities = ApplicationContextHolder.get().getBean(EntityDAO.class).queryAll();
        
        List <String> entityIds = new ArrayList <String> (entities.size());
        for(EntityInfo e : entities){
            entityIds.add(e.getId());
        }
        
        resources = ApplicationContextHolder.get().getBean(ResourceDAO.class).queryByColumn(PV.of("application_id", entityIds));

        permissions = ApplicationContextHolder.get().getBean(PermissionDAO.class).queryByColumn(PV.of("role_id", roles, "role_id", roles, "type", "attribute"));

        RbacTree tree = new SimpleTree("数据权限");

        List<Node> children = new ArrayList<Node>();

        for (EntityInfo m : entities) {
            children.add(generate(m, null));
        }

        tree.setChildren(children);

        permissions = null;

        return tree;

    }

    private Node<ResourceInfo> generate(EntityInfo ed, Node<ResourceInfo> node) {

        if (node == null) {
            node = new Node<ResourceInfo>(ed.getId(), ed.getAlias(), ResourceType.MODULE.name);
        }

        for (int i = 0; i < resources.size(); i++) {

            ResourceInfo attr = resources.get(i);
            
            /*if(!ed.getId().equals(attr.getEntityId())){
                continue;
            }*/

            List<Node<ResourceInfo>> children = node.getChildren();

            if (children == null) {
                children = new ArrayList<Node<ResourceInfo>>();
            }

            PermissionInfo right = getRight(attr.getId());

            Node<ResourceInfo> child = new Node<ResourceInfo>(attr.getId(), attr.getName(), ResourceType.ATTRIBUTE.name);

            if (right != null) {
                child.setValue(right.getId());
                if (!"0".equals(right.getPermission())) {
                    child.setSelected(true);
                }
            }

            children.add(child);

            node.setChildren(children);

        }

        return node;

    }

    private PermissionInfo getRight(String funId) {

        Iterator<PermissionInfo> iter = permissions.iterator();

        while (iter.hasNext()) {

            PermissionInfo r = iter.next();

            if (funId.equals(r.getValue())) {
                iter.remove();
                return r;
            }

        }

        return null;
    }

    @Override
    public RbacTree<Object> generate(List<Object> elements) {
        return null;
    }
}
