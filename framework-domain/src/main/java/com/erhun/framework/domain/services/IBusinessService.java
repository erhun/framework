package com.erhun.framework.domain.services;

import com.erhun.framework.basic.utils.PV;
import com.erhun.framework.basic.utils.ResultPack;
import com.erhun.framework.orm.Criteria;
import com.erhun.framework.orm.QueryParam;
import com.erhun.framework.domain.entities.BaseEntity;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author weichao <gorilla@aliyun.com> 
 * @param <E>
 */
@Service
public interface IBusinessService <Id extends Serializable, E extends BaseEntity<Id>> {

    /**
     * @param id
     * @return
     */
    E get(Id id);

    /**
     * @param entity
     * @return
     * @throws BusinessException
     */
    E save(E entity) throws BusinessException;

    /**
     * @param entity
     * @return
     * @throws BusinessException
     */
    E add(E entity) throws BusinessException;

    /**
     * @param entities
     * @throws BusinessException
     */
    void add(List<E> entities) throws BusinessException;

    /**
     * @param entity
     * @return
     * @throws BusinessException
     */
    E update(E entity) throws BusinessException;

    /**
     * @param entity
     * @param affects
     * @return
     * @throws BusinessException
     */
    E update(E entity, String... affects) throws BusinessException;

    /**
     * @param id
     * @return
     * @throws BusinessException
     */
    boolean delete(Id id) throws BusinessException;

    /**
     * @param pvs
     * @return
     */
    E find(PV...pvs);

    /**
     * @param criteria
     * @return
     */
    E find(Criteria criteria);

    /**
     * @param idList
     * @return
     */
    List <E> queryById(String idList);

    /**
     * @param queryParam
     * @return
     */
    ResultPack queryByPage(QueryParam queryParam);

    /**
     * @param queryParam
     * @param fetchFields
     * @return
     */
    ResultPack queryByPage(QueryParam queryParam, String[] fetchFields);

    /**
     * @param <T>
     * @param queryParam
     * @return
     */
    <T> List <T> queryMetadata(QueryParam queryParam);

    /**
     * @param <T>
     * @param queryWrapper
     * @param fetchFields
     * @return
     */
    <T> List <T> queryMetadata(QueryParam queryWrapper, String[] fetchFields);

    /**
     * @return
     */
    List<E> queryAll();

    /**
     *
     */
    void finalize();

    /**
     * @param ctx
     */
    void setBusinessContext(BusinessContext ctx);

    /**
     * @return
     */
    BusinessContext getBusinessContext();

}
