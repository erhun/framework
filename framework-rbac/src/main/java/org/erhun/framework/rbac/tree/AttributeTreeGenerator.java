package org.erhun.framework.rbac.tree;


import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.entities.EntityInfo;
import org.erhun.framework.rbac.dao.AttributeDAO;
import org.erhun.framework.rbac.dao.EntityDAO;
import org.erhun.framework.rbac.dao.PermissionDAO;
import org.erhun.framework.rbac.entities.AttributeInfo;
import org.erhun.framework.rbac.entities.PermissionInfo;
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
public class AttributeTreeGenerator implements TreeGenerator<Object> {

    private String roles;
    
    private String applicationId;
    
    private RbacUser user;

    private Collection<EntityInfo> entities;

    private List<PermissionInfo> permissions;
    
    private List <AttributeInfo> attributes;

    public AttributeTreeGenerator(String roles) {
        this.roles = roles;
    }

    public AttributeTreeGenerator(RbacUser user, String applicationId, String roles, boolean isAdmin) {
        this.user = user;
        this.roles = roles;
        this.applicationId = applicationId;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public RbacTree<?> generate() {

        if (roles == null) {
            return null;
        }

        entities = ApplicationContextHolder.get().getBean(EntityDAO.class).queryByColumn(PV.of("application_id", applicationId));
        
        if(ListUtils.isEmpty(entities)){
            return null;
        }
        
        List <String> entityIds = new ArrayList <String> (entities.size());
        for(EntityInfo e : entities){
            entityIds.add(e.getId());
        }
        
        attributes = ApplicationContextHolder.get().getBean(AttributeDAO.class).queryByColumn(PV.of("entity_id", entityIds));

        permissions = ApplicationContextHolder.get().getBean(PermissionDAO.class).queryByColumn(PV.of("role_id", roles, "type", "attribute"));

        RbacTree tree = new SimpleTree("字段权限");

        List<Node> children = new ArrayList<Node>();

        for (EntityInfo m : entities) {
            children.add(generate(m, null));
        }

        tree.setChildren(children);

        permissions = null;

        return tree;

    }

    private Node<AttributeInfo> generate(EntityInfo ed, Node node) {

        if (node == null) {
            node = new Node (ed.getId(), ed.getAlias(), ResourceType.ENTITY.name);
        }

        for (int i = attributes.size() - 1; i >= 0; i--) {

            AttributeInfo attr = attributes.get(i);
            
            if(!ed.getId().equals(attr.getEntityId())){
                continue;
            }

            List<Node<AttributeInfo>> children = node.getChildren();

            if (children == null) {
                children = new ArrayList<Node<AttributeInfo>>();
            }

            PermissionInfo right = getRight(attr.getId());

            Node<AttributeInfo> child = new Node<AttributeInfo>(attr.getId(), attr.getAlias(), ResourceType.ATTRIBUTE.name);

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
