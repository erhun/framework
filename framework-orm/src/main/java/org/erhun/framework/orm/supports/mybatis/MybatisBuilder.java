package org.erhun.framework.orm.supports.mybatis;

import org.erhun.framework.basic.utils.generics.GenericsUtils;
import org.erhun.framework.basic.utils.reflection.ReflectionUtils;
import org.erhun.framework.basic.utils.string.StringPool;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.orm.AttributeInfo;
import org.erhun.framework.orm.SQLUtils;
import org.erhun.framework.orm.annotations.*;
import org.erhun.framework.orm.dialect.Dialect;
import org.erhun.framework.orm.dialect.MySQLDialect;
import org.erhun.framework.orm.dialect.OracleDialect;
import org.erhun.framework.orm.entities.IEntity;
import org.erhun.framework.orm.entities.IOrderEntity;
import org.erhun.framework.orm.entities.IVirtualDeleteEntity;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.apache.ibatis.session.Configuration;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author weichao<gorilla@aliyun.com>
 *
 */
public final class MybatisBuilder {

    private static final AtomicLong UID_GENERATOR = new AtomicLong(System.currentTimeMillis());

    public static String nextUID() {
        return Long.toHexString(UID_GENERATOR.getAndIncrement());
    }

    public static void buildDefaultMappedStatements(Configuration configuration, Dialect dialect, Class <?> daoClass){

        new Builder(configuration, dialect, daoClass).build();

    }

    private static class Builder{

        private Configuration configuration;
        private Class<?> daoClass;
        private Class<?> entityClass;
        private List <ResultMap> resultMaps;
        private Collection <AttributeInfo> entityFields;
        private Dialect dialect;
        private AttributeOverrides attributeOverrides;
        private Map <String, String> tableAlias;
        private String masterAlias = SQLUtils.MAIN_ENTITY_ALIAS;
        private AtomicInteger tableIndex;

        private Builder(Configuration configuration, Dialect dialect, Class<?> daoClass) {
            this.configuration = configuration;
            this.dialect = dialect;
            this.daoClass = daoClass;
            this.entityClass = GenericsUtils.getSuperInterfaceGenricType(daoClass, 1);
            this.entityFields = SQLUtils.getAttributeDefinition(entityClass); //ClassUtility.getDeclaredFields(entityClass);
            this.tableAlias = new HashMap<>(1);
            this.tableIndex = new AtomicInteger(0);
            this.attributeOverrides = entityClass.getAnnotation(AttributeOverrides.class);
        }

        private void build(){

            if(entityClass == null) {
                return;
            }

            buildResultMap();
            buildAddMappedStatement();
            buildAddListMappedStatement();
            buildDeleteMappedStatement();
            buildDeleteAllMappedStatement();
            buildDeleteColumnMappedStatement();
            buildUpdateMappedStatement();
            buildUpdateColumnMappedStatement();
            buildCountMappedStatement();
            buildGetMappedStatement();
            buildFindColumnMappedStatement();
            buildFindByCriteriaMappedStatement();
            buildFindByIdMappedStatement();
            buildDuplicateMappedStatement();
            buildExistMappedStatement();
            buildQueryAllMappedStatement();
            buildQueryByIdMappedStatement();
            buildQueryCriteriaMappedStatement();
            buildQueryByColumnMappedStatement();
            buildQueryColumnMappedStatement();
            buildQueryMetadataMappedStatement();
            buildQueryByPageMappedStatement();
            buildCountByPageMappedStatement();
            buildInRetriveMappedStatement();
        }

        private void buildResultMap() {

            List <ResultMapping> mappings = new ArrayList <ResultMapping> ();

            for (AttributeInfo attributeInfo : entityFields) {
                String columnName = attributeInfo.getColumnName();
                String nestedResultMapId = null;
                if(StringUtils.isNotEmpty(attributeInfo.getNestedResultMapId())){
                        nestedResultMapId = "." + attributeInfo.getField().getType().getSimpleName();
                    if(StringUtils.isNotBlank(attributeInfo.getNestedResultMapColumn())){
                        columnName = attributeInfo.getNestedResultMapColumn();
                    }
                }

                List <ResultFlag> resultFlags = new ArrayList<>(1);

                if(attributeInfo.isPrimaryKey()) {
                    resultFlags.add(ResultFlag.ID);
                }

                ResultMapping mapping = new ResultMapping.Builder(configuration, attributeInfo.getFieldName(),
                        columnName,
                        attributeInfo.getField().getType()).flags(resultFlags).nestedResultMapId(nestedResultMapId).build();
                mappings.add(mapping);
            }

            resultMaps = new ArrayList <ResultMap>();

            String resultMapId = daoClass.getName() + "." + entityClass.getSimpleName();

            if(!configuration.hasResultMap(resultMapId)){
                ResultMap resultMap = new ResultMap.Builder(configuration, resultMapId, entityClass, mappings).build();
                resultMaps.add(resultMap);
                configuration.addResultMap(resultMap);
            }

            resultMapId = entityClass.getName();

            if(!configuration.hasResultMap(resultMapId)){
                ResultMap resultMap = new ResultMap.Builder(configuration, resultMapId, entityClass, mappings).build();
                resultMaps.add(resultMap);
                configuration.addResultMap(resultMap);
            }

            resultMapId = "." + entityClass.getSimpleName();

            if(!configuration.hasResultMap(resultMapId)){
                ResultMap resultMap = new ResultMap.Builder(configuration, resultMapId, entityClass, mappings).build();
                resultMaps.add(resultMap);
                configuration.addResultMap(resultMap);
            }

        }

