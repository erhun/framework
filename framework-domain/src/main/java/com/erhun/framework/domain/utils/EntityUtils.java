package com.erhun.framework.domain.utils;

import com.erhun.framework.domain.entities.BaseEntity;

import java.util.AbstractList;
import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
public class EntityUtils {

    
    public static String singleQuotes(List <BaseEntity<?>> list){
        
        StringBuilder buf = new StringBuilder();
        
        for (BaseEntity <?> s : list) {
            
            if(s.getId() instanceof String) {
                buf.append("'").append(s.getId()).append("'").append(",");
            }else {
                buf.append(s.getId()).append(",");
            }
        }
        
        if(buf.length() > 0){
            buf.setLength(buf.length() - 1);
        }
        
        return buf.toString();
        
    }

    public static String toIdString(List <BaseEntity<?>> list){

        StringBuilder buf = new StringBuilder();

        for (BaseEntity <?> s : list) {
            buf.append(s.getId()).append(",");
        }

        if(buf.length() > 0){
            buf.setLength(buf.length() - 1);
        }

        return buf.toString();

    }
    
    public static <T, E extends BaseEntity<T>> List <T> idList(final List <E> list){
        
        return new AbstractList <T> () {
            
            @Override
            public T get(int index) {
                return (T) list.get(index).getId();
            }

            @Override
            public int size() {
                return list.size();
            }
            
        };
        
    }
}
