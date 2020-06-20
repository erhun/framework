package org.erhun.framework.domain.spring.resolvers;

import org.erhun.framework.orm.QueryParam;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.ServletRequest;
import java.lang.reflect.Type;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2018/7/30
 */
public class QueryParamHandlerMethodArgumentResolver extends MapMethodProcessor {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == QueryParam.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Type [] types = parameter.getMethod().getGenericParameterTypes();

        final QueryParam queryParam = (QueryParam) parameter.getParameterType().getConstructor().newInstance();

        ServletRequest servletRequest = webRequest.getNativeRequest(ServletRequest.class);
        MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(servletRequest);

        for (PropertyValue pv : mpvs.getPropertyValues()){
            queryParam.put(pv.getName(), pv.getValue());
        }

        return queryParam;
    }

}
