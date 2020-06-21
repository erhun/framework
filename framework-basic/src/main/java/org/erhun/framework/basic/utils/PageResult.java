package org.erhun.framework.basic.utils;

import java.util.List;

/**
 * @Author weichao <gorilla@aliyun.com>
 */
public interface PageResult<E>{


    /**
     * 总记录数
     * @return
     */
    long getTotalRecords();

    /**
     * 数据记录
     * @return
     */
    List <E> getData();


}
