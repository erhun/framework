package org.erhun.framework.orm;


import org.erhun.framework.basic.utils.lambda.GetterLambdaFunction;
import org.erhun.framework.basic.utils.string.StringPool;
import org.erhun.framework.basic.utils.string.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
public class CriteriaImpl implements Criteria{

    String masterAlias = SQLUtils.MAIN_ENTITY_ALIAS;

    StringBuilder fetchFields = new StringBuilder();
    StringBuilder orderBy = new StringBuilder();
    StringBuilder groupBy = new StringBuilder();
    String forUpdate = StringPool.EMPTY;
    String limit = StringPool.EMPTY;

    List<Object> conditions;

    boolean hasUnclosedBracket;
    boolean hasUnclosedBracketAfter;

    CriteriaImpl() {
        conditions = new LinkedList<>();
    }

    CriteriaImpl resolveFields(GetterLambdaFunction ...fetchFields) {
        for (GetterLambdaFunction fn : fetchFields){
            String columnName = SQLUtils.resolveColumnName(fn);
            if(this.fetchFields.length() > 0){
                this.fetchFields.append(",");
            }
            this.fetchFields.append(masterAlias).append(".").append(columnName);
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
    public <T> OrderBy groupBy(GetterLambdaFunction<T, ?>... methods) {
        for (GetterLambdaFunction fn : methods) {
            String columnName = SQLUtils.resolveColumnName(fn);
            if (StringUtils.isNotEmpty(groupBy)) {
                groupBy.append(",");
            } else {
                groupBy.append(" group by ");
            }
            groupBy.append(masterAlias).append(StringPool.DOT).append(columnName);
        }
        return new OrderBy(CriteriaImpl.this);
    }

    public class OrderBy implements Criteria{
        private CriteriaImpl criteria;
        OrderBy(CriteriaImpl criteria){
            this.criteria = criteria;
        }
        public <T> LimitBy orderByDesc(GetterLambdaFunction<T, ?>... methods) {
            return CriteriaImpl.this.orderByDesc(methods);
        }
        public <T> LimitBy orderByAsc(GetterLambdaFunction<T, ?>... methods) {
            return CriteriaImpl.this.orderByAsc(methods);
        }
        public ForUpdate limit(long limit) {
            return CriteriaImpl.this.limit(limit);
        }
    }

    public class LimitBy implements Criteria{
        private CriteriaImpl criteria;
        LimitBy(CriteriaImpl criteria){
            this.criteria = criteria;
        }
        public ForUpdate limit(long limit) {
            return criteria.limit(limit);
        }
    }

    public class ForUpdate implements Criteria{
        private CriteriaImpl criteria;
        ForUpdate(CriteriaImpl criteria){
            this.criteria = criteria;
        }
        public Criteria forUpdate() {
            return criteria.forUpdate();
        }
        @Override
        public String toString() {
            return criteria.toString();
        }
    }

    public <T> LimitBy orderByDesc(GetterLambdaFunction<T, ?>... methods) {
        for (GetterLambdaFunction fn : methods) {
            String columnName = SQLUtils.resolveColumnName(fn);
            if (StringUtils.isNotEmpty(orderBy)) {
                orderBy.append(",");
            } else {
                orderBy.append(" order by ");
            }
            orderBy.append(masterAlias).append(StringPool.DOT).append(columnName).append(" desc");
        }
        return new LimitBy(CriteriaImpl.this);
    }

    public <T> LimitBy orderByAsc(GetterLambdaFunction<T, ?>... methods) {
        for (GetterLambdaFunction fn : methods) {
            String columnName = SQLUtils.resolveColumnName(fn);
            if (StringUtils.isNotEmpty(orderBy)) {
                orderBy.append(",");
            } else {
                orderBy.append(" order by ");
            }
            orderBy.append(masterAlias).append(StringPool.DOT).append(columnName).append(" asc");
        }
        return new LimitBy(CriteriaImpl.this);
    }

    public ForUpdate limit(long limit) {
        this.limit = " limit " + limit;
        return new ForUpdate(CriteriaImpl.this);
    }

    public Criteria forUpdate() {
        this.forUpdate = " for update ";
        return this;
    }

    private List<Object> conditions() {
        if (conditions.size() > 0) {
            if (hasUnclosedBracket) {
                conditions.add(StringPool.RIGHT_BRACKET);
            }
        }
        return conditions;
    }

    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder();

        for (Object condition : conditions) {
            if(condition instanceof String){
                buf.append(condition);
            }else{
                Conditional.Condition condition1 =(Conditional.Condition)condition;
                buf.append(condition1.columnName).append(condition1.expression.getOperatorSymbol()).append(condition1.value);
            }
        }

        if (StringUtils.isNotEmpty(this.groupBy)) {
            buf.append(" group by ").append(this.groupBy);
        }

        if (StringUtils.isNotEmpty(this.orderBy)) {
            buf.append(" order by ").append(this.orderBy);
        }

        return buf.toString();
    }
}
