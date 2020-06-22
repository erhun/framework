package org.erhun.framework.basic.utils;

import java.util.List;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2018/10/10
 */
public class PageResultImpl<E> implements PageResult {

    private long totalRecords;

    private int pageSize = 0;

    private List<E> data;

    public PageResultImpl(long totalRecords, int pageSize, List<E> data) {
        this.totalRecords = totalRecords;
        this.pageSize = pageSize;
        this.data = data;
    }

    @Override
    public long getTotalRecords() {
        return totalRecords;
    }

    @Override
    public boolean hasNextPage() {
        return data != null && data.size() == pageSize;
    }

    @Override
    public List <E> getData() {
        return data;
    }

}
