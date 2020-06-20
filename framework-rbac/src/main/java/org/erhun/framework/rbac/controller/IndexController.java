package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.rbac.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Controller
@RequestMapping("main")
public class IndexController {
    
    @Autowired
    private UserService userService;

    @RequestMapping
    public String index(HttpServletRequest request, HttpServletResponse response){

        String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");

        if(StringUtils.isNotBlank(redirectUrl)){
            request.getSession().removeAttribute("redirectUrl");
            try {
                response.sendRedirect(redirectUrl);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //return "forward:/WEB-INF/themes/theme1/index.html";
        return "main";
    }
    
}
