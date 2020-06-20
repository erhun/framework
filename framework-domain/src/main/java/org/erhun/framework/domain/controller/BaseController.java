package org.erhun.framework.domain.controller;

import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.net.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author weichao<gorilla@aliyun.com>
 * 
 */
@Controller
public abstract class BaseController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final ResultPack SUCCEED = ResultPack.SUCCEED;

    protected final ResultPack FAILED = ResultPack.FAILED;

    protected void output(HttpServletResponse response, Object value) throws IOException {
        if (value != null) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(value.toString());
            response.getWriter().flush();
        }
    }

    protected void log(HttpServletRequest request,String sysOpType, String userOpType, boolean status) {
        logger.info("IP: [" + request.getRemoteHost() + "], "
                + "Url: [" + request.getRequestURI() + "], "
                + "SysOpType: [" + sysOpType + "], "
                + "UserOpType: [" + userOpType + "], "
                + "Status: [" + status + "]");
    }

    protected boolean hasAjax() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return "XMLHttpRequest".equals(ra.getRequest().getHeader("X-Requested-With"));
    }

    protected HttpServletRequest getRequest() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return ra.getRequest();
    }

    protected String getRemoteIP(HttpServletRequest req) {
       return IpUtils.getIp(req);
    }

    protected String readInputStream(InputStream is) throws UnsupportedEncodingException, IOException {

        StringBuilder buffer = new StringBuilder();

        byte b[] = new byte[1024];

        int len = 0;

        while ((len = is.read(b)) != -1) {
            buffer.append(new String(b, 0, len, "UTF-8"));
        }

        return java.net.URLDecoder.decode(buffer.toString(), "UTF-8");

    }

}
