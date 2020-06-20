package org.erhun.framework.orm.annotations;

import org.erhun.framework.orm.annotations.validator.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeDef {

    String value() default "";
    
    String name() default "";

    String item() default "";

    boolean text() default false;
    
    String alias() default "";
    
    boolean updatable() default true;
    
    boolean creatable() default true;

    Visible [] visible() default {};

    Validator[] validators() default {};

    public static enum Visible{
        ADD,
        EDIT,
        EDIT_HIDDEN,
        LIST_NONE,
    }
}
