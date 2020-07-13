package org.erhun.framework.domain.spring.resolvers;

import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.orm.QueryParam;
import org.erhun.framework.orm.dto.BaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.ServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2018/7/30
 */
public class QueryParamHandlerMethodArgumentResolver extends MapMethodProcessor {

    private final Logger logger = LoggerFactory.getLogger(QueryParamHandlerMethodArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == QueryParam.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Type [] types = parameter.getMethod().getGenericParameterTypes();

        Enhancer cglibEnhancer = null;
        QueryParam tmpQueryParam = null;

        try {

            if(types != null && types.length > 0) {
                ParameterizedType ptt = (ParameterizedType) types[0];
                if(ptt.getActualTypeArguments()[0] instanceof Class) {
                    Class genericParameterClass = (Class) ptt.getActualTypeArguments()[0];
                    cglibEnhancer = new Enhancer();
                    cglibEnhancer.setSuperclass(genericParameterClass);
                    final QueryParam queryDto = (QueryParam) parameter.getParameterType().getConstructor().newInstance();
                    cglibEnhancer.setCallback(new MethodInterceptor() {
                        @Override
                        public Object intercept(Object o, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
                            Object retVal = methodProxy.invokeSuper(o, arguments);
                            String name = method.getName();
                            if (name.startsWith("set") && arguments.length > 0) {
                                name = name.substring(3);
                                queryDto.put(StringUtils.uncapitalize(name), arguments[0]);
                            } else if (name.startsWith("get")) {
                                if (retVal == null) {
                                    return queryDto.get(StringUtils.uncapitalize(name));
                                }
                            }
                            return retVal;
                        }
                    });
                    tmpQueryParam = queryDto;
                }

            }
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

        if(tmpQueryParam == null){
            tmpQueryParam = new QueryParam();
        }

        if(cglibEnhancer != null) {
            BaseDTO genericParameterInstance = (BaseDTO) cglibEnhancer.create();
            tmpQueryParam.param(genericParameterInstance);
        }

        ServletRequest servletRequest = webRequest.getNativeRequest(ServletRequest.class);
        MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(servletRequest);

        for (PropertyValue pv : mpvs.getPropertyValues()){
            tmpQueryParam.put(pv.getName(), pv.getValue());
        }

        return tmpQueryParam;
    }

}
