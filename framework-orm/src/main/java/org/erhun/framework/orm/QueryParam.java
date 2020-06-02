package org.erhun.framework.orm;


import java.util.HashMap;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2018/7/26
 */
public class QueryParam extends HashMap <String, Object> {

    /**
     * 1 开始
     */
    private Integer pageNo;

    private Integer pageSize;

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

        return super.put(key, value);
    }

    @Override
    public Object get(Object key) {
        Object value = super.get(key);
        return value;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
