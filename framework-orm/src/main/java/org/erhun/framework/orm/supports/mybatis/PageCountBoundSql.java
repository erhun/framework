package org.erhun.framework.orm.supports.mybatis;

import org.erhun.framework.orm.SQLUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2018/10/10
 */
public class PageCountBoundSql extends BoundSql {

    private BoundSql boundSql;

    public PageCountBoundSql(Configuration configuration, BoundSql boundSql){
        super(configuration, boundSql.getSql(), boundSql.getParameterMappings(), boundSql.getParameterObject());
        this.boundSql = boundSql;
    }

    @Override
    public String getSql() {
        StringBuilder sql = new StringBuilder(super.getSql());
        return SQLUtils.getDialect().getLimitCountSql(sql);
    }

    @Override
    public boolean hasAdditionalParameter(String name) {
        return boundSql.hasAdditionalParameter(name);
    }

    @Override
    public void setAdditionalParameter(String name, Object value) {
        boundSql.setAdditionalParameter(name, value);
    }

    @Override
    public Object getAdditionalParameter(String name) {
        return boundSql.getAdditionalParameter(name);
    }
}
