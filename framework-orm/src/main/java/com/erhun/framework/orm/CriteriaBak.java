package com.erhun.framework.orm;

import com.erhun.framework.orm.annotations.JoinType;
import com.erhun.framework.orm.entities.IEntity;
import com.erhun.framework.basic.utils.lambda.LambdaFunction;
import com.erhun.framework.basic.utils.lambda.LambdaUtils;
import com.erhun.framework.basic.utils.reflection.ReflectionUtils;
import com.erhun.framework.basic.utils.string.StringPool;
import com.erhun.framework.basic.utils.string.StringUtils;

import java.lang.reflect.Field;

/**
 * @author weichao<gorilla@aliyun.com>
 */
public class CriteriaBak {

    private Class masterClass;

    private String masterAlias;

    private StringBuilder sql = new StringBuilder();
    private StringBuilder returnFields = new StringBuilder();
    private StringBuilder joins = new StringBuilder();
    private StringBuilder conditions = new StringBuilder();
    private StringBuilder orderBy = new StringBuilder();
    private StringBuilder groupBy = new StringBuilder();
    private String forUpdate;
    private String limit;

    private Attribute attribute;

    private boolean hasUnclosedBracket;
    private boolean hasUnclosedBracketAfter;

    public CriteriaBak() {
    }

    public CriteriaBak(Class<?> masterClass, String alias) {
        this.masterAlias = alias;
        this.masterClass = masterClass;
    }

    public static <E extends IEntity> CriteriaBak fetch(Class<E> entityClass) {
        return fetch(entityClass, null);
    }

    public static <E extends IEntity> CriteriaBak fetch(Class<E> entityClass, String ... fields) {
        return new CriteriaBak(entityClass, "t").fetch(fields);
    }

    private CriteriaBak fetch(String ...fetchFields) {
        String alias = this.masterAlias;
        Class clazz = masterClass;
        if(attribute != null){
            clazz = attribute.joinClass;
            alias = attribute.alias;
        }
        for (String fn : fetchFields){
            Field field = ReflectionUtils.findField(clazz, fn);
            if(field != null){
                this.returnFields.append(alias).append(".").append(SQLUtils.resolveColumnName(clazz, ReflectionUtils.findField(clazz, fn)));
            }else{
                this.returnFields.append(fn);
            }
            this.returnFields.append(",");
        }
        return this;
    }

    public Attribute with(String fieldName) {
        Attribute attribute = new Attribute(fieldName);
        this.attribute = attribute;
        return attribute;
    }

    public CriteriaBak or() {
        return or(true);
    }

    public CriteriaBak or(boolean appendBracket) {
        return appendCondition(appendBracket, " or ");
    }

    private CriteriaBak appendCondition(boolean appendBracket, String condition) {
        if(appendBracket){
            if(hasUnclosedBracket) {
                conditions.append(StringPool.RIGHT_BRACKET);
                hasUnclosedBracketAfter = false;
                hasUnclosedBracket = false;
            }
        }
        if(appendBracket && conditions.length() > 0 && conditions.charAt(0) != StringPool.LEFT_BRACKET.charAt(0)){
            conditions.insert(0, StringPool.LEFT_BRACKET);
            conditions.append(StringPool.RIGHT_BRACKET);
        }
        if(!hasUnclosedBracket && conditions.length() > 0 || hasUnclosedBracketAfter) {
            conditions.append(condition);
        }
        if(appendBracket){
            conditions.append(StringPool.LEFT_BRACKET);
            hasUnclosedBracket = true;
        }else{
            if(hasUnclosedBracket) {
                hasUnclosedBracketAfter = true;
            }
        }

        return this;
    }

    public CriteriaBak and() {
        return and(true);
    }

    public CriteriaBak and(boolean appendBracket) {
        return appendCondition(appendBracket, " and ");
    }

    public CriteriaBak eq(String fieldName, Object value) {
        return appendCondition(fieldName, SQLExpression.EQUAL, value);
    }

    public <T> CriteriaBak eq(LambdaFunction<T, ?> method, Object value) {
        return appendCondition(method, SQLExpression.EQUAL, value);
    }

    public CriteriaBak gt(String fieldName, Object value) {
        return appendCondition(fieldName, SQLExpression.GREET, value);
    }

    public <T> CriteriaBak gt(LambdaFunction<T, ?> method, Object value) {
        return appendCondition(method, SQLExpression.GREET, value);
    }

    public CriteriaBak ge(String fieldName, Object value) {
        return appendCondition(fieldName, SQLExpression.GREET_EQUAL, value);
    }