        private void buildGetMappedStatement() {
            String statement = daoClass.getName() + ".get";
            addMappedStatement(configuration, statement, buildGetSQL(), SqlCommandType.SELECT, resultMaps, null);
        }

        private void buildFindByIdMappedStatement() {
            String statement = daoClass.getName() + ".findById";
            addMappedStatement(configuration, statement, buildFindByIdSQL(), SqlCommandType.SELECT, resultMaps, null);
        }

        private void buildFindColumnMappedStatement() {
            String statement = daoClass.getName() + ".findByColumn";
            addMappedStatement(configuration, statement, buildFindByColumnSQL(), SqlCommandType.SELECT, resultMaps, null);
        }

        private void buildFindByCriteriaMappedStatement() {
            String statement = daoClass.getName() + ".findByCriteria";
            addMappedStatement(configuration, statement, buildFindByCriteriaSQL(), SqlCommandType.SELECT, resultMaps, null);
        }

        private void buildDuplicateMappedStatement() {
            String statement = daoClass.getName() + ".duplicate";
            addMappedStatement(configuration, statement, buildDuplicateSQL(), SqlCommandType.SELECT, null, "int");
        }

        private void buildExistMappedStatement() {
            String statement = daoClass.getName() + ".exist";
            addMappedStatement(configuration, statement, buildExistSQL(), SqlCommandType.SELECT, null, null);
        }

        private void buildInRetriveMappedStatement() {
            String statement = daoClass.getName() + ".in";
            addMappedStatement(configuration, statement, buildInRetrieveSQL(), SqlCommandType.SELECT, resultMaps, null);
        }

        private void buildQueryAllMappedStatement() {
            String statement = daoClass.getName() + ".queryAll";
            addMappedStatement(configuration, statement, buildQueryAllSQL(), SqlCommandType.SELECT, resultMaps, null);
        }

        private void buildQueryByIdMappedStatement() {
            String statement = daoClass.getName() + ".queryById";
            addMappedStatement(configuration, statement, buildQueryByIdSQL(), SqlCommandType.SELECT, resultMaps, null);
        }

        private void buildQueryCriteriaMappedStatement() {
            String statement = daoClass.getName() + ".queryByCriteria";
            addMappedStatement(configuration, statement, buildQueryCriteriaSQL(), SqlCommandType.SELECT, resultMaps, null);
        }

        private void buildQueryByColumnMappedStatement() {
            String statement = daoClass.getName() + ".queryByColumn";
            addMappedStatement(configuration, statement, buildQueryByColumnSQL(), SqlCommandType.SELECT, resultMaps, null);
        }

        private void buildQueryColumnMappedStatement() {
            String statement = daoClass.getName() + ".queryColumn";
            addMappedStatement(configuration, statement, buildQueryColumnSQL(), SqlCommandType.SELECT, null, "object[]");
        }

        private void buildQueryMetadataMappedStatement() {
            String statement = daoClass.getName() + ".queryMetadata";
            addMappedStatement(configuration, statement, buildQueryMetadataSQL(), SqlCommandType.SELECT, null, "object[]");
        }

        private void buildQueryByPageMappedStatement() {
            String statement = daoClass.getName() + ".queryByPage";
            addMappedStatement(configuration, statement, buildQueryByPageSQL(), SqlCommandType.SELECT, resultMaps, null);
        }

        private void buildCountByPageMappedStatement() {
            String statement = daoClass.getName() + ".countByPage";
            addMappedStatement(configuration, statement, buildCountByPageSelectiveSQL(), SqlCommandType.SELECT, null, "long");
        }

        private void buildCountMappedStatement() {
            String statement = daoClass.getName() + ".count";
            addMappedStatement(configuration, statement, buildCountSQL(), SqlCommandType.SELECT, null, "long");
        }

        private void buildAddMappedStatement() {
            String statement = daoClass.getName() + ".add";
            addMappedStatement(configuration, statement, buildInsertSQL(), SqlCommandType.INSERT, null, null);
        }

        private void buildAddListMappedStatement() {
            String statement = daoClass.getName() + ".addList";
            addMappedStatement(configuration, statement, buildInsertListSQL(), SqlCommandType.INSERT, null, null);
        }

        private void buildDeleteMappedStatement() {
            String statement = daoClass.getName() + ".delete";
            String sql =  buildDeleteSQL();
            if(sql.indexOf("<delete") > -1) {
                addMappedStatement(configuration, statement, sql, SqlCommandType.DELETE, null, null);
            }else{
                addMappedStatement(configuration, statement, sql, SqlCommandType.UPDATE, null, null);
            }
        }

        private void buildDeleteAllMappedStatement() {
            String statement = daoClass.getName() + ".deleteAll";
            String sql =  buildDeleteAllSQL();
            if(sql.indexOf("<delete") > -1) {
                addMappedStatement(configuration, statement, sql, SqlCommandType.DELETE, null, null);
            }else{
                addMappedStatement(configuration, statement, sql, SqlCommandType.UPDATE, null, null);
            }
        }

        private void buildDeleteColumnMappedStatement() {
            String statement = daoClass.getName() + ".deleteByColumn";
            SqlCommandType commandType;
            String sql = buildDeleteByColumnSQL();
            if(sql.startsWith("<update")) {
                commandType = SqlCommandType.UPDATE;
            }else {
                commandType = SqlCommandType.DELETE;
            }
            addMappedStatement(configuration, statement, buildDeleteByColumnSQL(), commandType, null, null);
        }

