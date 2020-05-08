package com.erhun.framework.orm.entities;

import java.io.Serializable;

/**
 *
 * 版本实体接口
 * @author weichao<gorilla@gliyun.com>
 *
 */
public interface IVersionEntity<Id>{

    /**
     * @return
     */
    Long getVersion();

    /**
     * @param version
     */
    void setVersion(Long version);

}
