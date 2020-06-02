package org.erhun.framework.orm;



import org.erhun.framework.basic.utils.ClassUtils;
import org.erhun.framework.basic.utils.lambda.GetterLambdaFunction;
import org.erhun.framework.basic.utils.lambda.LambdaUtils;
import org.erhun.framework.basic.utils.reflection.ReflectionUtils;
import org.erhun.framework.basic.utils.string.StringPool;
import org.erhun.framework.basic.utils.string.StringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019/10/28
 */
public class Conditional implements Criteria{

    private CriteriaImpl criteria;

    Conditional(CriteriaImpl criteria){
        this.criteria = criteria;
    }

    public Conditional or() {
        return or(true);
    }

    private Conditional or(boolean appendBracket) {
        return appendCondition(appendBracket, " or ");
    }

    private Conditional appendCondition(boolean appendBracket, String condition) {
        if (appendBracket) {
            if (criteria.hasUnclosedBracket) {
                criteria.conditions.add(StringPool.RIGHT_BRACKET);
                criteria.hasUnclosedBracketAfter = false;
                criteria.hasUnclosedBracket = false;
            }
        }
        if (appendBracket && criteria.conditions.size() > 0 && !criteria.conditions.get(0).equals(StringPool.LEFT_BRACKET)) {
            criteria.conditions.add(0, StringPool.LEFT_BRACKET);
            criteria.conditions.add(StringPool.RIGHT_BRACKET);
        }
        if (!criteria.hasUnclosedBracket && criteria.conditions.size() > 0 || criteria.hasUnclosedBracketAfter) {
            criteria.conditions.add(condition);
        }
        if (appendBracket) {
            criteria.conditions.add(StringPool.LEFT_BRACKET);
            criteria.hasUnclosedBracket = true;
        } else {
            if (criteria.hasUnclosedBracket) {
                criteria.hasUnclosedBracketAfter = true;
            }
        }

        return this;
    }

    public Conditional and() {
        return and(true);
    }

    private Conditional and(boolean appendBracket) {
        return appendCondition(appendBracket, " and ");
    }

    /**
     * 等于
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional eq(GetterLambdaFunction<T, ?> method, Object value) {
        return appendCondition(method, SQLExpression.EQUAL, value);
    }

    /**
     * 大于
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional gt(GetterLambdaFunction<T, ?> method, Object value) {
        return appendCondition(method, SQLExpression.GREET, value);
    }

    /**
     * 大于等于
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional ge(GetterLambdaFunction<T, ?> method, Object value) {
        return appendCondition(method, SQLExpression.GREET_EQUAL, value);
    }

    /**
     * 小于
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional lt(GetterLambdaFunction<T, ?> method, Object value) {
        return appendCondition(method, SQLExpression.LESS, value);
    }

    /**
     * 小于等于
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional le(GetterLambdaFunction<T, ?> method, Object value) {
        return appendCondition(method, SQLExpression.LESS_EQUAL, value);
    }

    /**
     * like '%test%'
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional like(GetterLambdaFunction<T, ?> method, String value) {
        return appendCondition(method, SQLExpression.LIKE, value);
    }

    /**
     * like '%test' 前模糊匹配
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional startLike(GetterLambdaFunction<T, ?> method, String value) {
        return appendCondition(method, SQLExpression.START_LIKE, value);
    }

    /**
     * like 'test%' 后模糊匹配
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional endLike(GetterLambdaFunction<T, ?> method, String value) {
        return appendCondition(method, SQLExpression.END_LIKE, value);
    }

    /**
     * not like '%test%'
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional notLike(GetterLambdaFunction<T, ?> method, String value) {
        return appendCondition(method, SQLExpression.NOT_LIKE, value);
    }

    /**
     * not like '%test'
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional startNotLike(GetterLambdaFunction<T, ?> method, String value) {
        return appendCondition(method, SQLExpression.START_NOT_LIKE, value);
    }

    /**
     * not like 'test%'
     *
     * @param method
     * @param value
     * @param <T>
     * @return
     */
    public <T> Conditional endNotLike(GetterLambdaFunction<T, ?> method, String value) {
        return appendCondition(method, SQLExpression.END_NOT_LIKE, value);
    }

    /**
     * in
     *
     * @param method
     * @param values
     * @param <T>
     * @return
     */
    public <T> Conditional in(GetterLambdaFunction<T, ?> method, Object... values) {
        return appendCondition(method, SQLExpression.END_NOT_LIKE, values);
    }

    /**
     * is null
     *
     * @param method
     * @param <T>
     * @return
     */
    public <T> Conditional isNull(GetterLambdaFunction<T, ?> method) {
        and(false);
        criteria.conditions.add(SQLUtils.resolveColumnName(method) + " " + SQLExpression.IS_NULL.getOperatorSymbol() + " ");
        return this;
    }

    /**
     * is null
     *
     * @param method
     * @param <T>
     * @return
     */
    public <T> Conditional isNotNull(GetterLambdaFunction<T, ?> method) {
        and(false);
        criteria.conditions.add(SQLUtils.resolveColumnName(method) + " " + SQLExpression.IS_NOT_NULL.getOperatorSymbol() + " ");
        return this;
    }

    private <T> Conditional appendCondition(GetterLambdaFunction method, SQLExpression expression, Object value) {
        SerializedLambda lambda = LambdaUtils.getSerializedLambda(method);
        Class clazz = ClassUtils.getClass(lambda.getImplClass().replace("/", "."));
        Field field = ReflectionUtils.findField(clazz, ReflectionUtils.getPropertyNameByMethodName(lambda.getImplMethodName()));
        String columnName = SQLUtils.resolveColumnName(clazz, field);
        and(false);
        if (value instanceof String) {
            criteria.conditions.add(criteria.masterAlias + "." + columnName + SQLUtils.parseCondition(field.getType(), expression, StringUtils.toString(value)));
        } else {
            criteria.conditions.add(new Condition(criteria.masterAlias + "." + columnName, value, expression));
        }
        return this;
    }

    /**
     * group by
     *
     * @param methods
     * @param <T>
     * @return
     */
    public <T> CriteriaImpl.OrderBy groupBy(GetterLambdaFunction<T, ?>... methods) {
        return criteria.groupBy(methods);
    }

    public <T> CriteriaImpl.LimitBy orderByDesc(GetterLambdaFunction<T, ?>... methods) {
        return criteria.orderByDesc(methods);
    }

    public <T> CriteriaImpl.LimitBy orderByAsc(GetterLambdaFunction<T, ?>... methods) {
        return criteria.orderByAsc(methods);
    }

    public CriteriaImpl.ForUpdate limit(long limit) {
        return criteria.limit(limit);
    }

    @Override
    public String toString() {
        return criteria.toString();
    }

    class Condition {
        String columnName;
        Object value;
        SQLExpression expression;
        public Condition(String columnName, Object value, SQLExpression expression) {
            this.columnName = columnName;
            this.value = value;
            this.expression = expression;
        }
        @Override
        public String toString() {
            return columnName + " like '#{cc.value}'";
        }
    }

}
