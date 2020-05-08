package com.erhun.framework.orm.annotations;

import java.lang.annotation.*;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationOverride {

    String field();

    String value() default "";

    Class <? extends Annotation> annotation() default Annotation.class;

    Join [] join() default {};

    int min() default 0;

    int max() default 0;

}
