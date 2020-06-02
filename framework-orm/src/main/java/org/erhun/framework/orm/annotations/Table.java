package org.erhun.framework.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    String value() default "";

    String alias() default "";

    String join() default "";
    
    Class <?> nameGenerator() default Class.class;
    
}
