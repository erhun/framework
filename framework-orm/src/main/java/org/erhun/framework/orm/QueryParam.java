package org.erhun.framework.orm;


import org.erhun.framework.basic.utils.reflection.ReflectionUtils;
import org.erhun.framework.orm.annotations.Ignore;
import org.erhun.framework.orm.dto.BaseDTO;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2018/7/26
 */
public class QueryParam <E extends BaseDTO> extends HashMap <String, Object> {

    private Class <E> entityClass;

    private E model;

    private Integer pageNo;

    private Integer pageSize;

    private static final HashMap <Class, Map<String, Class>> types = new HashMap<Class, Map<String, Class>>();

    public QueryParam(){
    }

    public QueryParam(final E model){
        this.model = model;
        this.entityClass = (Class<E>) model.getClass();
        this.disassembly();
    }

    @Override
    public Object put(String key, Object value) {

        if(value == null){
            return null;
        }

        if("pageNo".equals(key)){
            if(value instanceof String){
                pageNo = Integer.valueOf((String)value);
            }else{
                pageNo = (Integer)value;
            }
        }else if("pageSize".equals(key)){
            if(value instanceof String){
                pageSize = Integer.valueOf((String)value);
            }else{
                pageSize = (Integer)value;
            }
        }

        if(model != null){
            Field field = ReflectionUtils.findField(entityClass, key);
            if(field != null && !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                ReflectionUtils.set(model, field, ReflectionUtils.convertToBasicValue(field.getType(), value));
            }
        }
        return super.put(key, value);
    }

    @Override
    public Object get(Object key) {
        Object value = super.get(key);
        if(value == null && model != null){
            return ReflectionUtils.getValue(model, (String) key);
        }
        return value;
    }

    public E param() {
        return model;
    }

    public void param(E model) {
        this.model = model;
        this.entityClass = (Class<E>) model.getClass();
        this.disassembly();
    }

    public Map <String, Class> getFieldTypes() {
        return types.get(entityClass);
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    private void disassembly(){
        Map<String, Class> fieldTypes = types.get(entityClass);
        if(fieldTypes == null) {
            fieldTypes = new HashMap<>();
            ReflectionUtils.doWithFields(entityClass, new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    if (!Modifier.isStatic(field.getModifiers())
                            && !Modifier.isFinal(field.getModifiers())
                            && field.getAnnotation(Ignore.class) == null
                            && !field.getName().startsWith("CGLIB")) {
                        field.setAccessible(true);
                        put(field.getName(), field.get(model));
                    }
                }
            });
            types.put(entityClass, fieldTypes);
        }
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

}
