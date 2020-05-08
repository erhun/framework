package com.erhun.framework.orm.entities;

import java.io.Serializable;

/**
 *
 * 分段ID实体接口
 * @author weichao<gorilla@gliyun.com>
 *
 */
public interface ISectionIdEntity<Id> extends Serializable {

    /**
     * @return
     */
    Long getSectionIdentity();

}
