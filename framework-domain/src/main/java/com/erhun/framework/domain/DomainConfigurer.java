package com.erhun.framework.domain;

import com.erhun.framework.domain.spring.resolvers.QueryParamHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019/10/16
 */
@Configuration
public class DomainConfigurer {

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter2(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>(requestMappingHandlerAdapter.getArgumentResolvers().size() + 1);
        resolvers.add(new QueryParamHandlerMethodArgumentResolver());
        resolvers.addAll(requestMappingHandlerAdapter.getArgumentResolvers());
        requestMappingHandlerAdapter.setArgumentResolvers(resolvers);
        return requestMappingHandlerAdapter;
    }
}
