package com.erhun.framework.basic.utils.lambda;

import java.io.Serializable;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019-10-12
 */
@FunctionalInterface
public interface LambdaFunction<T, M> extends Serializable {

    /**
     * @param t
     * @return
     */
    M accept(T t);
}