        private void buildUpdateMappedStatement() {
            String statement = daoClass.getName() + ".update";
            addMappedStatement(configuration, statement, buildUpdateSQL(), SqlCommandType.UPDATE, null, null);
        }

        private void buildUpdateColumnMappedStatement() {
            String statement = daoClass.getName() + ".updateColumn";
            addMappedStatement(configuration, statement, buildUpdateColumnSQL(), SqlCommandType.UPDATE, null, null);
        }

        private String buildInsertSQL() {

            StringBuilder buf1 = new StringBuilder();
            StringBuilder buf2 = new StringBuilder();

            Field idField = null;
            String incrementValue = null;
            boolean findIncrementId = false;

            if(attributeOverrides != null) {
                AnnotationOverride[] annotations = attributeOverrides.annotations();
                for (AnnotationOverride a : annotations) {
                    if(a.annotation() == AutoIncrement.class) {
                        incrementValue = a.value();
                        idField = ReflectionUtils.findField(entityClass, a.field());
                        if(idField == null){
                            throw new IllegalArgumentException(entityClass.getName() + " increment field is invalid.");
                        }
                        findIncrementId = true;
                        break;
                    }
                }
            }

            if(idField == null) {
                idField = ReflectionUtils.findField(entityClass, "id");
                AutoIncrement incrementId = idField.getAnnotation(AutoIncrement.class);
                if(incrementId != null){
                    incrementValue = incrementId.value();
                    findIncrementId = true;
                }
            }


            buf1.append("<insert keyProperty=\"entity.id\"");

            if (findIncrementId) {
                if (dialect instanceof MySQLDialect) {
                    buf1.append(" useGeneratedKeys=\"true\" ");
                } else if (dialect instanceof OracleDialect) {
                    if(StringUtils.isBlank(incrementValue)){
                        throw new IllegalArgumentException( entityClass.getName() + " increment id not config value");
                    }
                    String type;
                    if(idField.getType() == Integer.class) {
                        type = "int";
                    }else{
                        type = "long";
                    }
                    if(incrementValue.indexOf("select") == -1){
                        incrementValue = "select " + incrementValue + " from dual";
                    }
                    buf1.append("<selectKey keyProperty=\"id\" resultType=\"" + type + "\" order=\"BEFORE\">" + incrementValue + "</if></selectKey>");
                }
            }

            buf1.append(">");


            buf1.append("insert into ").append(SQLUtils.resolveTableName(entityClass));
            buf1.append(" <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");

            buf2.append(" values ");
            buf2.append(" <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");

            buildInsertColumns(buf1, buf2, findIncrementId);

            buf1.append("</trim>");
            buf2.append("</trim>");

            buf1.append(buf2);
            buf1.append("</insert>");

            return buf1.toString();

        }

        private String buildInsertListSQL() {

            StringBuilder buf1 = new StringBuilder();
            StringBuilder buf2 = new StringBuilder();

            buf1.append("<insert keyProperty=\"entity.id\">");
            buf1.append("insert into ").append(SQLUtils.resolveTableName(entityClass)).append(" (");

            for (AttributeInfo field : entityFields) {
                if(field.isCreatable()) {
                    buf1.append(field.getColumnName()).append(",");
                    buf2.append("#{entity.").append(field.getFieldName()).append("},");
                }
            }

            buf1.setLength(buf1.length()-1);
            buf2.setLength(buf2.length()-1);

            buf1.append(") values (").append(buf2).append(")");
            buf1.append("</insert>");

            return buf1.toString();

        }

        private void buildInsertColumns(StringBuilder buf1, StringBuilder buf2, boolean excludeIdField) {
            for (AttributeInfo field : entityFields) {
                if(field.isCreatable()) {
                    buf1.append("<if test=\"entity != null and entity.").append(field.getFieldName()).append("!=null").append("\">");
                    buf1.append(field.getColumnName()).append(",");
                    buf1.append("</if>");
                    buf2.append("<if test=\"entity != null and entity.").append(field.getFieldName()).append("!=null").append("\">");
                    buf2.append("#{entity.").append(field.getFieldName()).append("}, ");
                    buf2.append("</if>");
                }
            }
        }

        private String buildUpdateSQL() {

            StringBuilder buf = new StringBuilder();

            buf.append("<update>");
            buf.append("update ").append(SQLUtils.resolveTableName(entityClass)).append("<set>");

            buf.append("<choose>");
            buf.append("<when test='affects!=null and affects.length > 0'>");
            buf.append("<foreach collection=\"affects\" item=\"pv\" ");
            buf.append("index=\"index\" open=\"\" close=\"\" separator=\",\">");
            buf.append("${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\""+entityClass.getName()+"\".class,pv.name)}=#{entity.${pv.name}}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise>");
            for (AttributeInfo attributeInfo : entityFields) {
                if(attributeInfo.isUpdatable()) {
                    buf.append(attributeInfo.getColumnName()).append("=#{entity.").append(attributeInfo.getFieldName()).append("},");
                }
            }
            buf.append("</otherwise>");
            buf.append("</choose>");
            buf.append("</set>");
            buf.append(" where id=#{entity.id}");
            buf.append("</update>");

            return buf.toString();

        }

        private String buildUpdateColumnSQL() {

            return "<update>update " + SQLUtils.resolveTableName(entityClass) + " set ${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\""+entityClass.getName()+"\", column)}=#{value} where id=#{id}</update>";

        }

