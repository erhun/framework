package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.security.User;
import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.controller.AbstractBusinessController;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.domain.utils.EntityUtils;
import org.erhun.framework.orm.QueryParam;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.entities.GroupInfo;
import org.erhun.framework.rbac.dto.GroupQueryDTO;
import org.erhun.framework.rbac.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Controller
@RequestMapping("/admin/rbac/groups")
public class GroupController extends AbstractRbacController <String, GroupInfo, GroupQueryDTO> {
	
	@Autowired
	private GroupService groupService;
	
	@RequestMapping(method = RequestMethod.GET)
    public String listPage() throws Exception {
        return "group";
    }
	
	@Override
    @ResponseBody
    @RequestMapping(method= RequestMethod.GET, headers="accept=application/json")
    public ResultPack list(QueryParam<GroupQueryDTO> queryDto) {
		
	    RbacUser user = this.getRbacUser();
	    
	    if(!user.isAdmin()) {
            if(StringUtils.isBlank(queryDto.param().getApplicationId())) {
                queryDto.param().setApplicationId(EntityUtils.toIdString(ListUtils.as(user.getApplications())));
            }
	    }
	    
	    return groupService.queryByPage(queryDto);
		
	}
	
	@ResponseBody
    @RequestMapping("queryRoleIdByGroupId")
    public Object queryRoleIdByGroupId(@RequestParam String groupId){
        return groupService.queryRoleIdByGroupId(groupId);
    }
	
    @RequestMapping(method = RequestMethod.POST, params={"gId", "rId"})
    public ResultPack saveRole(@RequestParam String gId, @RequestParam String rId, @RequestParam String opr) throws Exception {
        groupService.saveRole(gId, rId, opr);
        return SUCCEED;

    }
    
    @Override
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public ResultPack save(GroupInfo role){
        
        if(!this.getUser().isAdmin() && StringUtils.isBlank(role.getApplicationId())) {
            throw new BusinessException("所属应用不能为空");
        }
        
        groupService.save(role);
        
        return ResultPack.SUCCEED;
    }
    
    @RequestMapping(value="add", method = RequestMethod.GET)
    public String add(HttpServletRequest request) throws Exception {
        request.setAttribute("admin", this.getUser().isAdmin());
        request.setAttribute("group", new GroupInfo());
        return "group_edit";
    }

    @RequestMapping(value="{id}/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, @PathVariable String id) throws Exception {

        GroupInfo role = groupService.get(id);

        request.setAttribute("admin", this.getUser().isAdmin());
        request.setAttribute("group", role);
       
        return "group_edit";
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.DELETE)
    public ResultPack del(HttpServletRequest request, String id) throws Exception {

       groupService.delete(id);

       return ResultPack.SUCCEED;
    }
    
}
