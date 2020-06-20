package org.erhun.framework.rbac.controller;


import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.json.JsonUtils;
import org.erhun.framework.basic.utils.number.NumberUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.utils.EntityUtils;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.tree.*;
import org.erhun.framework.rbac.utils.tree.RbacTree;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 */
@Controller
public class TreeController extends AbstractRbacUserController {

	@RequestMapping("**/tree")
	public String execute(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		
		RbacUser user = getRbacUser();
		
		int treeType = NumberUtils.toInt(request.getParameter("type") , 0);
		
		if( treeType == 0 ){
		    request.setAttribute("applications", JsonUtils.toJSONString(user.getApplications()));
		    return "permission";
		}
		
		String appId = request.getParameter("appId");
		String roleId = request.getParameter("roleId");
		
		RbacTree<?> tree = null;
		String jsonString = "";
		
		switch(treeType){
			case 1:{
				if(StringUtils.isBlank(roleId)) {
					roleId = EntityUtils.toIdString(ListUtils.as(user.getRoles()));
				}
				tree = new ModuleTreeGenerator(user, appId, roleId, "1").generate();
				jsonString = JsonUtils.toJSONString(tree);
				break;
			}case 2:{
				tree = new PermissionTreeGenerator(user, appId, roleId, "1").generate();
				jsonString = JsonUtils.toJSONString(tree);
				break;
			}case 3:{
                tree = new AttributeTreeGenerator(user, appId, roleId, true).generate();
				jsonString = JsonUtils.toJSONString(tree);
                break;
			}case 4:{
                tree = new ResourceTreeGenerator(request.getParameter("roleId"), true).generate(user);
				jsonString = JsonUtils.toJSONString(tree);
                break;
            }
			case 5:{
			}
		}
		
		if(hasAjax()){
		    if(tree == null){
		        output(response, "{\"name\":\"您没有权限!\"}");
		    }else{
		        output(response, jsonString);
		    }
			return null;
		}else{
			if(tree != null){
			    request.setAttribute("applications", JsonUtils.toJSONString(user.getApplications()));
				request.setAttribute(TreeUtil.TREE_JSON_STRING, jsonString);
			}else{
				request.setAttribute(TreeUtil.TREE_JSON_STRING, "{\"name\":\"您没有权限!\"}");
			}
			
			return "permission";
			
		}
		
	}

}