    public CriteriaBak lt(String fieldName, Object value) {
        return appendCondition(fieldName, SQLExpression.LESS, value);
    }

    public <T> CriteriaBak lt(LambdaFunction<T, ?> method, Object value) {
        return appendCondition(method, SQLExpression.LESS, value);
    }

    public CriteriaBak le(String fieldName, Object value) {
        return appendCondition(fieldName, SQLExpression.LESS_EQUAL, value);
    }

    public <T> CriteriaBak le(LambdaFunction<T, ?> method, Object value) {
        return appendCondition(method, SQLExpression.LESS_EQUAL, value);
    }

    public CriteriaBak like(String fieldName, Object value) {
        return appendCondition(fieldName, SQLExpression.LIKE, value);
    }

    public <T> CriteriaBak like(LambdaFunction<T, ?> method, Object value) {
        return appendCondition(method, SQLExpression.LIKE, value);
    }

    public CriteriaBak groupBy(String ...fieldNames) {
        for (String fieldName : fieldNames) {
            String columnName = SQLUtils.resolveColumnName(masterClass, fieldName);
            if(StringUtils.isNotEmpty(groupBy)){
                groupBy.append(",");
            }
            groupBy.append(masterAlias).append(StringPool.DOT).append(columnName);
        }
        return this;
    }

    public CriteriaBak orderByDesc(String fieldName) {
        String columnName = SQLUtils.resolveColumnName(masterClass, fieldName);
        if(StringUtils.isNotEmpty(orderBy)){
            orderBy.append(",");
        }
        orderBy.append(masterAlias).append(StringPool.DOT).append(columnName).append(" desc");
        return this;
    }

    public CriteriaBak orderByAsc(String fieldName) {
        String columnName = SQLUtils.resolveColumnName(masterClass, fieldName);
        if(StringUtils.isNotEmpty(orderBy)){
            orderBy.append(",");
        }
        orderBy.append(masterAlias).append(StringPool.DOT).append(columnName).append(" asc");
        return this;
    }

    public CriteriaBak limit(long limit) {
        this.limit += " limit " + limit;
        return this;
    }

    public <T> CriteriaBak appendCondition(LambdaFunction method, SQLExpression expression, Object value) {
        return appendCondition(LambdaUtils.getFieldName(method), expression, value);
    }

    public CriteriaBak appendCondition(String fieldName, SQLExpression expression, Object value) {
        Field field = ReflectionUtils.findField(masterClass, fieldName);
        String columnName = SQLUtils.resolveColumnName(masterClass, field);
        and(false);
        conditions.append(masterAlias).append(".").append(columnName).append(SQLUtils.parseCondition(field.getType(), expression, StringUtils.toString(value)));
        return this;
    }

    public class Attribute {

        private String masterAlias;

        private Class masterClass;

        private String fieldName;

        private String alias;

        private Class joinClass;

        private Attribute(String fieldName) {
            CriteriaBak that = CriteriaBak.this;
            this.fieldName = fieldName;
            this.masterAlias = that.masterAlias;
            this.masterClass = that.masterClass;
            if(that.attribute != null){
                this.masterClass = attribute.joinClass;
                this.masterAlias = attribute.alias;
            }
        }

        public CriteriaBak join(String alias, Class joinClass) {
            return join(alias, joinClass, JoinType.LEFT);
        }

        public CriteriaBak join(String alias, Class joinClass, JoinType joinType) {

            if (joinType == JoinType.LEFT) {
                joins.append(" left join ");
            }
            joins.append(SQLUtils.resolveTableName(joinClass)).append(" ").append(alias);
            joins.append(" on ").append(alias).append(".id=").append(masterAlias).append(".").append(SQLUtils.resolveColumnName(masterClass, ReflectionUtils.findField(masterClass, fieldName)));
            this.alias = alias;
            this.joinClass = joinClass;
            return CriteriaBak.this;
        }
    }

    @Override
    public String toString() {
        int lastIndex = returnFields.length() - 1;
        if(returnFields.charAt(lastIndex) == ','){
            returnFields.setLength(lastIndex);
        }
        StringBuilder buf = new StringBuilder();
        buf.append(sql);
        if(conditions.length() > 0) {
            buf.append(conditions);
            if(hasUnclosedBracket){
                buf.append(StringPool.RIGHT_BRACKET);
            }
        }

        if(StringUtils.isNotEmpty(groupBy)){
            buf.append(" group by ").append(groupBy);
        }

        if(StringUtils.isNotEmpty(orderBy)){
            buf.append(" order by ").append(orderBy);
        }

        return buf.toString();
    }
}
