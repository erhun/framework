package org.erhun.framework.orm.supports.mybatis;

import org.erhun.framework.basic.utils.conversion.ConvertUtils;
import org.erhun.framework.orm.Limits;
import org.erhun.framework.orm.SQLUtils;
import org.erhun.framework.orm.dialect.MySQLDialect;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2018/10/10
 */
public class PageBoundSql extends BoundSql {

    private final static String offsetKey = "#{limit.offset}";

    private final static String sizeKey = "#{limit.size}";

    private final static String limitKey = "#{limit.limit}";

    private long pageNo;

    private int pageSize;

    private List<ParameterMapping> parameterMappings;

    private BoundSql boundSql;

    public PageBoundSql(Configuration configuration, BoundSql boundSql){
        super(configuration, boundSql.getSql(), boundSql.getParameterMappings(), boundSql.getParameterObject());
        this.boundSql = boundSql;
        this.parameterMappings = new ArrayList<>(super.getParameterMappings());

        int size = parameterMappings.size();

        if(size == 0){
            parameterMappings = new ArrayList<>(2);
        }

        Object parameterObject = boundSql.getParameterObject();

        if(parameterObject instanceof Map){
            Map tmp = ((Map) parameterObject);
            Limits limits = (Limits) tmp.get("limit");
            if(limits == null && tmp.containsKey("pageNo")){
                Integer pageNo = ConvertUtils.toInt(tmp.get("pageNo"), 1);
                Integer pageSize = ConvertUtils.toInt(tmp.get("pageSize"), 0);
                if(pageSize < 1 || pageSize > 100){
                    pageSize = 20;
                }
                this.pageNo = pageNo;
                this.pageSize = pageSize;
                tmp.put("limit", Limits.of(pageNo, pageSize));
            }else{
                this.pageNo = limits.getOffset();
                this.pageSize = limits.getSize();
            }
        }

        parameterMappings.add(new ParameterMapping.Builder(configuration, "limit.offset", Long.class).build());

        if(SQLUtils.getDialect() instanceof MySQLDialect) {
            parameterMappings.add(new ParameterMapping.Builder(configuration, "limit.size", Integer.class).build());
        }else{
            parameterMappings.add(new ParameterMapping.Builder(configuration, "limit.limit", Integer.class).build());
        }
    }

    @Override
    public String getSql() {
        StringBuilder sql = new StringBuilder(super.getSql());
        SQLUtils.getDialect().getLimitSQL(sql);

        int idx = sql.indexOf(offsetKey);

        if(idx > -1) {
            sql.replace(idx, idx + offsetKey.length(), "?");
        }

        idx = sql.indexOf(sizeKey);

        if(idx > -1) {
            sql.replace(idx, idx + sizeKey.length(), "?");
        }

        idx = sql.indexOf(limitKey);

        if(idx > -1) {
            sql.replace(idx, idx + limitKey.length(), "?");
        }

        return sql.toString();
    }

    public String getPageCountSql() {
        StringBuilder sql = new StringBuilder(super.getSql());
        return SQLUtils.getDialect().getLimitCountSql(sql);
    }

    public long getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    @Override
    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
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
