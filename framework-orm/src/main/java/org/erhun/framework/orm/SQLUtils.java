package org.erhun.framework.orm;


import org.erhun.framework.basic.utils.ClassUtils;
import org.erhun.framework.basic.utils.lambda.GetterLambdaFunction;
import org.erhun.framework.basic.utils.lambda.LambdaUtils;
import org.erhun.framework.basic.utils.number.NumberUtils;
import org.erhun.framework.basic.utils.reflection.ReflectionUtils;
import org.erhun.framework.basic.utils.string.StringPool;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.orm.dialect.Dialect;
import org.erhun.framework.orm.exception.ParsedException;
import org.erhun.framework.orm.namegenerator.TableNameGenerator;
import org.erhun.framework.orm.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author weichao<gorilla@aliyun.com>
 */
public class SQLUtils {

    private static final Logger logger = LoggerFactory.getLogger(SQLUtils.class);

    public static final String MAIN_ENTITY_ALIAS = "t";

    private static final AtomicLong UID_GENERATOR = new AtomicLong(System.currentTimeMillis());

    public static final Map <String, TreeMap <String, AttributeDefinition>> attributeDefinition = new HashMap();

    public static final Map <GetterLambdaFunction, AttributeDefinition> lamdbaAttributeDefinition = new HashMap();

    public static long nextUID() {
        return UID_GENERATOR.getAndIncrement();
    }

    private static Dialect dialect;

    public static void main(String[] args) {
        System.out.println(nextUID());
    }

    /**
     * Resolve table name
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> String resolveTableName(Class<T> clazz) {

        Table table = clazz.getAnnotation(Table.class);

        if (table == null) {
            throw new ParsedException("Entity [" + clazz + "] has no define Table annotation.");
        }

        String tableName = table.value();

        if (StringUtils.isEmpty(tableName)) {
            throw new ParsedException("Entity [" + clazz + "] has no tablename defined.");
        }

        Class<?> ng = table.nameGenerator();

        if (ng != Class.class) {
            try {
                TableNameGenerator gen = (TableNameGenerator) ng.newInstance();
                tableName = gen.generate(tableName);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return tableName;
    }

    public static <T> String resolveColumnName(GetterLambdaFunction function) {

        AttributeDefinition attributeInfo = lamdbaAttributeDefinition.get(function);

        if(attributeInfo == null){
            SerializedLambda lambda = LambdaUtils.getSerializedLambda(function);
            Class clazz = ClassUtils.getClass(lambda.getImplClass().replace("/", "."));
            Field field = ReflectionUtils.findField(clazz, ReflectionUtils.getPropertyNameByMethodName(lambda.getImplMethodName()));
            attributeInfo =  resolveAttributeInfo(clazz, field);
            lamdbaAttributeDefinition.put(function, attributeInfo);
        }

        if(attributeInfo != null){
            return attributeInfo.getColumnName();
        }

        return null;

    }

    /**
     * Resolve column name
     * @param <T>
     * @Param entityClass
     * @param field
     * @return
     */
    public static <T> String resolveColumnName(Class entityClass, Field field) {
        AttributeDefinition attributeInfo = resolveAttributeInfo(entityClass, field);
        if(attributeInfo != null){
            return attributeInfo.getColumnName();
        }
        return null;
    }

    /**
     *
     * @param entityClass
     * @return
     */
    public static Collection <AttributeDefinition> getAttributeDefinition(Class entityClass) {

        TreeMap <String, AttributeDefinition> attributes = attributeDefinition.get(entityClass.getName());

        if(attributes == null){
            attributes = initAttributeDefinition(entityClass);
        }

        return attributes.values();

    }