        private String buildCountSQL() {

            StringBuilder buf = new StringBuilder();

            buf.append("<select>");
            buf.append("select count(*) from ").append(SQLUtils.resolveTableName(entityClass)).append(" where ");
            buf.append("<foreach collection=\"pvs\" item=\"pv\" open=\"\" close=\"\" separator=\" and \">");
            buf.append("<choose><when test=\"pv.value !=null and (pv.value instanceof java.util.List or pv.value.class.array)\">");
            buf.append("${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\""+entityClass.getName()+"\", pv.name)} in ");
            buf.append("<foreach collection=\"pv.value\" item=\"v\" open=\"(\" close=\")\" separator=\",\">");
            buf.append("#{v}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise>${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\""+entityClass.getName()+"\", pv.name)}${@org.erhun.framework.orm.SQLUtils@parseCondition(\""+entityClass.getName()+"\",pv.name,pv.value)}</otherwise></choose>");
            buf.append("</foreach>");

            buf.append("</select>");

            return buf.toString();

        }

        private String buildQueryAllSQL() {

            StringBuilder buf = new StringBuilder("<select>select ");

            for (AttributeInfo field : entityFields) {
                if(field.isQueryable()){
                    buf.append(field.getColumnName()).append(",");
                }
            }

            buf.setLength(buf.length() - 1);
            buf.append(" from ").append(SQLUtils.resolveTableName(entityClass)).append(" where 1=1");

            buildWhere(buf);
            buildOrderBy(buf);

            buf.append("</select>");

            return buf.toString();

        }

        private void buildWhere(StringBuilder buf) {
            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                buf.append(" and deleted='1'");
            }
        }

        private void buildOrderBy(StringBuilder buf) {
            if(IOrderEntity.class.isAssignableFrom(entityClass)){
                buf.append(" order by show_index");
            }
        }

        private String buildQueryByIdSQL() {

            StringBuilder buf = new StringBuilder("<select>select ");

            for (AttributeInfo field : entityFields) {
                if(field.isQueryable()){
                    buf.append(field.getColumnName()).append(",");
                }
            }

            buf.setLength(buf.length() - 1);
            buf.append(" from ").append(SQLUtils.resolveTableName(entityClass));
            buf.append(" where id in(${id})");
            buf.append("</select>");

            return buf.toString();

        }

        private String buildQueryCriteriaSQL() {

            StringBuilder buf = new StringBuilder();
            buf.append("<select>");
            buildQueryCriteriaSQL(buf);
            buf.append("</select>");

            return buf.toString();

        }

        private void buildQueryCriteriaSQL(StringBuilder buf) {

            buf.append("select ");
            buf.append("<choose><when test=\"criteria.criteria.fetchFields!=null and criteria.criteria.fetchFields.length()&gt;0\">${criteria.criteria.fetchFields}</when>");
            buf.append("<otherwise>");
            for (AttributeInfo field : entityFields) {
                if (field.isQueryable()) {
                    buf.append(masterAlias).append(".").append(field.getColumnName()).append(",");
                }
            }
            buf.setLength(buf.length() - 1);
            buf.append("</otherwise></choose>");

            buf.append(" from ");
            buf.append(SQLUtils.resolveTableName(entityClass)).append(" ").append(masterAlias);
            buf.append(" where ");

            if (IVirtualDeleteEntity.class.isAssignableFrom(entityClass)) {
                buf.append("(");
            }

            buildCriteriaConditions(buf);

            if (IVirtualDeleteEntity.class.isAssignableFrom(entityClass)) {
                buf.append(") and is_deleted='1'");
            }

            buf.append(" ${criteria.criteria.groupBy} ");
            buf.append(" ${criteria.criteria.orderBy} ");
            //buf.append(" ${criteria.criteria.limit} ");
            //buf.append(" ${criteria.criteria.forUpdate} ");
        }

        private void buildCriteriaConditions(StringBuilder buf) {
            buf.append("<foreach collection=\"criteria.criteria.conditions()\" item=\"cc\" open=\"\" close=\"\" separator=\"\">");
            buf.append("<choose><when test=\"cc instanceof String\">${cc}</when>");
            buf.append("<otherwise>");
            //buf.append("${cc.columnName}${cc.expression.condition}#{cc.value}");
            buf.append("<choose><when test=\"cc.value !=null and (cc.value instanceof java.util.List or cc.value.class.array)\">");
            buf.append("${cc.columnName} in ");
            buf.append("<foreach collection=\"cc.value\" item=\"v\" open=\"(\" close=\")\" separator=\",\">");
            buf.append("#{v}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise>");
            buf.append("${cc.columnName}${cc.expression.getOperatorSymbol}#{cc.value}");
            buf.append("</otherwise></choose>");
            buf.append("</otherwise></choose>");
            buf.append("</foreach>");
        }

