package org.erhun.framework.log.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * REST拦截注解
 * 
 * @author gorilla
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface RestInterceptor {

	/** 包含路径 */
	String[] includePaths() default {};

	/** 排除路径 */
	String[] excludePaths() default {};

}
