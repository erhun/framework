package org.erhun.framework.rbac.controller;

import org.erhun.framework.domain.controller.AbstractBusinessController;
import org.erhun.framework.rbac.services.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author weichao (gorilla@aliyun.com)
 */
@Controller
@RequestMapping("/admin/rbac/authcode")
public class AuthcodeController extends AbstractBusinessController {

    @Autowired
    private FunctionService functionService;

    @RequestMapping(method = RequestMethod.GET)
    public String send() throws Exception {
        return "function";
    }
    
}
