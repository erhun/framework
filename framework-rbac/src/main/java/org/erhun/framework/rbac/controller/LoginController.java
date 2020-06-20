package org.erhun.framework.rbac.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.util.encoders.Hex;
import org.erhun.framework.basic.servlet.session.SessionConstants;
import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.security.RSAUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.controller.BaseController;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.rbac.RbacApplication;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.List;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Controller
@RequestMapping("/login")
public class LoginController extends BaseController {
	
    @Autowired
	private UserService userService;
    
    @Value("portal.old.api.login")
    private String oldLoginUrl;
    
    @Value("login.rsa.public_exponent")
    private String publicExponent;
    
    @Value("login.rsa.public_modulus")
    private String publicModulus;
    
    @Value("login.rsa.private_exponent")
    private String privateExponent;
    
    @Value("login.rsa.private_modulus")
    private String privateModulus;

    @ResponseBody
	@RequestMapping(method= RequestMethod.POST)
	public ResultPack login(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		HttpSession session = request.getSession();
		
	    String userName = request.getParameter("userName");
	    String password = request.getParameter("password");
	    
	    if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
	        throw new BusinessException("${user.username.and.pwd.notempty}");
	    }

	    password = new String(RSAUtils.decryptByPrivateKey(Hex.decode(password.getBytes()), privateModulus, privateExponent));
	    
	    RbacUser user = userService.login(userName, DigestUtils.md5Hex(DigestUtils.md5Hex(password)));

	    /*if(user == null) {
	        String ipCacheKey = "portal.user.login.ip_" + getRemoteIP(request);
	        ipCacheKey.getBytes();
	    }*/
		
		if( user == null ){
		    logger.info( "User: [" + userName+ ", "
	                   + "IP: " + request.getRemoteHost() + ", "
	                   + "Url: " + request.getRequestURI() + ", "
	                   + "OpType: login, " 
	                   + "Status: failed]");
            throw new BusinessException("${user.login.failed}");
		}

		session.invalidate();
	    session = request.getSession(true);
		session.setMaxInactiveInterval(3600);
		session.setAttribute(SessionConstants.LOGIN_USER, user);
	    
		logger.info( "User: [" + user.getAccount()+ ", "
                + "IP: " + request.getRemoteHost() + ", "
                + "Url: " + request.getRequestURI() + ", "
                + "OpType: login, " 
                + "Status: succeed]");

        String referer = request.getHeader("Referer");
        String redirectUrl = null;

        if(StringUtils.isNotBlank(referer)){
            int idx = referer.lastIndexOf("r_url=");
            if(idx > -1) {
                redirectUrl = referer.substring(idx + 6);
                redirectUrl = URLDecoder.decode(redirectUrl, "utf-8");
                List<RbacApplication> apps = user.getApplications();
                for (RbacApplication app : apps) {
                    String loginUrl = app.getLoginUrl();
                    if (StringUtils.isNotBlank(loginUrl)) {
                        idx = loginUrl.indexOf("//");
                        if (idx > -1) {
                            loginUrl = loginUrl.substring(0, loginUrl.indexOf("/", idx + 2));
                        }
                        if (redirectUrl.startsWith(loginUrl)) {
                            return ResultPack.succeed().set("url", "/agents/" + app.getCode() + "?r_url=" + redirectUrl);
                        }
                    }
                }
            }
        }

        /*if(StringUtils.isNotBlank(redirectUrl)){
            request.setAttribute("redirectUrl", redirectUrl);
        }else{
            if(StringUtils.isNotBlank(referer)){
                int idx = referer.lastIndexOf("r_url=");
                if(idx > -1){
                    redirectUrl = referer.substring(idx + 6);
                    session.setAttribute("redirectUrl", redirectUrl);
                }
            }
        }*/

		return ResultPack.SUCCEED;
		
	}

	@ResponseBody
	@RequestMapping(value="getKey", method= RequestMethod.GET)
	public String generateKey(HttpSession session) throws Exception{
		 return  publicExponent + "," + publicModulus; 
	}

}
