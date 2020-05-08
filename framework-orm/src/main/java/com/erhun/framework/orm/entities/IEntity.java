package com.erhun.framework.orm.entities;

import java.io.Serializable;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
public interface IEntity<Id> extends Serializable {

    /**
     * @return
     */
    Id getId();

    /**
     * @param id
     */
    void setId(Id id);

}