    private static TreeMap<String, AttributeDefinition> initAttributeDefinition(Class entityClass) {

        TreeMap<String, AttributeDefinition> attributes = new TreeMap<>();

        Field[] entityFields = ClassUtils.getDeclaredFields(entityClass);

        boolean autoIncrement = false;

        if(entityClass.getAnnotation(AutoIncrement.class) != null) {
            autoIncrement = true;
        }

        for (Field field : entityFields) {

            if (field.getAnnotation(Ignore.class) != null || Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            AttributeDefinition attributeDefinition = new AttributeDefinition();
            String columnName = null;

            AttrDef attributeDef = field.getAnnotation(AttrDef.class);

            if (attributeDef != null) {
                if (StringUtils.isNotEmpty(attributeDef.value())) {
                    columnName = attributeDef.value();
                }
                attributeDefinition.setCreatable(attributeDef.creatable());
                attributeDefinition.setUpdatable(attributeDef.updatable());
                if(attributeDef.typeHandler() != Object.class) {
                    attributeDefinition.setTypeHandler(attributeDef.typeHandler());
                }
            }


            Column column = field.getAnnotation(Column.class);

            if (column != null && StringUtils.isNotEmpty(column.name())) {
                columnName = column.name();
            }

            if (StringUtils.isEmpty(columnName)) {
                columnName = convPropertyNameToColumnName(field);
            }

            if(field.getName().equalsIgnoreCase("id")){
                attributeDefinition.setPrimaryKey(true);
                attributeDefinition.setAutoIncrement(autoIncrement);
                attributeDefinition.setUpdatable(false);
                if(autoIncrement){
                    attributeDefinition.setCreatable(false);
                }
            }

            if(field.getAnnotation(Transient.class) != null) {
                attributeDefinition.setTransient(field.getAnnotation(Transient.class) != null);
                attributeDefinition.setCreatable(false);
                attributeDefinition.setUpdatable(false);
                attributeDefinition.setQueryable(false);
            }

            if(field.getAnnotation(AutoIncrement.class) != null) {
                attributeDefinition.setAutoIncrement(true);
                attributeDefinition.setCreatable(false);
                attributeDefinition.setUpdatable(false);
            }

            if(field.getAnnotation(Join.class) != null) {
                Join join = field.getAnnotation(Join.class);
                attributeDefinition.setJoinKey(join.value());
                attributeDefinition.setJoinClass(join.clazz());
                attributeDefinition.setJoinType(join.type());
                attributeDefinition.setJoinCondition(join.condition());
            }

            attributeDefinition.setField(field);
            attributeDefinition.setFieldName(field.getName());
            attributeDefinition.setColumnName(columnName);

            NestedResultMapping nestedResultMapping = field.getAnnotation(NestedResultMapping.class);

            if (nestedResultMapping != null) {
                attributeDefinition.setNestedResultMapId(nestedResultMapping.value());
                attributeDefinition.setNestedResultMapColumn(resolveColumnName(entityClass, nestedResultMapping.column()));
                attributeDefinition.setCreatable(false);
                attributeDefinition.setUpdatable(false);
                attributeDefinition.setQueryable(false);
            }

            attributes.put(field.getName(), attributeDefinition);

        }

        attributeDefinition.put(entityClass.getName(), attributes);

        return attributes;

    }

    /**
     * Resolve column name
     * @param <T>
     * @Param entityClass
     * @param field
     * @return
     */
    private static <T> AttributeDefinition resolveAttributeInfo(Class entityClass, Field field) {

        if(field == null){
            return null;
        }

        TreeMap <String, AttributeDefinition> attributes = attributeDefinition.get(entityClass.getName());

        if(attributes != null){
            AttributeDefinition attributeInfo = attributes.get(field.getName());
            if(attributeInfo != null){
                return attributeInfo;
            }
        }else{
            attributes = initAttributeDefinition(entityClass);
        }

        return attributes.get(field.getName());
    }

    /**
     * Resolve column name
     * @param fieldName
     * @return
     */
    public static String resolveColumnName(Class clazz, String fieldName) {

        StringBuilder buf = new StringBuilder();

        String names [] = fieldName.split(",");

        for (String tmp : names) {
            if(buf.length() > 0){
                buf.append(",");
            }
            Field field = ReflectionUtils.findField(clazz, tmp);
            if(field == null){
                buf.append(tmp);
                continue;
            }
            String columnName = resolveColumnName(clazz, field);
            buf.append(columnName);
        }

        return buf.toString();

    }

    /**
     * Resolve column name
     * @param className
     * @param fieldName
     * @return
     */
    public static String resolveColumnName(String className, String fieldName) {
        Class clazz = ClassUtils.getClass(className);
        Field field = ReflectionUtils.findField(clazz, fieldName);
        if(field == null){
            return fieldName;
        }
        return resolveColumnName(clazz, field);
    }

    /**
     * Convert column name to property name
     *
     * @param columnName
     * @return
     */
    public static String convColumnNameToPropertyName(String columnName) {

        assert columnName != null : "'fieldName' cannot be null.";

        StringBuilder sb = new StringBuilder();
        String[] segs = columnName.split("_");
        //没有下划线 全部转小写
        if (segs.length == 1) {
            return segs[0].toLowerCase();
        }
        //否则按下划线规则处理
        for (String seg : segs) {
            sb.append(StringUtils.capitalize(seg));
        }
        return StringUtils.uncapitalize(sb.toString());
    }

    /**
     * Convert property name to column name
     *
     * @return
     */
    public static String convPropertyNameToColumnName(Field field) {

        assert field != null : "'propName' cannot be null.";

        String propName = field.getName();

        StringBuilder buf = new StringBuilder();

        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            buf.append("is_");
        }

        String alias = "";
        int p = propName.indexOf(".");
        if (p > 0) {
            alias = propName.substring(0, p);
            propName = propName.substring(p + 1);
        }
        int pos = 0;
        for (int i = 0; i < propName.length(); i++) {
            if (Character.isUpperCase(propName.charAt(i))) {
                buf.append(StringUtils.uncapitalize(propName.substring(pos, i)));
                buf.append('_');
                pos = i;
            }
        }
        String columnName = buf.append(propName.substring(pos)).toString().toLowerCase();
        if (alias.length() > 0) {
            columnName = alias + "." + columnName;
        }

        return columnName;
    }