        private StringBuilder buildQueryPageSelectiveBody(StringBuilder select){

            StringBuilder condition = new StringBuilder();
            StringBuilder from = new StringBuilder();

            Set<String> overrideNames = new HashSet<String>(2);

            for (AttributeInfo field : entityFields) {

                if(!field.isQueryable() || overrideNames.contains(field.getFieldName())){
                    continue;
                }

                String item = field.getItem();
                String columnName = field.getColumnName();
                String alias = null;

                if(StringUtils.isNotEmpty(field.getJoinKey())) {
                    masterAlias = parseJoin(field, select, from);
                }else if(StringUtils.isNotBlank(item)){
                    alias = "t_" + columnName;
                    from.append(" left join t_code_item ").append(alias).append(" on ").append(alias).append(".group_id='").append(item).append("' and ").append(alias).append(".code=").append(masterAlias).append(".").append(columnName);
                    select.append(alias).append(".name ").append(columnName).append(",");
                }else {
                    if(field.isQueryable()) {
                        select.append(masterAlias).append(".").append(columnName).append(",");
                    }
                }

                condition.append("<if test=\"entity.").append(field.getFieldName()).append("!=null\">");
                condition.append("<choose><when test=\"entity.").append(field.getFieldName()).append(" instanceof String and entity.").append(field.getFieldName()).append("!=''\">");

                if (StringUtils.isNotBlank(item)) {
                    condition.append(" and ").append(alias).append(".name like concat(#{entity.").append(field.getFieldName()).append("},'%')");
                } else {
                    condition.append(" and ").append("t.").append(columnName).append("${@org.erhun.framework.orm.SQLUtils@parseCondition(\""+entityClass.getName()+"\",\""+field.getFieldName()+"\",entity."+field.getFieldName()+")}");
                    //condition.append(" and ").append("t.").append(columnName).append(" like concat(#{entity.").append(with.getName()).append("},'%')");
                }
                condition.append("</when><otherwise>");
                condition.append(" and ").append(masterAlias).append(".").append(columnName).append("=#{entity.").append(field.getFieldName()).append("}");
                condition.append("</otherwise></choose>");
                condition.append("</if>");
            }

            condition.append("<if test=\"param3!=null and param3 instanceof org.erhun.framework.orm.Criteria\">");
            condition.append( " and (" );
            buildCriteriaConditions(condition);
            condition.append(")");
            condition.append("</if>");


            select.setLength(select.length() -1);
            select.append(" from ").append(SQLUtils.resolveTableName(entityClass)).append(" t").append(from).append(" where 1=1 ").append(condition);

            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                select.append(" and t.is_deleted='1'");
            }

