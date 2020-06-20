package org.erhun.framework.domain.controller;

import org.erhun.framework.basic.security.User;
import org.erhun.framework.basic.servlet.session.SessionConstants;
import org.erhun.framework.domain.controller.BaseController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author weichao<gorilla@aliyun.com>
 * 
 */
public abstract class AbstractUserController extends BaseController {

    protected void log(HttpServletRequest request, String userName, String sysOpType, String userOpType, boolean status) {
        logger.info( "User: [" + userName + "], "
                + "IP: [" + request.getRemoteHost() + "], "
                + "Url: [" + request.getRequestURI() + "], "
                + "SysOpType: [" + sysOpType + "], "
                + "UserOpType: [" + userOpType + "], "
                + "Status: [" + status + "]");
    }

    protected User getUser(){
        return (User) getRequest().getSession().getAttribute(SessionConstants.LOGIN_USER);
    }


}