    public static String parseCondition(String className, String fieldName, Object value){
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.warn(e.getMessage(), e);
        }
        return parseCondition(clazz, fieldName, value);
    }

    public static String parseCondition(Class clazz, String fieldName, Object value){

        if (value == null) {
            return "=null";
        }

        Class type = String.class;

        if(fieldName.indexOf("_") > -1){
            type = value.getClass();
        }else {
            Field field = ReflectionUtils.findField(clazz, fieldName);
            type = field.getType();
            if ("id".equals(field.getName())) {
                if (value instanceof String) {
                    return "='" + value + "'";
                } else {
                    return "=" + value;
                }
            }
        }

        return parseCondition(type, value);

    }

    public static String parseCondition(Class type, Object val) {

        if (val == null) {
            return "=null";
        }

        String value = val.toString();

        if (StringUtils.isBlank(value) || type == null) {
            return StringPool.EMPTY;
        }

        String keyword = value = value.trim();

        SQLExpression expression = parseExpression(keyword, value);

        return parseCondition(type, expression, value);

    }

    public static String parseCondition(Class type, SQLExpression expression, String value) {

        value = trimSymbol(value);

        StringBuilder buffer = new StringBuilder();

        if (type == String.class) {
            parseStringValue(buffer, expression, value);
        } else if (Number.class.isAssignableFrom(type)) {
            parseNumberQueryValue(buffer, expression, value);
        } else if (Timestamp.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type) || Instant.class.isAssignableFrom(type)) {
            parseDateQueryValue(buffer, expression, value, type);
        }

        return buffer.toString();
    }

    /**
     *
     * @param key
     * @param value
     */
    public static SQLExpression parseExpression(String key, String value) {

        if (key.startsWith("*") && key.endsWith("*") && value.length() > 0) {
            return SQLExpression.LIKE;
        }

        if (key.equals("**")) {
            return SQLExpression.IS_NOT_NULL;
        }

        if (key.startsWith("*")) {
            return SQLExpression.START_LIKE;
        }

        if (key.startsWith("!*") && value.length() > 0) {
            return SQLExpression.START_NOT_LIKE;
        }

        if (key.startsWith("!") && key.endsWith("*") && value.length() > 0) {
            return SQLExpression.END_NOT_LIKE;
        }

        if (key.equals("!*")) {
            return SQLExpression.IS_NULL;
        }

        if (key.endsWith("*")) {
            return SQLExpression.END_LIKE;
        }

        if (key.startsWith("!")) {
            if (key.length() > 1) {
                if (key.indexOf(",") > -1) {
                    return SQLExpression.NOT_IN;
                }
                return SQLExpression.NOT_EQUAL;
            } else {
                return SQLExpression.IS_NOT_NULL;
            }
        }

        if (key.startsWith("<=")) {
            return SQLExpression.LESS_EQUAL;
        }
        if (key.startsWith("<")) {
            return SQLExpression.LESS;
        }
        if (key.startsWith(">=")) {
            return SQLExpression.GREET_EQUAL;
        }
        if (key.startsWith(">")) {
            return SQLExpression.GREET;
        }
        if (key.startsWith("=")) {
            return SQLExpression.EQUAL;
        }

        int idx = value.indexOf(",");

        if (idx == -1) {
            idx = value.indexOf("~");
            if (idx > -1) {
                return SQLExpression.BETEEN;
            } else {
                return SQLExpression.EQUAL;
            }
        } else if (idx == 0) {
            return SQLExpression.LESS;
        } else if (idx == value.length() - 1) {
            return SQLExpression.GREET;
        } else { // idx > 0
            return SQLExpression.IN;
        }

    }

    /**
     *
     * @param condition
     * @param value
     * @param type
     * @return
     */
    private static String parseDateQueryValue(StringBuilder condition, SQLExpression expression, String value, Class type) {

        String queryDates[][] = parseDateValue(value, type);

        String dateValue1 = queryDates[0][0];

        switch (expression) {
            case EQUAL: {
                condition.append("=").append(dialect.convertDate(dateValue1));
                break;
            }
            case NOT_EQUAL: {
                condition.append("<>").append(dialect.convertDate(dateValue1));
                break;
            }
            case IS_NULL: {
                condition.append(" is null ");
                break;
            }
            case IS_NOT_NULL: {
                condition.append(" is not null ");
                break;
            }
            case LESS: {
                condition.append("<").append(dialect.convertDate(dateValue1));
                break;
            }
            case LESS_EQUAL: {
                condition.append("<=").append(dialect.convertDate(dateValue1));
                break;
            }
            case GREET: {
                condition.append(">").append(dialect.convertDate(dateValue1));
                break;
            }
            case GREET_EQUAL: {
                condition.append(">=").append(dialect.convertDate(dateValue1));
                break;
            }
            case IN: {
                condition.append(" in (");
                for (int i = 0; i < queryDates.length; i++) {
                    String ar[] = queryDates[i];
                    if (i > 0) {
                        condition.append(", ");
                    }
                    if (StringUtils.isEmpty(ar[1])) {
                        condition.append("").append(dialect.convertDate(ar[0]));
                    } else {
                        condition.append(" between ").append(dialect.convertDate(ar[0])).append(" and ").append(dialect.convertDate(ar[1]));
                    }
                }
                condition.append(")");
                break;
            }
            case BETEEN: {
                String[] date1 = queryDates[0];
                if (StringUtils.isNotEmpty(date1[1])) {
                    condition.append(" between ").append(dialect.convertDate(date1[0])).append(" and ").append(dialect.convertDate(date1[1]));
                } else {
                    condition.append("=").append(dialect.convertDate(date1[0]));
                }
                break;
            }
            default: {
                break;
            }
        }
        return StringPool.EMPTY;

    }

    private static String [][] parseDateValue(String value, Class type){

        String ar1 [] = value.split("~|,");

        if(value.indexOf("~")>-1){
            String a1 [] = parseDateValue2(ar1[0], type);
            String a2 [] = parseDateValue2(ar1[1], type);
            return new String[][]{new String[]{a1[0], StringUtils.isNotEmpty(a2[1])?a2[1]:a2[0]}};
        }else{
            String rs [][] = new String [ar1.length][];
            for( int i=0;i<ar1.length;i++){
                rs[i] = parseDateValue2(ar1[i], type);
            }
            return rs;
        }

    }

    private static String [] parseDateValue2(String startDate, Class type){

        StringBuilder dateValue1 = new StringBuilder();
        StringBuilder dateValue2 = new StringBuilder();

        String ar [] = startDate.split("-");

        if( ar.length > 3 ){
            return new String[0];
        }

        boolean queryMonth = false;

        if(!NumberUtils.isDigits(ar[0])){
            if(ar[0].charAt(0) == 'm' || ar[0].charAt(0) == 'M'){
                queryMonth = true;
                ar[0] = ar[0].substring(1);
                if(!NumberUtils.isDigits(ar[0])){
                    return new String[0];
                }
            }else{
                return new String[0];
            }
        }

        if(ar.length == 2 && !NumberUtils.isDigits(ar[1])){
            return new String[0];
        }

        if(ar.length == 3 && !NumberUtils.isDigits(ar[2])){
            return new String[0];
        }

        Calendar cal = Calendar.getInstance();

        if(ar.length == 1){

            Integer v = Integer.parseInt(ar[0]);

            if(queryMonth){
                dateValue1.append(cal.get(Calendar.YEAR));
                dateValue1.append("-");
                dateValue1.append(StringUtils.leftPad(String.valueOf(v), 2, '0'));
                dateValue1.append("-");
                dateValue1.append("01");
                dateValue2.append(cal.get(Calendar.YEAR));
                dateValue2.append("-");
                dateValue2.append(StringUtils.leftPad(String.valueOf(v), 2, '0'));
                dateValue2.append("-");
                dateValue2.append(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            }else if( v <= 31 ){
                dateValue1.append(cal.get(Calendar.YEAR));
                dateValue1.append("-");
                dateValue1.append(StringUtils.leftPad(String.valueOf(cal.get(Calendar.MONTH)+1), 2, '0'));
                dateValue1.append("-");
                dateValue1.append(StringUtils.leftPad(String.valueOf(v), 2, '0'));
            }else{

                String nowYear = String.valueOf(cal.get(Calendar.YEAR));

                String year = startDate;

                if(startDate.length() < nowYear.length()){
                    year = nowYear.substring(0, startDate.length() ) + startDate;
                }

                dateValue1.append(year).append("-01-01");

                /*if (!isBetween) {
                    dateValue2.append(endDate).append("-12-31");
                } else {*/
                    dateValue2.append(year).append("-12-31");
                //}

                if (type == Timestamp.class) {
                    dateValue2.append(" 23:59:59");
                }

            }
        }else if(ar.length == 2){

            Integer tmp = Integer.valueOf(ar[0]);

            if(tmp < 13){
                dateValue1.append(cal.get(Calendar.YEAR));
                dateValue1.append("-");
                dateValue1.append(StringUtils.leftPad(ar[0], 2, '0'));
                dateValue1.append("-");
                dateValue1.append(StringUtils.leftPad(ar[1], 2, '0'));
            }else{

                String year = String.valueOf(cal.get(Calendar.YEAR));

                if(ar[0].length() < year.length()){
                    year = year.substring(0, ar[0].length() ) + ar[0];
                }

                if(tmp > 31){
                    year = ar[0];
                }

                dateValue1.append(year);
                dateValue1.append("-");
                dateValue1.append(StringUtils.leftPad(ar[1], 2, '0'));
                dateValue1.append("-01");

                cal.set(Calendar.YEAR, Integer.valueOf(year));
                cal.set(Calendar.MONTH, Integer.valueOf(ar[1])-1);

                dateValue2.append(year);
                dateValue2.append("-");
                dateValue2.append(StringUtils.leftPad(ar[1], 2, '0'));
                dateValue2.append("-");
                dateValue2.append(cal.getActualMaximum(Calendar.DAY_OF_MONTH));

            }

        }else{
            return new String[]{startDate, ""};
        }

        return new String[]{dateValue1.toString(), dateValue2.toString()};
    }

    /**
     *
     * @param condition
     * @param expression
     * @param value
     * @return
     */
    private static String parseNumberQueryValue(StringBuilder condition, SQLExpression expression, String value) {

        switch (expression) {
            case IS_NOT_NULL: {
                condition.append(" is not null ");
                break;
            }
            case NOT_EQUAL: {
                condition.append("<>").append(value);
                break;
            }
            case LESS: {
                condition.append("<").append(value);
                break;
            }
            case LESS_EQUAL: {
                condition.append("<=").append(value);
                break;
            }
            case GREET: {
                condition.append(">").append(value);
                break;
            }
            case GREET_EQUAL: {
                condition.append(">=").append(value);
                break;
            }
            case IN: {
                condition.append(" in (").append(value).append(")");
                break;
            }
            case BETEEN: {
                condition.append(" between ").append(value.replaceFirst("~", " and "));
                break;
            }
            default: {
                condition.append("=").append(value);
            }
        }

        return StringPool.EMPTY;

    }
    /**
     *
     * @param condition
     * @param expression
     * @param value
     */
    public static void parseStringValue(StringBuilder condition, SQLExpression expression, String value){

        switch (expression) {
            case EQUAL: {
                condition.append("='").append(value).append("'");
                break;
            }
            case NOT_EQUAL: {
                condition.append("<>'").append(value).append("'");
                break;
            }
            case LESS: {
                condition.append("<'").append(value).append("'");
                break;
            }
            case LESS_EQUAL: {
                condition.append("<='").append(value).append("'");
                break;
            }
            case GREET: {
                condition.append(">'").append(value).append("'");
                break;
            }
            case GREET_EQUAL: {
                condition.append(">='").append(value).append("'");
                break;
            }
            case IS_NULL: {
                condition.append(" is null ");
                break;
            }
            case IS_NOT_NULL: {
                condition.append(" is not null ");
                break;
            }
            case LIKE: {
                condition.append(" like '%").append(value).append("%'");
                break;
            }
            case START_LIKE: {
                condition.append(" like '%").append(value).append("'");
                break;
            }
            case END_LIKE: {
                condition.append(" like '").append(value).append("%'");
                break;
            }
            case NOT_LIKE: {
                condition.append(" not like '%").append(value).append("%'");
                break;
            }
            case START_NOT_LIKE: {
                condition.append(" not like '%").append(value).append("'");
                break;
            }
            case END_NOT_LIKE: {
                condition.append(" not like '").append(value).append("%'");
                break;
            }
            case BETEEN: {
                condition.append(" between '").append(value.replaceFirst("~", "' and '")).append("'");
                break;
            }
            case IN: {
                condition.append(" in ").append("(").append(StringUtils.singleQuotes(value)).append(")");
                break;
            }
            default: {
                condition.append("='").append(value).append("'");
            }
        }

    }

    public static String resolveArgs(Object val, Object val2){


        return "";
    }

    private static String trimSymbol( String value ){
        return value.replaceAll( "\\*|!|>|<|&gt;|&lt;|=|\\$|\\{|\\}" , "" ).replace("'", "''");
    }


    public static Dialect getDialect() {
        return dialect;
    }

    static void setDialect(Dialect dialect) {
        SQLUtils.dialect = dialect;
    }
}
