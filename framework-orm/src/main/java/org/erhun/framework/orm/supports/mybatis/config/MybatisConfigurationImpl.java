package org.erhun.framework.orm.supports.mybatis.config;

import org.erhun.framework.orm.supports.mybatis.handlers.ArrayTypeHandler;
import org.erhun.framework.orm.supports.mybatis.statement.BatchPreparedStatementHandler;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.JdbcType;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class MybatisConfigurationImpl extends Configuration {

    public MybatisConfigurationImpl() {
        setJdbcTypeForNull(JdbcType.NULL);
        this.typeHandlerRegistry.register(Object[].class, new ArrayTypeHandler());
    }

    @Override
    public void addMappedStatement(MappedStatement ms) {
        if (!mappedStatements.containsKey(ms.getId())) {
            mappedStatements.put(ms.getId(), ms);
        }
    }

    @Override
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {

        String id = mappedStatement.getId();

        if (id.endsWith(".addList")) {
            return new BatchPreparedStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        }

        return super.newStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
    }

    @Override
    public Executor newExecutor(Transaction transaction) {
        return newExecutor(transaction, defaultExecutorType);
    }
}
