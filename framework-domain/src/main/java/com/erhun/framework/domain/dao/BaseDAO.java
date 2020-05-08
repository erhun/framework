package com.erhun.framework.domain.dao;

import com.erhun.framework.domain.entities.BaseEntity;
import com.erhun.framework.orm.IGenericDAO;

import java.io.Serializable;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 * @param <Id>
 * @param <E>
 */
public interface BaseDAO<Id extends Serializable, E extends BaseEntity> extends IGenericDAO<Id, E> {


}
