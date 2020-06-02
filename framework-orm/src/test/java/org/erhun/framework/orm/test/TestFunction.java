package org.erhun.framework.orm.test;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019/10/18
 */
@FunctionalInterface
public interface TestFunction<R> {

    R accept();
}
