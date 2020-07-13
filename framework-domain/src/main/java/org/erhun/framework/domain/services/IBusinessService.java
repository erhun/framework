package org.erhun.framework.domain.services;

import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.basic.utils.PageResult;
import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.domain.entities.BaseEntity;
import org.erhun.framework.orm.Criteria;
import org.erhun.framework.orm.Limits;
import org.erhun.framework.orm.QueryParam;
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
     *
     * @param idList
     * @return
     * @throws BusinessException
     */
    boolean deleteAll(String idList) throws BusinessException;

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
     *
     * @param criteria
     * @return
     */
    List <E> queryByCriteria(Criteria criteria);

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
     * 根据queryParam查询记录并返回指定列，不执行统计条类语句
     * @param entity
     * @param fetchFields
     * @param limit
     * @return
     */
    PageResult<E> queryByNextPage(QueryParam entity, String fetchFields[], Limits limit);

    /**
     * @param queryParam
     * @param fetchFields
     * @return
     */
    ResultPack queryByPage(QueryParam queryParam, String[] fetchFields, Criteria criteria);

    /**
     * 根据queryParam查询记录并返回指定列，不执行统计条类语句
     * @param entity
     * @param fetchColumns
     * @param criteria
     * @param limit
     * @return
     */
    PageResult <E> queryByNextPage(QueryParam entity, String fetchColumns[], Criteria criteria, Limits limit);

    /**
     * @param <T>
     * @param queryParam
     * @return
     */
    <T> List <T> queryMetadata(QueryParam queryParam);

    /**
     * @param <T>
     * @param queryParam
     * @param fetchFields
     * @return
     */
    <T> List <T> queryMetadata(QueryParam queryParam, String[] fetchFields);

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
