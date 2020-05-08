package com.erhun.framework.orm.supports.mybatis.statement;

import com.erhun.framework.basic.utils.PageResultImpl;
import com.erhun.framework.orm.Limits;
import com.erhun.framework.orm.supports.mybatis.PageBoundSql;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2018/7/11
 */
public class PagePreparedStatementHandler extends PreparedStatementHandler {

    public PagePreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameter, rowBounds, resultHandler, new PageBoundSql(mappedStatement.getConfiguration(), boundSql));
    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {

        List data = super.query(statement, resultHandler);
        Long count = executeCount(statement, data.size());

        return (List<E>) Arrays.asList(new PageResultImpl<E>(count, data));

    }

    private Long executeCount(Statement statement, long size) throws SQLException {

        Object parameterObject = this.getParameterHandler().getParameterObject();

        if(parameterObject instanceof Map){
            Map tmp = (Map) parameterObject;
            if(tmp != null){
                Object lm = tmp.get("limit");
                if(lm instanceof Limits){
                    Limits limits = (Limits)lm;
                    if(limits.getOffset() == 0 && size < limits.getSize()){
                        return size;
                    }
                }
            }
        }

        return executeCount(statement);
    }

    private Long executeCount(Statement statement) throws SQLException {

        PageBoundSql pageBoundSql =(PageBoundSql) this.boundSql;

        PreparedStatement prepareStatement = statement.getConnection().prepareStatement(pageBoundSql.getPageCountSql());

        bindingCountStatementParameter(prepareStatement);

        ResultSet resultSet = null;

        try {
            resultSet = prepareStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getLong(1);
            }
        }finally {
            if(resultSet != null) {
                resultSet.close();
            }
            if(prepareStatement != null){
                prepareStatement.close();
            }
        }
        return 0L;
    }

    private void bindingCountStatementParameter(PreparedStatement prepareStatement) {

        List<ParameterMapping> parameterMappings = this.boundSql.getParameterMappings();
        Object parameterObject = this.boundSql.getParameterObject();

        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size() - 2 ; i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) {
                        jdbcType = configuration.getJdbcTypeForNull();
                    }
                    try {
                        typeHandler.setParameter(prepareStatement, i + 1, value, jdbcType);
                    } catch (TypeException e) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    } catch (SQLException e) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    }
                }
            }
        }
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        parameterHandler.setParameters((PreparedStatement) statement);
    }

}
