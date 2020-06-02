package org.erhun.framework.orm;


import org.erhun.framework.basic.utils.lambda.GetterLambdaFunction;

/**
 * @author weichao<gorilla@aliyun.com>
 */
public interface Criteria{

    /**
     * 返回实体中所有字段
     * @return
     */
    static Conditional fetch() {
        CriteriaImpl criteria = new CriteriaImpl();
        return new Conditional(criteria);
    }

    /**
     * 返回实体中指定字段
     * @param fields
     * @param <T>
     * @return
     */
    static <T> Conditional fetch(GetterLambdaFunction<T, ?>... fields) {
        CriteriaImpl criteria = new CriteriaImpl();
        criteria.resolveFields(fields);
        return new Conditional(criteria);
    }

}
