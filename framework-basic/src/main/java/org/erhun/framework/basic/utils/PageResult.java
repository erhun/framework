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
     * 是否包含下一页(用于下一页)
     * @return
     */
    boolean hasNextPage();

    /**
     * 数据记录
     * @return
     */
    List <E> getData();


}
