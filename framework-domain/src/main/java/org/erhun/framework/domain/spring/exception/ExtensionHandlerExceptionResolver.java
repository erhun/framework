package org.erhun.framework.domain.spring.exception;

import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.spring.view.AjaxView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

/**
 *
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class ExtensionHandlerExceptionResolver extends AbstractHandlerExceptionResolver {


    private static final Logger logger = LoggerFactory.getLogger(ExtensionHandlerExceptionResolver.class);

    private String EXCEPTION_MESSAGE = "EXCEPTION_MESSAGE";

    private String defaultErrorView;

    private String defaultStatusCode;

    public ExtensionHandlerExceptionResolver(){
        setOrder(0);
    }

    public String getDefaultStatusCode() {
        return defaultStatusCode;
    }

    public void setDefaultStatusCode(String defaultStatusCode) {
        this.defaultStatusCode = defaultStatusCode;
    }

    public String getDefaultErrorView() {
        return defaultErrorView;
    }

    public void setDefaultErrorView(String defaultErrorView) {
        this.defaultErrorView = defaultErrorView;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                              Exception ex) {

        try {
            response.setCharacterEncoding("utf-8");
            request.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(ex.getMessage(), ex);
        }

        if (ex.getMessage() != null && ex.getMessage().indexOf("not login") > -1) {
            return new ModelAndView("redirect:/temporary.html");
        }

        logger.error(ex.getMessage(), ex);

        WebApplicationContext context = RequestContextUtils.findWebApplicationContext(request);

        String msg = ex.getMessage();

        if(ex instanceof SQLException || ex.getCause() instanceof SQLException) {
            msg = "数据库异常,请联系管理员！";
        }else {
            if (msg != null && msg.startsWith("${")) {
                msg = msg.substring(2, msg.length() - 1);
                msg = context.getMessage(msg, null, request.getLocale());
            }
        }

        String accept = request.getHeader("Accept");

        boolean ajax = StringUtils.isNotEmpty(accept) && accept.indexOf("application/json") > -1;

        if (ajax) {
            return new ModelAndView(new AjaxView(ResultPack.failed(msg)));
        } else {
            request.setAttribute(EXCEPTION_MESSAGE, msg);
        }

        return new ModelAndView(defaultErrorView);

    }

}
