package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.controller.AbstractBusinessController;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.orm.QueryParam;
import org.erhun.framework.rbac.dto.FunctionQueryDTO;
import org.erhun.framework.rbac.entities.FunctionInfo;
import org.erhun.framework.rbac.entities.ModuleInfo;
import org.erhun.framework.rbac.services.FunctionService;
import org.erhun.framework.rbac.services.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author weichao (gorilla@aliyun.com)
 */
@Controller
@RequestMapping("/admin/rbac/functions")
public class FunctionController extends AbstractBusinessController <String, FunctionInfo, FunctionQueryDTO> {

    @Autowired
    private FunctionService functionService;

    @Autowired
    private ModuleService moduleService;

    @RequestMapping(method = RequestMethod.GET)
    public String listPage() throws Exception {
        return "function";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, headers = "accept=application/json")
    public ResultPack list(QueryParam<FunctionQueryDTO> queryDto, Integer pageNo, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(queryDto.param().getModuleId())) {
            throw new BusinessException("模块ID不能为空");
        }

        return functionService.queryByPage(queryDto);

    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(HttpServletRequest request, @RequestParam String moduleId) throws Exception {

        ModuleInfo module = moduleService.get(moduleId);
        FunctionInfo function = new FunctionInfo();
        function.setModuleId(module.getId());
        function.setModuleText(module.getName());
        request.setAttribute("function", function);
        return "function_edit";
    }

    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, @PathVariable String id) throws Exception {

        FunctionInfo function = functionService.get(id);
        
        ModuleInfo module = moduleService.get(function.getModuleId());
        
        function.setModuleText(module.getName());
        if(StringUtils.isNotEmpty(function.getIcon())) {
            function.setIcon(function.getIcon().replace("&", "&amp;"));
        }
        request.setAttribute("function", function);

        return "function_edit";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    public ResultPack del(HttpServletRequest request, String id) throws Exception {
        functionService.delete(id);
        return ResultPack.succeed();
    }

}
