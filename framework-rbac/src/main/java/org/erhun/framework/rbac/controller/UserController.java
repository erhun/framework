package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.json.JsonUtils;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.domain.utils.EntityUtils;
import org.erhun.framework.orm.QueryParam;
import org.erhun.framework.rbac.RbacGroup;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.dto.UserQueryDTO;
import org.erhun.framework.rbac.entities.*;
import org.erhun.framework.rbac.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

/**
 * 
 * @author weichao (gorilla@aliyun.com)
 */
@Controller
@RequestMapping("/admin/rbac/users")
public class UserController extends AbstractRbacController<String, UserInfo, UserQueryDTO> {

    @Autowired
    private UserService userService;
    
    @Autowired
    private RbacService rbacService;
    
    @Autowired
    private EntityService entityService;
    
    @Autowired
    private AuthcodeService authService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String listPage() throws Exception {
        return "user";
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String searchPage() throws Exception {
        return "user_search";
    }
    
    @RequestMapping(value="user_setting", method = RequestMethod.GET)
    public String userSettingPage() throws Exception {
        return "user_setting";
    }
    
    @RequestMapping(value="change_pwd", method = RequestMethod.GET)
    public String changePwdPage(HttpServletRequest request) throws Exception {
        request.setAttribute("user", getUser());
        return "user_pwd";
    }

    @RequestMapping(value="user_mau", method = RequestMethod.GET)
    public String userMau(HttpServletRequest request) throws Exception {
        request.setAttribute("user", getUser());
        return "user_mau";
    }
    
    @Override
    @ResponseBody
    @RequestMapping(method= RequestMethod.GET, headers="accept=application/json")
    public ResultPack list(QueryParam<UserQueryDTO> queryDto){
        
        RbacUser u = super.getRbacUser();
        
        if(!u.isAdmin()) {
            queryDto.param().setApplicationId(EntityUtils.toIdString(ListUtils.as(u.getApplications())));
        }

        return userService.queryByPage(queryDto, (String[]) PV.as("id", "code", "name", "account", "status"));
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, params={"userId","appId"})
    public ResultPack saveApp(@RequestParam String userId, @RequestParam String appId) throws Exception {
        
        UserLinkApplication ula = new UserLinkApplication();
        ula.setUserId(userId);
        ula.setApplicationId(appId);
        
        userService.save(ula);
        
        return SUCCEED;
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.POST, params={"userId","groupId"})
    public ResultPack saveGroup(@RequestParam String userId, @RequestParam String groupId) throws Exception {
        
        UserLinkGroup ula = new UserLinkGroup();
        ula.setUserId(userId);
        ula.setGroupId(groupId);
        
        userService.save(ula);
        
        return SUCCEED;
    }

    @ResponseBody
    @RequestMapping(method= RequestMethod.POST, params={"userId","resourceId"})
    public ResultPack saveResources(@RequestParam String userId, @RequestParam String resourceId) throws Exception {

        UserLinkResource ula = new UserLinkResource();
        ula.setUserId(userId);
        ula.setResourceId(resourceId);

        userService.save(ula);

        return SUCCEED;
    }
    
    @ResponseBody
    @RequestMapping(value="lock", method= RequestMethod.POST)
    public ResultPack lock(@RequestParam String id) throws Exception {
        userService.lock(id);
        return SUCCEED;
    }
    
    @ResponseBody
    @RequestMapping(value="unlock", method= RequestMethod.POST)
    public ResultPack unlock(@RequestParam String id) throws Exception {
        userService.unlock(id);
        return SUCCEED;
    }
    
    @ResponseBody
    @RequestMapping(value="change_pwd", method= RequestMethod.POST)
    public ResultPack changePwd(@RequestParam String id, @RequestParam String password, @RequestParam String newPassword, @RequestParam String authCode) throws Exception {
        
        userService.changePwd(id, password, newPassword, authCode);
        
        return SUCCEED;
    }
    
    @RequestMapping(value="reset_pwd", method = RequestMethod.GET)
    public String resetPwdPage(HttpServletRequest request, String id) throws Exception {
        if(!super.getUser().isAdmin()) {
            throw new BusinessException("只有管理员才能重置密码!");
        }
        UserInfo user = userService.get(id);
        request.setAttribute("user", user);
        return "user_pwd2";
    }
    
    @ResponseBody
    @RequestMapping(value="reset_pwd", method= RequestMethod.POST)
    public ResultPack resetPwd(@RequestParam String id, @RequestParam String newPassword) throws Exception {
        
        if(!super.getUser().isAdmin()) {
            throw new BusinessException("只有超级管理员才能重置密码!");
        }
        
        userService.changePwd(id, newPassword);
        
        return SUCCEED;
    }
    
    @ResponseBody
    @RequestMapping("authcode")
    public ResultPack authCode() throws Exception {
        
        authService.send(this.getUser().getId());
        //userService.changePwd(id, password, newPassword, authCode);
        
        return SUCCEED;
    }
    
    @RequestMapping(value="add", method = RequestMethod.GET)
    public String add(HttpServletRequest request) throws Exception {
        
        if(!this.getUser().isAdmin()) {
            request.setAttribute("groupList", JsonUtils.toJSONString(userService.queryGroupByUserId(this.getUser().getId())));
        }
        //request.setAttribute("user", new UserInfo());
        return "user_edit";
    }

    @RequestMapping(value="{id}/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, @PathVariable String id) throws Exception {

       UserInfo user = userService.get(id);

       request.setAttribute("user", user);
       
       return "user_edit";
    }
    
    @RequestMapping(value="{id}/view", method = RequestMethod.GET)
    public String view(HttpServletRequest request, @PathVariable String id) throws Exception {

       RbacUser user = this.getRbacUser();
       UserInfo userInfo = userService.get(id);
       
       EntityInfo entity = entityService.findByEntityName("UserInfo");
       
       List <AttributeInfo> list = null;
       
       if(entity != null){
           list = rbacService.queryAttributeByRoleIdAndEntityId(EntityUtils.singleQuotes(ListUtils.as(user.getRoles())), entity.getId());
           if(list == null || list.isEmpty()){
                throw new AuthenticationException("${rights.not.operate}");
           }
       }
       
       request.setAttribute("attributes", list);
       request.setAttribute("user", userInfo);
       
       return "user_view";
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.DELETE)
    public ResultPack del(HttpServletRequest request, @RequestParam String id) throws Exception {

       userService.delete(id);

       return SUCCEED;
    }

    @RequestMapping("logout")
    public String logout(HttpServletRequest request) throws Exception {

        Enumeration<String> enums = request.getSession().getAttributeNames();
        while (enums.hasMoreElements()){
            request.getSession().removeAttribute(enums.nextElement());
        }
        request.getSession().setMaxInactiveInterval(-1);

        return "/login.html";
    }

    @ResponseBody
    @RequestMapping("queryAppIdByUserId")
    public Object queryAppIdByUserId(@RequestParam String userId) throws Exception {
        return userService.queryAppIdByUserId(userId);
    }
    
    @ResponseBody
    @RequestMapping("queryGroupIdByUserId")
    public Object queryGroupIdByUserId(@RequestParam String userId) throws Exception {
        return userService.queryGroupIdByUserId(userId);
    }

    @ResponseBody
    @RequestMapping("queryCurrentUserGroupCode")
    public String queryGroupCodeByUserId() throws Exception {
        UserImpl user = (UserImpl) getUser();
        StringBuilder buf = new StringBuilder();
        for (RbacGroup group: user.getGroups()) {
            buf.append(group.getCode()).append(",");
        }
        if(buf.length()>0){
            buf.setLength(buf.length()-1);
        }
        return buf.toString();
    }

    @ResponseBody
    @RequestMapping("queryResourceIdByUserId")
    public Object queryResourceIdByUserId(@RequestParam String applicationId, @RequestParam String userId) throws Exception {
        return userService.queryResourceIdByUserId(applicationId, userId, null);
    }

    @ResponseBody
    @RequestMapping("queryMau")
    public List <Object[]> queryMau() throws Exception {
        return userService.queryMau();
    }

}
