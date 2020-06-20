package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.domain.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Controller
@RequestMapping("/admin/logs")
public class LogController extends BaseController {
	
    /*@Autowired
	private LogService logService;*/
    
    @RequestMapping(method = RequestMethod.GET)
    public String listPage() throws Exception {
        return "log";
    }
    
    /*@ResponseBody
    @RequestMapping(method= RequestMethod.GET, headers="accept=application/json")
    public ResultPack list(QueryDTO <LogInfo> queryDto, Integer pageNo, Integer pageSize) throws Exception {
        
        return logService.queryByPage(queryDto, pageNo, pageSize);
        
    }*/

}
