package com.erhun.framework.orm.supports.mybatis.config;

import com.erhun.framework.basic.utils.ClassUtils;
import com.erhun.framework.basic.utils.PageResult;
import com.erhun.framework.orm.supports.mybatis.handlers.ArrayTypeHandler;
import com.erhun.framework.orm.supports.mybatis.statement.BatchPreparedStatementHandler;
import com.erhun.framework.orm.supports.mybatis.statement.PagePreparedStatementHandler;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Method;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class MybatisConfigurationImpl extends Configuration{

    public MybatisConfigurationImpl(){
        setJdbcTypeForNull(JdbcType.NULL);
        this.typeHandlerRegistry.register(Object[].class, new ArrayTypeHandler());
    }

    @Override
    public void addMappedStatement(MappedStatement ms) {
        if(!mappedStatements.containsKey(ms.getId())){
            mappedStatements.put(ms.getId(), ms);
        }
    }
    
    @Override
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {

        String id = mappedStatement.getId();

        if(id.endsWith(".addList")){
            return new BatchPreparedStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        }

        if (isPaging(id)) {
            return new PagePreparedStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        }

        return super.newStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
    }

    private boolean isPaging(String id) {

        int idx = id.lastIndexOf(".");

        String className = id.substring(0, idx);
        String methodName = id.substring(idx + 1);

        Class clazz = ClassUtils.getClass(className, false);
        Method [] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if(methodName.equals(method.getName()) && PageResult.class.isAssignableFrom(method.getReturnType())){
                return true;
            }
        }

        return false;
    }
}
