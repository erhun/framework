package org.erhun.framework.orm.annotations;


import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Join {

    /**
     * 关联实体
     * @return
     */
    Class <? extends Serializable> clazz() default Serializable.class;

    /**
     * 关联类型
     * @return
     */
    JoinType type() default JoinType.LEFT;

    /**
     * 关联字段
     * @return
     */
    String rel() default "";

    /**
     *
     * @return
     */
    String condition() default "";

    /**
     *
     * @return
     */
    String value() default "id";

    /**
     * 主键
     * @return
     */
    String key() default "id";
    
}
