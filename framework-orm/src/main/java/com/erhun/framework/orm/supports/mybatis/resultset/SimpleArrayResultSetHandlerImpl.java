package com.erhun.framework.orm.supports.mybatis.resultset;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class SimpleArrayResultSetHandlerImpl extends DefaultResultSetHandler {
    
    public SimpleArrayResultSetHandlerImpl(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler,
            ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
    }
    
    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        
        ResultSet rs = stmt.getResultSet();
        
        if(!rs.next()){          
            return Collections.emptyList();
        }
        
        List <Object> results = new ArrayList <Object> ();
        
        ResultSetMetaData metaData = rs.getMetaData();
        
        int columnCount = metaData.getColumnCount();
        
        do{
            
            Object row [] = new Object[columnCount]; 
            
            for (int i = 0; i < row.length;) {
                row[i] = rs.getObject(++i);
            }
            
            results.add(row);
            
        }while(rs.next());
        
        return results;
    }
    

}
