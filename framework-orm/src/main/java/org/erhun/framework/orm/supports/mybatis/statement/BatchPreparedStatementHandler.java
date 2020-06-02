package org.erhun.framework.orm.supports.mybatis.statement;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2018/7/11
 */
public class BatchPreparedStatementHandler extends PreparedStatementHandler {

    public BatchPreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) statement;
        Object parameterObject = this.parameterHandler.getParameterObject();
        if(parameterObject instanceof Map){
            Map params = (Map)parameterObject;
            List entities = (List) params.get("entities");
            if(entities != null && !entities.isEmpty()) {
                int tmp = -1;
                int i = 0;
                for (; i < entities.size(); i++) {
                    params.put("entity", entities.get(i));
                    super.parameterize(preparedStatement);
                    preparedStatement.addBatch();
                    tmp = i % 1000;
                    if(i> 0 && tmp == 0) {
                        preparedStatement.executeBatch();
                    }
                }
                if(tmp != 0 || i == 1){
                    preparedStatement.executeBatch();
                }
            }
        }
        return -2;
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
    }

}