            return select;
        }

        private String buildQueryByPageSQL() {


            StringBuilder sql = new StringBuilder("select ");

            sql.append("<choose><when test='fetchColumns!=null'>");
            sql.append("<foreach collection=\"fetchColumns\" item=\"column\" separator=\",\">");
            sql.append("${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\""+entityClass.getName()+"\",pv.name)}");
            sql.append("</foreach>");
            sql.append("</when>");
            sql.append("<otherwise>*</otherwise></choose>");
            sql.append(" from (select ");

            buildQueryPageSelectiveBody(sql);

            OrderBy orderBy = entityClass.getAnnotation(OrderBy.class);

            if(orderBy != null){
                String [] val = orderBy.value();
                if(val.length > 0){
                    sql.append(" order by ");
                    for (String v : val) {
                        String t [] = v.split(" ");
                        Field field = ReflectionUtils.findField(entityClass, t[0]);
                        if(field != null){
                            String cn = SQLUtils.resolveColumnName(entityClass, field);
                            sql.append(cn);
                            if(t.length>1){
                                sql.append(" ").append(t[1]);
                            }
                        }
                    }
                }
            }else{
                if(IOrderEntity.class.isAssignableFrom(entityClass)){
                    sql.append("order by show_index");
                }
            }

            //dialect.getLimitSQL(sql);

            sql.insert(0, "<select>");
            sql.append(") t");
            sql.append("</select>");

            return sql.toString();

        }

        private String buildCountByPageSelectiveSQL() {

            StringBuilder sql = new StringBuilder("<select>select count(*) from (select ");

            buildQueryPageSelectiveBody(sql);

            sql.append(") t </select>");

            return sql.toString();

        }

        private String buildInRetrieveSQL(){

            StringBuilder buf = new StringBuilder();

            buf.append("<select>");
            buf.append("select ");

            for (AttributeInfo field : entityFields) {
                if(field.isQueryable()){
                    buf.append(field.getColumnName()).append(",");
                }
            }

            buf.setLength(buf.length() -1);
            buf.append(" from ").append(SQLUtils.resolveTableName(entityClass));
            buf.append(" where ${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\"").append(entityClass.getName()).append("\",column)} in ");

            buf.append("<foreach collection='values' item='id' open='(' separator=',' close=')'>");
            buf.append("${id}");
            buf.append("</foreach>");

            buf.append("</select>");

            return buf.toString();

        }

        private String buildGetSQL() {

            StringBuilder buf = new StringBuilder();

            buf.append("<select>");
            buf.append("select ");

            for (AttributeInfo field : entityFields) {
                if (field.isQueryable()) {
                    buf.append(field.getColumnName()).append(",");
                }
            }

            if (entityFields.size() > 0) {
                buf.setLength(buf.length()-1);
            }

            buf.append(" from ");
            buf.append(SQLUtils.resolveTableName(entityClass));
            buf.append(" where id=#{id}");

            buf.append("</select>");

            return buf.toString();

        }

        private String buildFindByIdSQL() {

            StringBuilder buf1 = new StringBuilder();
            StringBuilder buf2 = new StringBuilder();

            buf1.append("<select>");
            buf1.append("select ");

            for (AttributeInfo field : entityFields) {
                if (field.isQueryable()) {
                    buf1.append(masterAlias).append(StringPool.DOT).append(field.getColumnName()).append(",");
                }
            }

            if (entityFields.size() > 0) {
                buf1.setLength(buf1.length()-1);
            }

            buf1.append(" from ");
            buf1.append(SQLUtils.resolveTableName(entityClass));
            buf1.append(" where id=#{id}");
            buf1.append("</select>");

            return buf1.toString();

        }

        private String buildFindByCriteriaSQL() {

            StringBuilder buf = new StringBuilder();
            buf.append("<select>");
            buildQueryCriteriaSQL(buf);
            dialect.getSingleLimitSQL(buf);
            buf.append("</select>");

            return buf.toString();

        }

        private String buildFindByColumnSQL() {

            StringBuilder buf = new StringBuilder();

            buf.append("<select>");
            buf.append("select ");

            for (AttributeInfo field : entityFields) {
                if (field.isQueryable()) {
                    buf.append(field.getColumnName()).append(",");
                }
            }

            buf.setLength(buf.length()-1);

            buf.append(" from ");
            buf.append(SQLUtils.resolveTableName(entityClass));
            buf.append(" where 1=1 and ");
            buf.append("<foreach collection=\"pvs\" item=\"pv\" ");
            buf.append("index=\"index\" open=\"\" close=\"\" separator=\" and \">");
            buf.append("${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\"").append(entityClass.getName());
            buf.append("\",pv.name)}${@org.erhun.framework.orm.SQLUtils@parseCondition(\""+entityClass.getName()+"\",pv.name,pv.value)}</foreach>");

            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                buf.append(" and is_deleted='1'");
            }

            dialect.getSingleLimitSQL(buf);
            buf.append("</select>");

            return buf.toString();

        }

        private String buildQueryColumnSQL() {

            StringBuilder buf = new StringBuilder();

            buf.append("<select>");
            buf.append("select");
            buf.append("<choose><when test='fetchColumns!=null'>");
            buf.append("<foreach collection=\"fetchColumns\" item=\"column\" separator=\",\">");
            buf.append("${column}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise>*</otherwise></choose>");
            buf.append(" from ");
            buf.append(SQLUtils.resolveTableName(entityClass));
            buf.append(" where ");
            buf.append("<foreach collection=\"pvs\" item=\"pv\" open=\"\" close=\"\" separator=\" and \">");
            buf.append("<choose><when test=\"pv.value !=null and (pv.value instanceof java.util.List or pv.value.class.array)\">");
            buf.append("${@org.erhun.framework.orm.SQLUtils.resolveColumnName(\""+entityClass.getName()+".class\", pv.name)} in ");
            buf.append("<foreach collection=\"pv.value\" item=\"v\" open=\"(\" close=\")\" separator=\",\">");
            buf.append("#{v}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise>");
            buf.append("${pv.name}${@org.erhun.framework.orm.SQLUtils@parseCondition(\""+entityClass.getName()+"\",pv.name,pv.value)}");
            buf.append("</otherwise></choose>");
            buf.append("</foreach>");

            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                buf.append(" and is_deleted='1'");
            }

            if(IOrderEntity.class.isAssignableFrom(entityClass)){
                buf.append("order by show_index");
            }

            buf.append("</select>");

            return buf.toString();

        }

        private String buildQueryByColumnSQL() {

            StringBuilder buf = new StringBuilder();



            buf.append("<select> ");
            buf.append("select <choose> <when test=\"(param1 instanceof String) and (param1 != null and param1 !='')\">${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\""+entityClass.getName()+"\",columns)}</when><otherwise>");

            for (AttributeInfo field : entityFields) {
                if (field.isQueryable()) {
                    buf.append(field.getColumnName()).append(",");
                }
            }

            buf.setLength(buf.length()-1);

            buf.append("</otherwise></choose>");
            buf.append(" from ");
            buf.append(SQLUtils.resolveTableName(entityClass));
            buf.append(" where ");
            buf.append("<foreach collection=\"pv\" item=\"pv\" open=\"\" close=\"\" separator=\" and \">");
            buf.append("<choose><when test=\"pv.value !=null and (pv.value instanceof java.util.List or pv.value.class.array)\">");
            buf.append("${pv.name} in ");
            buf.append("<foreach collection=\"pv.value\" item=\"v\" open=\"(\" close=\")\" separator=\",\">");
            buf.append("#{v}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise> ${pv.name}${@org.erhun.framework.orm.SQLUtils@parseCondition(\""+entityClass.getName()+"\",pv.name,pv.value)}</otherwise></choose>");
            buf.append("</foreach>");

            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                buf.append(" and is_deleted='1'");
            }

            if(IOrderEntity.class.isAssignableFrom(entityClass)){
                buf.append("order by show_index");
            }

            buf.append("</select>");

            return buf.toString();

        }

        private String buildQueryMetadataSQL() {

            StringBuilder buf = new StringBuilder();

            buf.append("<select> select ");
            buf.append("<choose><when test='fetchColumns!=null'>");
            buf.append("<foreach collection=\"fetchColumns\" item=\"column\" separator=\",\">");
            buf.append("${column}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise>*</otherwise></choose>");
            buf.append(" from (select ");

            buildQueryPageSelectiveBody(buf);


            buf.append(") t </select>");

            return buf.toString();

        }

        private String buildDuplicateSQL() {

            StringBuilder buf = new StringBuilder();

            buf.append("<select>");
            buf.append("select count(*) from ").append(SQLUtils.resolveTableName(entityClass));
            buf.append(" where id!=#{id} and ");
            buf.append("<foreach collection=\"pvs\" item=\"pv\" open=\"\" close=\"\" separator=\" and \">");
            buf.append("<choose><when test=\"pv.value !=null and (pv.value instanceof java.util.List or pv.value.class.array)\">");
            buf.append("${pv.name} in ");
            buf.append("<foreach collection=\"pv.value\" item=\"v\" open=\"(\" close=\")\" separator=\",\">");
            buf.append("#{v}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise> ${pv.name}${@org.erhun.framework.orm.SQLUtils@parseCondition(\""+entityClass.getName()+"\",pv.name,pv.value)}</otherwise></choose>");
            buf.append("</foreach>");
            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                buf.append(" and is_deleted='1'");
            }
            buf.append("</select>");

            return buf.toString();

        }

        private String buildExistSQL() {

            StringBuilder buf = new StringBuilder();

            buf.append("<select>");
            buf.append("select count(*) from ").append(SQLUtils.resolveTableName(entityClass));
            buf.append("<foreach collection=\"pv\" item=\"pv\" open=\"\" close=\"\" separator=\" and \">");
            buf.append("<choose><when test=\"pv.value !=null and (pv.value instanceof java.util.List or pv.value.class.array)\">");
            buf.append("${pv.name} in ");
            buf.append("<foreach collection=\"pv.value\" item=\"v\" open=\"(\" close=\")\" separator=\",\">");
            buf.append("#{v}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise> ${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\""+entityClass.getName()+"\",pv.name)}${@org.erhun.framework.orm.SQLUtils@parseCondition(\""+entityClass.getName()+"\",pv.name,pv.value)}</otherwise></choose>");
            buf.append("</foreach>");
            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                buf.append(" and is_deleted='1'");
            }
            buf.append("</select>");

            return buf.toString();

        }

        private String buildDeleteSQL() {
            if (IVirtualDeleteEntity.class.isAssignableFrom(entityClass)) {
                return "<update>update " + SQLUtils.resolveTableName(entityClass) + " set is_deleted='0' where id=#{id}</update>";
            }
            return "<delete>delete from " + SQLUtils.resolveTableName(entityClass) + " where id=#{id}</delete>";
        }

        private String buildDeleteAllSQL() {
            if (IVirtualDeleteEntity.class.isAssignableFrom(entityClass)) {
                return "<update>update " + SQLUtils.resolveTableName(entityClass) + " set is_deleted='0' where id in(#{id})</update>";
            }
            return "<delete>delete from " + SQLUtils.resolveTableName(entityClass) + " where id in (#{id})</delete>";
        }

        private String buildDeleteByColumnSQL() {

            StringBuilder buf = new StringBuilder();

            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                buf.append("<update>update ").append(SQLUtils.resolveTableName(entityClass)).append(" set is_deleted=1 ").append(" where ");
            }else {
                buf.append("<delete>delete from ").append(SQLUtils.resolveTableName(entityClass)).append(" where ");
            }

            buf.append("<choose><when test=\"pvs!=null\">");
            buf.append("<foreach collection=\"pvs\" item=\"pv\" open=\"\" close=\"\" separator=\" and \">");
            buf.append("<choose><when test=\"pv.value !=null and (pv.value instanceof java.util.List or pv.value.class.array)\">");
            buf.append("${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\"" + entityClass.getName() + "\",pv.name)} in ");
            buf.append("<foreach collection=\"pv.value\" item=\"v\" open=\"(\" close=\")\" separator=\",\">");
            buf.append("#{v}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise> ${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\"" + entityClass.getName() + "\",pv.name)}${@org.erhun.framework.orm.SQLUtils@parseCondition(\"" + entityClass.getName() + "\",pv.name,pv.value)}</otherwise></choose>");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise> ${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\"" + entityClass.getName() + "\",pv.name)}=#{pv.value}</otherwise></choose>");

            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                buf.append("</update>");
            }else {
                buf.append("</delete>");
            }

            return buf.toString();

        }

        private String buildRemoveColumnSQL() {

            StringBuilder buf = new StringBuilder();

            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                buf.append("<update>update " + SQLUtils.resolveTableName(entityClass) + " set is_deleted='0' where ");
            }else {
                buf.append("<delete>");
                buf.append("delete from ").append(SQLUtils.resolveTableName(entityClass)).append(" where ");
            }

            buf.append("<choose><when test=\"pv!=null\">");
            buf.append("<foreach collection=\"pv\" item=\"pv\" open=\"\" close=\"\" separator=\" and \">");
            buf.append("<choose><when test=\"pv.value !=null and (pv.value instanceof java.util.List or pv.value.class.array)\">");
            buf.append("${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\""+entityClass.getName()+"\",pv.name)} in ");
            buf.append("<foreach collection=\"pv.value\" item=\"v\" open=\"(\" close=\")\" separator=\",\">");
            buf.append("#{v}");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise> ${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\""+entityClass.getName()+"\",pv.name)}${@org.erhun.framework.orm.SQLUtils@parseCondition(\""+entityClass.getName()+"\",pv.name,pv.value)}</otherwise></choose>");
            buf.append("</foreach>");
            buf.append("</when>");
            buf.append("<otherwise> ${@org.erhun.framework.orm.SQLUtils@resolveColumnName(\""+entityClass.getName()+"\",pv.name)}=#{pv.value}</otherwise></choose>");

            if(IVirtualDeleteEntity.class.isAssignableFrom(entityClass)){
                buf.append("</update>");
            }else {
                buf.append("</delete>");
            }

            return buf.toString();

        }

        private String parseJoin(AttributeInfo join, StringBuilder buf1, StringBuilder buf3) {

            String joinKey = join.getJoinKey();
            String key = join.getFieldName();

            if(StringUtils.isEmpty(joinKey)) {
                joinKey = "id";
            }

            if(StringUtils.isEmpty(key)) {
                key = "id";
            }

            String columnName = join.getColumnName();

            JoinType joinType = join.getJoinType();

            Class <? extends Serializable> clazz = join.getJoinClass();
            Field joinField = ReflectionUtils.findField(clazz, joinKey);
            String alias = getTableAlias(clazz, join, false);
            if(StringUtils.isEmpty(alias)){
                alias = getTableAlias(clazz, join, true);
                buf3.append(" ").append(joinType.name().toLowerCase()).append(" join ");
                String tableName = SQLUtils.resolveTableName(clazz);
                buf3.append(tableName).append(" ").append(alias).append(" on ").append(alias).append(".").append(key).append("=").append("t.").append(columnName);
                if(StringUtils.isNotBlank(join.getJoinCondition())){
                    buf3.append(" and ").append(join.getJoinCondition());
                }
            }
            if(key.equals(joinKey)) {
                buf1.append("t.").append(columnName).append(",");
            }else{
                buf1.append(alias).append(".").append(SQLUtils.resolveColumnName(entityClass, joinField)).append(" as ").append(columnName).append(",");
            }

            return alias;
        }

        private void addMappedStatement(Configuration configuration, String statement, String sql, SqlCommandType commandType, List <ResultMap> resultMaps, String resultType) {


            if(configuration.hasStatement(statement, false)){
                return;
            }

            XPathParser p = null;

            try{
                p = new XPathParser(sql);
            }catch (Exception e) {
                e.printStackTrace();
            }

            XNode node = null;

            switch(commandType){
                case INSERT:
                    node = p.evalNode("/insert");
                    break;
                case DELETE:
                    node = p.evalNode("/delete");
                    break;
                case UPDATE:
                    node = p.evalNode("/update");
                    break;
                case SELECT:{
                    node = p.evalNode("/select");
                    break;
                }
                default:
                    return;
            }

            XMLScriptBuilder scriptBuilder = new XMLScriptBuilder(configuration, node);
            SqlSource sqlSource = scriptBuilder.parseScriptNode();

            MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statement, sqlSource, commandType);

            if(commandType == SqlCommandType.INSERT) {
                boolean useGeneratedKeys = node.getBooleanAttribute("useGeneratedKeys", configuration.isUseGeneratedKeys());
                if(useGeneratedKeys) {
                    builder.keyGenerator(Jdbc3KeyGenerator.INSTANCE);
                }
                builder.keyProperty(node.getStringAttribute("keyProperty"));
            }

            if(resultMaps != null){
                builder.resultMaps(resultMaps);
            }

            if(resultType != null){
                if(resultMaps == null){
                    resultMaps = new ArrayList <ResultMap>(1);
                }
                ResultMap inlineResultMap = new ResultMap.Builder(
                        configuration,
                        statement + "-Inline",
                        configuration.getTypeAliasRegistry().resolveAlias(resultType),
                        new ArrayList<ResultMapping>(1),
                        null).build();
                resultMaps.add(inlineResultMap);
                builder.resultMaps(resultMaps);
            }

            configuration.addMappedStatement(builder.build());
            tableAlias.clear();;

        }

        private Map <String, Join> findJoinOverrides(AnnotationOverride annotations []) {

            Map <String, Join> joins = null;

            for (AnnotationOverride a : annotations) {
                if(a.join() != null && a.join().length > 0){
                    if(joins == null){
                        joins = new HashMap<String, Join>(3);
                    }
                    joins.put(a.field(), a.join()[0]);
                }
            }

            return joins;
        }

        private String getTableAlias(Class <? extends Serializable> clazz, AttributeInfo attr, boolean generated) {

            String name = attr.getFieldName();

            int i = name.length() - 1;

            for (; i > -1; i--) {
                if (Character.isUpperCase(name.charAt(i))) {
                    break;
                }
            }

            if (i != -1) {
                name = name.substring(0, i);
            }

            name = clazz.getSimpleName() + "$" + name;

            String a = tableAlias.get(name);

            if(generated && StringUtils.isEmpty(a)) {
                a = "t" + tableIndex.incrementAndGet();
                tableAlias.put(name, a);
            }

            return a;

        }

        private boolean validField(Field field){

            boolean valid = validQueryField(field);

            if(!valid) {
                return valid;
            }

            if (field.getAnnotation(Transient.class) != null) {
                return false;
            }

            return true;
        }

        private boolean validQueryField(Field field){

            if(field.getAnnotation(Ignore.class) != null){
                return false;
            }

            if(field.getAnnotation(NestedResultMapping.class) != null){
                return false;
            }

            if (field.getAnnotation(NestedFetch.class) != null) {
                return false;
            }

            if(Modifier.isStatic(field.getModifiers())){
                return false;
            }

            return true;
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {



        StringBuilder buf = new StringBuilder();

        buf.append("<select>update test set is_deleted='0' where 1=1 <if test=\"pv!=null or (pv instanceof String and pv!='')\">and bb=111</if></select>");

        XPathParser p = new XPathParser(buf.toString());

        XNode node = p.evalNode("/select");

        Configuration cfg = new Configuration();

        XMLScriptBuilder scriptBuilder = new XMLScriptBuilder(cfg, node);
        SqlSource sqlSource = scriptBuilder.parseScriptNode();

        Map map = new HashMap();

        map.put("pv", new Integer(0));

        BoundSql s = sqlSource.getBoundSql(map);

        System.out.println(s.getSql());
    }

}
