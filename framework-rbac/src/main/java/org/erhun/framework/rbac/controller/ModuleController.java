package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.controller.AbstractUserController;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.orm.QueryParam;
import org.erhun.framework.rbac.entities.ApplicationInfo;
import org.erhun.framework.rbac.dto.ModuleQueryDTO;
import org.erhun.framework.rbac.entities.ModuleInfo;
import org.erhun.framework.rbac.services.ApplicationService;
import org.erhun.framework.rbac.services.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 
 * @author weichao (gorilla@aliyun.com)
 */
@Controller
@RequestMapping("/admin/rbac/modules")
public class ModuleController extends AbstractRbacController <String, ModuleInfo, ModuleQueryDTO> {

    @Autowired
    private ModuleService moduleService;
    
    @Autowired
    private ApplicationService appService;

    @RequestMapping(method = RequestMethod.GET)
    public String listPage() throws Exception {
        return "module";
    }
    
    @Override
    @ResponseBody
    @RequestMapping(method= RequestMethod.GET, headers="accept=application/json")
    public ResultPack list(QueryParam <ModuleQueryDTO> queryDto) {
        
        if(StringUtils.isBlank(queryDto.param().getApplicationId())) {
            throw new BusinessException("应用ID不能为空");
        }
        
        return moduleService.queryByPage(queryDto);
        
    }
    
    @ResponseBody
    @RequestMapping(value="queryByAppId", method= RequestMethod.GET)
    public List <ModuleInfo> queryIdAndNameByAppId(HttpServletRequest request, @RequestParam String appId) throws Exception {
        return moduleService.queryIdAndNameByAppId(appId);
    }
    
    @Override
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public ResultPack save(ModuleInfo module) {
        moduleService.save(module);
        return ResultPack.succeed();
    }
    
    @RequestMapping(value="add", method = RequestMethod.GET)
    public String add(HttpServletRequest request) throws Exception {
       ModuleInfo module = new ModuleInfo();
       ApplicationInfo app = appService.get(request.getParameter("applicationId"));
       module.setParentId(request.getParameter("parentId"));
       module.setApplicationId(app.getId());
       module.setApplicationText(app.getName());
       request.setAttribute("module", module);
       return "module_edit";
    }

    @RequestMapping(value="{id}/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, @PathVariable String id) throws Exception {

       ModuleInfo module = moduleService.get(id);
       
       if(!"1".equals(module.getType())) {
           throw new BusinessException("只能编辑普通模块"); 
       }

       ApplicationInfo app = appService.get(module.getApplicationId());
       module.setApplicationText(app.getName());
       
       request.setAttribute("module", module);
       
       return "module_edit";
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.DELETE)
    public ResultPack del(HttpServletRequest request, String id) throws Exception {
        
       ModuleInfo module = moduleService.get(id);
       
       if(!"1".equals(module.getType())) {
           throw new BusinessException("只能删除普通模块"); 
       }
        
       moduleService.delete(id);
       return ResultPack.SUCCEED;
    }

}
