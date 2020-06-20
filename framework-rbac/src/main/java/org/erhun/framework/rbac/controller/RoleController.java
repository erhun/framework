package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.security.User;
import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.json.JsonUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.controller.AbstractBusinessController;
import org.erhun.framework.domain.utils.EntityUtils;
import org.erhun.framework.orm.QueryParam;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.dto.RoleQueryDTO;
import org.erhun.framework.rbac.entities.GroupInfo;
import org.erhun.framework.rbac.entities.RoleInfo;
import org.erhun.framework.rbac.entities.PermissionInfo;
import org.erhun.framework.rbac.services.GroupService;
import org.erhun.framework.rbac.services.PermissionService;
import org.erhun.framework.rbac.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Controller
@RequestMapping("/admin/rbac/roles")
public class RoleController extends AbstractRbacController {

    @Autowired
    private GroupService groupService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String listPage() throws Exception {
        return "role";
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.GET, headers="accept=application/json")
    public ResultPack list(QueryParam<RoleQueryDTO> queryDto, String groupId) throws Exception {
        
        RbacUser user = getRbacUser();
        
        if(StringUtils.isNoneBlank(groupId)) {
            GroupInfo group = groupService.get(groupId);
            if(group != null) {
                queryDto.param().setApplicationId(group.getApplicationId());
            }
        }else {
            if(!user.isAdmin()) {
                if(StringUtils.isBlank(queryDto.param().getApplicationId())) {
                    queryDto.param().setApplicationId(EntityUtils.toIdString(ListUtils.as(user.getApplications())));
                }
            }
        }
        
        return roleService.queryByPage(queryDto);
        
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public ResultPack save(RoleInfo role) throws Exception {
        
        roleService.save(role);
        
        return ResultPack.succeed();
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params={"roleId", "data"})
    public String save(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String data = request.getParameter("data");

        List <String> list;

        if (StringUtils.isNotEmpty(data)) {
            list = JsonUtils.parseList(data, List.class);
        } else {
            list = Collections.emptyList();
        }

        String roleId = request.getParameter("roleId");

        if (StringUtils.isEmpty(roleId)) {
            return "error:${rbac.rights_setting.role_id.notempty}";
        }
        
        RoleInfo role = roleService.get(roleId);

        StringBuilder rs = new StringBuilder("ajax:[");

        for (int i = 0; i < list.size(); i++) {

            String ar[] = list.get(i).split(",");
            String rightId = ar[1];
            String val = ar[0];
            String type = ar[3];

            if (StringUtils.isEmpty(val)) {
                continue;
            }

            if (StringUtils.isNotEmpty(rightId)) {
                if ("0".equals(ar[2])) {
                    if (permService.deleteByRoleIdAndValue(role, type, val) != 0) {
                        if (rs.length() > 1) {
                            rs.append(",");
                        }
                        rs.append("[");
                        rs.append("'").append(val).append("',");
                        rs.append("]");
                    }
                }
            } else {

                if ("0".equals(ar[2])) {
                    continue;
                }

                PermissionInfo permssion = new PermissionInfo();

                permssion.setRoleId(roleId);
                permssion.setValue(val);
                permssion.setPermission(ar[2]);
                permssion.setType(type);

                permService.save(role, permssion);

                if (rs.length() > 1) {
                    rs.append(",");
                }

                rs.append("[");
                rs.append("'").append(ar[0]).append("',");
                rs.append("'").append(permssion.getId()).append("'");
                rs.append("]");
            }

        }

        if (rs.length() == 1) {
            rs.setLength(0);
        } else {
            rs.append("]");
        }

        //output(response, rs.toString());

        return rs.toString();

    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    public String add(HttpServletRequest request) throws Exception {
       request.setAttribute("role", new RoleInfo());
       return "role_edit";
    }
    
    @RequestMapping(value="{id}/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, @PathVariable String id) throws Exception {

       RoleInfo role = roleService.get(id);

       request.setAttribute("role", role);
       
       return "role_edit";
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.DELETE)
    public ResultPack del(HttpServletRequest request, String id) throws Exception {

       roleService.delete(id);

       return ResultPack.succeed();
    }
}
