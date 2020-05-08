package com.erhun.framework.basic.utils;

import java.util.List;

/**
 * @Author weichao <gorilla@aliyun.com>
 */
public interface PageResult<E>{


    /**
     * 最大记录数
     * @return
     */
    long getMaxRecords();

    /**
     * 数据记录
     * @return
     */
    List <E> getData();


}
