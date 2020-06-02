package org.erhun.framework.orm;

import org.erhun.framework.orm.annotations.JoinType;

import java.lang.reflect.Field;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019/10/25
 */
public class AttributeInfo {
    private Field field;
    private String fieldName;
    private String columnName;
    private boolean primaryKey;
    private boolean autoIncrement;
    private boolean isTransient;
    private boolean creatable = true;
    private boolean updatable = true;
    private boolean queryable = true;
    private String nestedResultMapColumn;
    private String nestedResultMapId;
    private String item;

    private Class joinClass;
    private JoinType joinType;
    private String joinKey;
    private String joinCondition;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        primaryKey = primaryKey;
    }

    public boolean isCreatable() {
        return creatable;
    }

    public void setCreatable(boolean creatable) {
        this.creatable = creatable;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public String getNestedResultMapColumn() {
        return nestedResultMapColumn;
    }

    public void setNestedResultMapColumn(String nestedResultMapColumn) {
        this.nestedResultMapColumn = nestedResultMapColumn;
    }

    public String getNestedResultMapId() {
        return nestedResultMapId;
    }

    public void setNestedResultMapId(String nestedResultMapId) {
        this.nestedResultMapId = nestedResultMapId;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean aTransient) {
        isTransient = aTransient;
    }

    public boolean isQueryable() {
        return queryable;
    }

    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Class getJoinClass() {
        return joinClass;
    }

    public void setJoinClass(Class joinClass) {
        this.joinClass = joinClass;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public String getJoinCondition() {
        return joinCondition;
    }

    public void setJoinCondition(String joinCondition) {
        this.joinCondition = joinCondition;
    }

    public String getJoinKey() {
        return joinKey;
    }

    public void setJoinKey(String joinKey) {
        this.joinKey = joinKey;
    }
}
