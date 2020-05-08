package com.erhun.framework.domain.services.impl;

import com.erhun.framework.basic.utils.net.IpUtils;
import com.erhun.framework.basic.utils.number.NumberUtils;
import com.erhun.framework.domain.services.BusinessContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 * 
 */
@Service
@Scope("prototype")
public class BusinessContextImpl implements BusinessContext, DisposableBean {

    private Map <String, Object> data;

    public BusinessContextImpl() {
    }

    @Override
    public void put(String key, Object value) {
        if (data == null) {
            data = new HashMap <String, Object>();
        }
        data.put(key, value);
    }

    @Override
    public <T> T getParam(String key) {
        Object val = null;
        if (data != null) {
            val = data.get(key);
        }
        if (val == null) {
            return (T) getRequestObject().getParameter((String) key);
        }
        return (T) val;
    }

    public Map <?, ?> getParameterMap() {
        return getRequestObject().getParameterMap();
    }

    @Override
    public Integer getIntegerParam(String key) {
        return NumberUtils.toInt(getParam(key));
    }

    @Override
    public Integer getIntegerParam(String key, Integer defaultValue) {
        return NumberUtils.toInt(getParam(key), defaultValue);
    }

    @Override
    public Integer getLongParam(String key) {
        return NumberUtils.toInt(getParam(key));
    }

    @Override
    public Integer getLongParam(String key, Integer defaultValue) {
        return NumberUtils.toInt(getParam(key), defaultValue);
    }

    @Override
    public <T> T getRequestAttribute(String name) {
        return (T) getRequestObject().getAttribute(name);
    }

    @Override
    public String getRequestHeader(String name) {
        return getRequestObject().getHeader(name);
    }

    public <T> T getSessionAttribute(String name) {
        return (T) getRequestObject().getSession().getAttribute(name);
    }

    @Override
    public void destroy() throws Exception {
    }

    private HttpServletRequest getRequestObject(){
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return ra.getRequest();
    }

    @Override
    public String getClientIP() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(ra != null) {
            HttpServletRequest request = ra.getRequest();
            return IpUtils.getIp(request);
        }
        return null;
    }

}
