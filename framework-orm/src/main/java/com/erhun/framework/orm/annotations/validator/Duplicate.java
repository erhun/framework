package com.erhun.framework.orm.annotations.validator;




import com.erhun.framework.orm.annotations.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Duplicate {

    Value[] value();
    
}
