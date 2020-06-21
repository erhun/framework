package org.erhun.framework.basic.utils;

import java.util.List;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2018/10/10
 */
public class PageResultImpl<E> implements PageResult {

    private long totalRecords;

    private List<E> data;

    public PageResultImpl(long totalRecords, List<E> data) {
        this.totalRecords = totalRecords;
        this.data = data;
    }

    @Override
    public long getTotalRecords() {
        return totalRecords;
    }

    @Override
    public List <E> getData() {
        return data;
    }

}
