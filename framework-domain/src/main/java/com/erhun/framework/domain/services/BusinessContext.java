package com.erhun.framework.domain.services;

import org.springframework.stereotype.Service;

/**
 * 
 * @author weichao (gorilla@aliyun.com)
 */
@Service
public interface BusinessContext {

    String CONTEXT = "BUSINESS_CONTEXT";

    /**
     * @param name
     * @param <T>
     * @return
     */
    <T> T getRequestAttribute(String name);

    /**
     * @param key
     * @param value
     */
    void put(String key, Object value);

    /**
     * @param key
     * @param <T>
     * @return
     */
    <T> T getParam(String key);

    /**
     * @param key
     * @return
     */
    Integer getIntegerParam(String key);

    /**
     * @param key
     * @return
     */
    Integer getIntegerParam(String key, Integer defaultValue);

    /**
     * @param key
     * @return
     */
    Integer getLongParam(String key);

    /**
     * @param key
     * @return
     */
    Integer getLongParam(String key, Integer defaultValue);


    /**
     *
     * @param name
     * @return
     */
    String getRequestHeader(String name);

    /**
     * @return
     */
    String getClientIP();

}
