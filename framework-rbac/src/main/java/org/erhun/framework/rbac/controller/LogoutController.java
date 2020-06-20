package org.erhun.framework.rbac.controller;

import org.erhun.framework.domain.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Controller
@RequestMapping("/logout")
public class LogoutController extends BaseController {
    
    @RequestMapping
	public String logout(HttpServletRequest request) throws Exception{
		
		HttpSession session = request.getSession();
		
	    session.setMaxInactiveInterval(-1);
	    
	    Enumeration<String> names = session.getAttributeNames();
	    
	    while(names.hasMoreElements()) {
	        session.removeAttribute(names.nextElement());
	    }
		
		return "redirect:index.html";
		
	}


}
