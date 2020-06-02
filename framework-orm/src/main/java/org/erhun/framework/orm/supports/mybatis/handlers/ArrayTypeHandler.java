package org.erhun.framework.orm.supports.mybatis.handlers;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class ArrayTypeHandler implements TypeHandler {

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
    }

    @Override
    public Object getResult(ResultSet rs, String columnName) throws SQLException {

        int columnCount = rs.getMetaData().getColumnCount();

        if(columnCount == 1){
            return rs.getObject(1);
        }

        Object row [] = new Object[columnCount];

        for (int i = 0; i < row.length;) {
            row[i] = rs.getObject(++i);
        }

        return row;

    }

    @Override
    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }
}
