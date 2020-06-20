package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.security.User;
import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.domain.controller.AbstractBusinessController;
import org.erhun.framework.domain.controller.AbstractUserController;
import org.erhun.framework.orm.QueryParam;
import org.erhun.framework.orm.dto.EmptyDTO;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.entities.ApplicationInfo;
import org.erhun.framework.rbac.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author weichao (gorilla@aliyun.com)
 */
@Controller
@RequestMapping("/admin/rbac/applications")
public class AppController extends AbstractRbacController<String, ApplicationInfo, EmptyDTO> {

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(method = RequestMethod.GET)
    public String listPage() throws Exception {
        return "application";
    }
    
    @Override
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, headers="accept=application/json")
    public ResultPack list(QueryParam<EmptyDTO> app) {

        RbacUser user = this.getRbacUser();

        if(user.isAdmin()) {
            return applicationService.queryByPage(app);
        }else {
            return ResultPack.result(user.getApplications().size(), user.getApplications());
        }

    }
    
    @RequestMapping(value="add", method = RequestMethod.GET)
    public String add(HttpServletRequest request) throws Exception {
       request.setAttribute("app", new ApplicationInfo());
       return "application_edit";
    }

    @RequestMapping(value="{id}/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, @PathVariable String id) throws Exception {

       ApplicationInfo app = applicationService.get(id);

       request.setAttribute("app", app);
       
       return "application_edit";
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.DELETE)
    public ResultPack del(HttpServletRequest request, String id) throws Exception {

       applicationService.delete(id);

       return ResultPack.succeed();
    }
    
    @RequestMapping("allocate")
    public String allocatePage(HttpServletRequest request, String id) throws Exception {
       return "application_allocate";
    }
    
}
