package org.erhun.framework.domain.services;

import org.apache.ibatis.annotations.Param;
import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.basic.utils.PageResult;
import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.generics.GenericsUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.basic.utils.uuid.ObjectId;
import org.erhun.framework.domain.dao.BaseDAO;
import org.erhun.framework.domain.entities.BaseEntity;
import org.erhun.framework.orm.*;
import org.erhun.framework.orm.annotations.IdGenerator;
import org.erhun.framework.orm.annotations.IdentityType;
import org.erhun.framework.orm.entities.IHistoryEntity;
import org.erhun.framework.orm.entities.IVirtualDeleteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 *
 * @author weichao<gorilla@aliyun.com>
 *
 * @param <E>
 */
public abstract class AbstractBusinessService<Id extends Serializable, E extends BaseEntity<Id>> implements IBusinessService<Id, E> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Class<E> entityClass;

    protected Class <Id> idClass;

    @Autowired
    protected BaseDAO<Id, E> baseDao;

    @Autowired
    protected BusinessContext context;

    @Autowired
    private ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    public AbstractBusinessService() {
        idClass = (Class<Id>) GenericsUtils.getSuperClassGenricType(getClass(), 0);
        entityClass = (Class<E>) GenericsUtils.getSuperClassGenricType(getClass(), 1);
    }

    public AbstractBusinessService(BusinessContext context) {
        this();
        this.context = context;
    }

    @Override
    @Transactional
    public E save(E entity) throws BusinessException {

        if(entity.getId() == null || ((entity.getId() instanceof String) && StringUtils.isBlank((String)entity.getId()))){
            add(entity);
        }else{
            update(entity);
        }

        return entity;

    }

    @Override
    @Transactional
    public E add(E entity) throws BusinessException {

        if (entity instanceof IHistoryEntity) {
            IHistoryEntity his = (IHistoryEntity) entity;
            if(his.getCreateTime() == null) {
                his.setCreateTime(Instant.now());
            }
            if(his.getUpdateTime() == null) {
                his.setUpdateTime(Instant.now());
            }
        }

        if (entity instanceof IVirtualDeleteEntity) {
            ((IVirtualDeleteEntity) entity).setDeleted(true);
        }

        IdGenerator idGenerator = entityClass.getAnnotation(IdGenerator.class);

        if(idGenerator != null){
            if(idGenerator.type() == IdentityType.SNOWFLAKE){
                IdGeneration idGeneratorService = (IdGeneration) applicationContext.getBean(idGenerator.value());
                if(idGeneratorService != null){
                    Object id = idGeneratorService.generate();
                    if(id == null){
                        throw new IllegalArgumentException("id generate failed");
                    }
                    entity.setId((Id)id);
                }
            }
        }else {
            if (String.class.isAssignableFrom(idClass)) {
                entity.setId((Id) ObjectId.id());
            }
        }

        baseDao.add(entity);

        return entity;
    }

    @Override
    @Transactional
    public void add(List <E> entities) throws BusinessException {

        baseDao.addList(entities);

    }

    @Override
    @Transactional
    public E update(E entity) throws BusinessException {

        if (entity instanceof IHistoryEntity) {
            IHistoryEntity his = (IHistoryEntity) entity;
            if(his.getUpdateTime() == null) {
                his.setUpdateTime(Instant.now());
            }
        }

        baseDao.update(entity);

        return entity;
    }

    @Override
    @Transactional
    public E update(E entity, String ...affectives) throws BusinessException {
        baseDao.update(entity, affectives);
        return entity;
    }

    @Override
    public boolean delete(Id id) throws BusinessException {
        baseDao.delete(id);
        return true;
    }

    @Override
    public boolean deleteAll(String idList) throws BusinessException {
        baseDao.deleteAll(idList);
        return true;
    }

    @Override
    public E get(Id id) {
        return baseDao.get(id);
    }

    @Override
    public E find(PV... pvs) {
        return baseDao.findByColumn(pvs);
    }

    @Override
    public E find(Criteria criteria) {
        return baseDao.findByCriteria(criteria);
    }

    public E findByColumn(@Param("pv") PV ...pv){
        return baseDao.findByColumn(pv);
    }

    @Override
    public List<E> queryAll() {
        return baseDao.queryAll();
    }

    @Override
    public List <E> queryById(String idList) {
        return baseDao.queryById(idList);
    }

    @Override
    public List <E> queryByCriteria(Criteria criteria) {
        return baseDao.queryByCriteria(criteria);
    }

    @Override
    public ResultPack queryByPage(QueryParam queryParam){
        return this.queryByPage(queryParam, null);
    }

    @Override
    public ResultPack queryByPage(QueryParam queryParam, String[] fetchFields){

        Integer pageNo = queryParam.getPageNo();
        Integer pageSize = queryParam.getPageSize();

        if(pageNo == null || pageNo < 1){
            pageNo = 1;
        }

        if(pageSize == null){
            pageSize = 20;
        }

        PageResult result = baseDao.queryByPage(queryParam, fetchFields, Limits.of(pageNo, pageSize));

        return ResultPack.result(result.getTotalRecords(), result.getData());
    }

    @Override
    public ResultPack queryByPage(QueryParam queryParam, String[] fetchFields, Criteria criteria) {
        Integer pageNo = queryParam.getPageNo();
        Integer pageSize = queryParam.getPageSize();

        if(pageNo == null || pageNo < 1){
            pageNo = 1;
        }

        if(pageSize == null){
            pageSize = 20;
        }

        PageResult result = baseDao.queryByPage(queryParam, fetchFields, criteria, Limits.of(pageNo, pageSize));

        return ResultPack.result(result.getTotalRecords(), result.getData());
    }

    @Override
    public PageResult<E> queryByNextPage(QueryParam entity, String[] fetchColumns, Limits limit) {
        return baseDao.queryByNextPage(entity, fetchColumns, limit);
    }

    @Override
    public PageResult<E> queryByNextPage(QueryParam entity, String[] fetchColumns, Criteria criteria, Limits limit) {
        return baseDao.queryByNextPage(entity, fetchColumns, criteria, limit);
    }

    @Override
    public <T> List<T> queryMetadata(QueryParam queryParam) {
        return this.queryMetadata(queryParam, null);
    }

    @Override
    public <T> List<T> queryMetadata(QueryParam queryParam, String[] fetchFields) {
        Integer pageNo = queryParam.getPageNo();
        Integer pageSize = queryParam.getPageSize();
        if(pageNo == null || pageNo < 1){
            pageNo = 1;
        }
        if(pageSize == null){
            pageSize = 20;
        }
        return baseDao.queryMetadata(queryParam, fetchFields, Limits.of(pageNo, pageSize));
    }

    protected boolean duplicate(String id, String fieldName, String value) {
        return baseDao.duplicate(id, PV.nv(fieldName, value)) > 0;
    }

    @Override
    public BusinessContext getBusinessContext() {
        return context;
    }

    @Override
    public void setBusinessContext(BusinessContext ctx) {
        this.context = ctx;
    }

    @Override
    @PreDestroy
    public void finalize() {
        this.context = null;
    }

}
