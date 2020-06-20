package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.security.User;
import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.controller.AbstractBusinessController;
import org.erhun.framework.domain.utils.EntityUtils;
import org.erhun.framework.orm.QueryParam;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.dto.ResourceQueryDTO;
import org.erhun.framework.rbac.entities.ResourceInfo;
import org.erhun.framework.rbac.services.ResourceService;
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
@RequestMapping("/admin/rbac/resources")
public class ResourceController extends AbstractRbacController <String, ResourceInfo, ResourceQueryDTO> {

    @Autowired
    private ResourceService resourceService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String listPage() throws Exception {
        return "resource";
    }

    @Override
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, headers="accept=application/json")
    public ResultPack list(QueryParam<ResourceQueryDTO> queryParam) {
        RbacUser user = this.getRbacUser();
        if(!user.isAdmin()) {
            if(StringUtils.isBlank(queryParam.param().getApplicationId())) {
                queryParam.param().setApplicationId(EntityUtils.toIdString(ListUtils.as(user.getApplications())));
            }
        }
        return resourceService.queryByPage(queryParam);
    }
    
    @RequestMapping(value="add", method = RequestMethod.GET)
    public String add(HttpServletRequest request) throws Exception {
       request.setAttribute("resource", new ResourceInfo());
       return "resource_edit";
    }

    @RequestMapping(value="{id}/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, @PathVariable String id) throws Exception {

       ResourceInfo resource = resourceService.get(id);

       request.setAttribute("resource", resource);
       
       return "resource_edit";
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.DELETE)
    public ResultPack del(HttpServletRequest request, String id) throws Exception {
       resourceService.delete(id);
       return SUCCEED;
    }

}
