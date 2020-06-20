package org.erhun.framework.domain.dao;

import org.erhun.framework.domain.entities.BaseEntity;
import org.erhun.framework.orm.IGenericDAO;

import java.io.Serializable;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 * @param <Id>
 * @param <E>
 */
public interface BaseDAO<Id extends Serializable, E extends BaseEntity> extends IGenericDAO<Id, E> {


}
