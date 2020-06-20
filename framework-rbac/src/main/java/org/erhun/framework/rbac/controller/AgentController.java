package org.erhun.framework.rbac.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.erhun.framework.basic.security.User;
import org.erhun.framework.basic.utils.security.Base64;
import org.erhun.framework.basic.utils.security.RSAUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.controller.AbstractUserController;
import org.erhun.framework.rbac.entities.ApplicationInfo;
import org.erhun.framework.rbac.services.ApplicationService;
import org.erhun.framework.rbac.services.AuthcodeService;
import org.erhun.framework.rbac.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @author weichao (gorilla@aliyun.com)
 */
@Controller
@RequestMapping("/agents")
public class AgentController extends AbstractUserController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthcodeService authService;

    //@ResponseBody
    @RequestMapping("{code}")
    public void redirect(HttpServletRequest request, HttpServletResponse response, @PathVariable("code") String code) throws Exception {

        if (StringUtils.isBlank(code)) {
            return;
        }

        User user = getUser();

        if (user.isAdmin()) {
            throw new RuntimeException("admin can't be access biz system!");
        }

        ApplicationInfo appInfo = applicationService.findByCode(code);

        if (appInfo == null) {
            throw new RuntimeException("application is invalid!");
        }

        String privateExponent = appInfo.getPrivateExponent();
        String privateModulus = appInfo.getPrivateModulus();

        if (StringUtils.isEmpty(privateModulus) || StringUtils.isEmpty(privateExponent) || StringUtils.isEmpty(appInfo.getLoginUrl())) {
            throw new RuntimeException("rsa not configure!");
        }

        long timestamp = System.currentTimeMillis();

        String authToken = (String) this.getRequest().getSession().getAttribute("old_portal.auth_token");

        String sign = DigestUtils.md5Hex("appId" + appInfo.getAppId() + "authToken" + authToken +"timestamp" + timestamp + "userId"+user.getId() + appInfo.getAppKey());

        try {
            String redUrl = request.getParameter("r_url");
            String data = appInfo.getAppId() + ";" + user.getId() + ";" + authToken + ";" + timestamp + ";" + sign;
            String token = Base64.encodeToString(RSAUtils.encryptByPrivateKey (data.getBytes(), privateModulus, privateExponent));
            String url = appInfo.getLoginUrl();
            if(url.indexOf("?") == -1){
                url += "?";
            }else{
                url += "&";
            }
            url += "token=" + URLEncoder.encode(token, "utf-8");
            if(StringUtils.isNotBlank(redUrl)){
                url += "&r_url=" + redUrl;
            }
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
