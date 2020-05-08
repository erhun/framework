package com.erhun.framework.log.configurer;

import com.erhun.framework.log.annotation.RestInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 自定义拦截器配置
 * @author gorilla
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer, ApplicationContextAware {

	private final static List<HandlerInterceptor> interceptors = new LinkedList<HandlerInterceptor>();

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		for (HandlerInterceptor interceptor : interceptors) {
			RestInterceptor annotation = interceptor.getClass().getAnnotation(RestInterceptor.class);
			InterceptorRegistration interceptorRegistration = registry.addInterceptor(interceptor);
			interceptorRegistration.addPathPatterns(annotation.includePaths());
			interceptorRegistration.excludePathPatterns(annotation.excludePaths());
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RestInterceptor.class);
		for (Entry<String, Object> bean : beans.entrySet()) {
			HandlerInterceptor interceptor = (HandlerInterceptor) bean.getValue();
			interceptors.add(interceptor);
		}
	}

}
