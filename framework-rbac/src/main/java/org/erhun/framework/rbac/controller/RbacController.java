package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.security.User;
import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.json.JsonUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.controller.AbstractUserController;
import org.erhun.framework.rbac.RbacApplication;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.entities.ApplicationInfo;
import org.erhun.framework.rbac.entities.ResourceInfo;
import org.erhun.framework.rbac.services.AuthcodeService;
import org.erhun.framework.rbac.services.ResourceService;
import org.erhun.framework.rbac.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author weichao (gorilla@aliyun.com)
 */
@Controller
@RequestMapping("/admin/rbac")
public class RbacController extends AbstractRbacUserController {

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthcodeService authService;
    
    @RequestMapping(value="retrieve_pwd", method = RequestMethod.GET)
    public String changePwdPage(HttpServletRequest request) throws Exception {
        request.setAttribute("user", getUser());
        return "user_repwd";
    }
    
    @ResponseBody
    @RequestMapping(value="retrieve_pwd", method= RequestMethod.POST)
    public ResultPack retrievePwd(@RequestParam String account, @RequestParam String newPassword, @RequestParam String authCode) throws Exception {
        userService.retrievePwd(account, newPassword, authCode);
        return SUCCEED;
    }

    @RequestMapping(value="allocate")
    public String allocate(@RequestParam String account, @RequestParam String newPassword, @RequestParam String authCode) throws Exception {
        return "allocate";
    }
    
    @ResponseBody
    @RequestMapping("authcode")
    public ResultPack authCode(@RequestParam String account) throws Exception {
        authService.sendByAccount(account);
        return SUCCEED;
    }

    @ResponseBody
    @RequestMapping("queryResourceByRoleId")
    public ResultPack queryResourceByRoleId(String appId, String roleId, Integer pageNo, Integer pageSize) {

        List <ResourceInfo> list = resourceService.queryResourceByRoleId(appId, StringUtils.singleQuotes(roleId), pageNo, pageSize);
        
        long size = resourceService.countResourceByRoleId(appId, StringUtils.singleQuotes(roleId));
        
        return ResultPack.result(size, list);
        
    }
    
    @ResponseBody
    @RequestMapping("queryUserApps")
    public List <RbacApplication> getApps() {
        RbacUser user = getRbacUser();
        List <RbacApplication> list = user.getApplications();
        List <RbacApplication> returnList = new ArrayList <RbacApplication> (list.size());
        for (RbacApplication app : list) {
            ApplicationInfo appInfo = new ApplicationInfo();
            appInfo.setId(app.getId());
            appInfo.setName(appInfo.getName());
            returnList.add(app);
        }
        return returnList;
    }
    
    @ResponseBody
    @RequestMapping("queryAuthorizedApps")
    public List <RbacApplication> ssoApps() {
        
        RbacUser user = getRbacUser();
        
        if(user.isAdmin()) {
            return Collections.emptyList();
        }
        
        long timestamp = System.currentTimeMillis();
        
        String authToken = (String) this.getRequest().getSession().getAttribute("portal.old_auth_token");
        
        List <RbacApplication> list = user.getApplications();//userService.queryAppByUserId(user.getId());
        
        if(ListUtils.isEmpty(list)) {
            return null;
        }
        
        List <RbacApplication> returnList = new ArrayList <RbacApplication> (list.size());
        
        for (RbacApplication tmp : list) {

            ApplicationInfo app = (ApplicationInfo) tmp;

            ApplicationInfo returnApp = new ApplicationInfo();

            //returnApp.setId(app.getId());
            returnApp.setCode(app.getCode());
            returnApp.setName(app.getName());
            returnApp.setIcon(app.getIcon());

            String privateExponent = app.getPrivateExponent();
            String privateModulus = app.getPrivateModulus();
            
            if(StringUtils.isEmpty(privateModulus) || StringUtils.isEmpty(privateExponent) || StringUtils.isEmpty(app.getLoginUrl())){
                continue;
            }
            
            try {
                returnApp.setLoginUrl("agents/" + app.getCode());
                returnList.add(returnApp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        
        return returnList;
        
    }
    
    @ResponseBody
    @RequestMapping(value="getUserModules", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getModules() {
        RbacUser user = getRbacUser();
        return JsonUtils.toJSONString(user.getModuleTree());
    }

    @RequestMapping("rights_mgr")
    public String rights() throws Exception {
        return "rights_mgr";
    }

    @RequestMapping("rights_allocate")
    public String rightsAllocate() throws Exception {
        return "rights_allocate";
    }

    @RequestMapping("rights_setting")
    public String rightsSetting() throws Exception {
        return "rights_setting";
    }
}
