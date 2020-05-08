package com.erhun.framework.domain.services;

import com.erhun.framework.basic.utils.PV;
import com.erhun.framework.basic.utils.ResultPack;
import com.erhun.framework.basic.utils.generics.GenericsUtils;
import com.erhun.framework.basic.utils.number.NumberUtils;
import com.erhun.framework.basic.utils.reflection.ReflectionUtils;
import com.erhun.framework.basic.utils.string.StringUtils;
import com.erhun.framework.basic.utils.uuid.ObjectId;
import com.erhun.framework.domain.dao.BaseDAO;
import com.erhun.framework.domain.entities.BaseEntity;
import com.erhun.framework.orm.Criteria;
import com.erhun.framework.orm.Limits;
import com.erhun.framework.orm.QueryParam;
import com.erhun.framework.orm.SQLUtils;
import com.erhun.framework.orm.annotations.SectionId;
import com.erhun.framework.orm.entities.IVirtualDeleteEntity;
import com.erhun.framework.orm.entities.IHistoryEntity;
import com.erhun.framework.orm.entities.ISectionIdEntity;
import com.erhun.framework.orm.entities.IVersionEntity;
import com.erhun.framework.orm.identity.IdentityManager;
import com.erhun.framework.orm.version.VersionManager;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.lang.reflect.Field;
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
    protected IdentityManager identityManager;

    @Autowired
    protected VersionManager versionManager;

    private final String SECTION_IDENTITY_KEY = "user_id";

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
        
        if(String.class.isAssignableFrom(idClass)){
            entity.setId((Id) ObjectId.id());
        }

        SectionId sectionId = entity.getClass().getAnnotation(SectionId.class);

        if(sectionId != null){
            String sectionIdentity = null;
            if(context != null){
                sectionIdentity = context.getRequestHeader(SECTION_IDENTITY_KEY);
            }
            if(StringUtils.isEmpty(sectionIdentity)){
                throw new BusinessException(StringUtils.format("{}类定义了分段ID策略请求头中必须包含user_id", entity.getClass().getName()));
            }
            int sectionIndex = identityManager.getSectionIndex(NumberUtils.toLong(sectionIdentity));
            Long id = identityManager.getSectionId(entity.getClass(), sectionIndex);
            logger.debug("className: {}, sectionId: {}, sectionIndex: {}, identity: {}",
                    entity.getClass().getSimpleName(), sectionIdentity, sectionIndex, id);
            entity.setId((Id)id);
        }

        if( entity instanceof ISectionIdEntity){
            ISectionIdEntity sectionIdEntity = (ISectionIdEntity) entity;
            int sectionIndex = identityManager.getSectionIndex(sectionIdEntity.getSectionIdentity());
            Long id = identityManager.getSectionId(entity.getClass(), sectionIndex);
            logger.debug("className: {}, sectionId: {}, sectionIndex: {}, identity: {}",
                        entity.getClass().getSimpleName(), sectionIdEntity.getSectionIdentity(), sectionIndex, id);
            entity.setId((Id)id);
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

        if (entity instanceof IVersionEntity) {
            IVersionEntity versionEntity = (IVersionEntity) entity;
            String sectionIdentity = null;
            if(context != null){
                sectionIdentity = context.getRequestHeader(SECTION_IDENTITY_KEY);
            }
            if(StringUtils.isEmpty(sectionIdentity)){
                throw new BusinessException(StringUtils.format("{}类实现了IVersionEntity请求头中必须包含user_id", entity.getClass().getName()));
            }
            long version = versionManager.updateVersion(NumberUtils.toLong(sectionIdentity), entity.getClass());
            versionEntity.setVersion(version);
        }
        
        baseDao.update(entity);
        
        return entity;
    }
    
    @Override
    @Transactional
    public E update(E entity, String ...affectives) throws BusinessException {
        
        PV pvs [] = new PV[affectives.length];
        
        for (int i = 0; i < affectives.length; i++) {
            String name = affectives[i];
            Field field = ReflectionUtils.field(entity.getClass(), name);
            if(field == null) {
                throw new BusinessException("can't find affective attribute '" + name + "'");
            }
            pvs[i] = PV.nv(field.getName(), SQLUtils.resolveColumnName(entityClass, field));
        }

        baseDao.update(entity, pvs);
        
        return entity;
    }
    
    @Override
    public boolean delete(Id id) throws BusinessException {
        baseDao.delete(id);
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
    public List <E> queryById(String id) {
        return baseDao.queryById(id);
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

        convertColumnName(entityClass, fetchFields);

        List <E> list = baseDao.queryByPage(queryParam, fetchFields, Limits.of(pageNo, pageSize));

        long totalRecords = 0;

        if(pageNo > 1 || pageNo == 1 && list.size() == pageSize) {
            totalRecords = baseDao.countByPage(queryParam);
        }

        return ResultPack.result(totalRecords, list);
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
        convertColumnName(queryParam.getClass(), fetchFields);
        return baseDao.queryMetadata(queryParam, fetchFields, Limits.of(pageNo, pageSize));
    }

    protected boolean duplicate(String id, String fieldName, String value) {
        return baseDao.duplicate(id, PV.nv(fieldName, value)) > 0;
    }

    private String convertColumnName(Class entityClass, String field) {
        Field fd = ReflectionUtils.findField(entityClass, field);
        if(field == null) {
            throw new IllegalArgumentException(fd.getName() + " no find.");
        }
        return SQLUtils.resolveColumnName(entityClass, fd);
    }
    
    private void convertColumnName(Class entityClass, String[] fields) {
        if(fields != null && fields.length > 0){
            for (int i = 0; i < fields.length; i++) {
                Field field = ReflectionUtils.findField(entityClass, fields[i]);
                if(field == null) {
                    throw new IllegalArgumentException(field.getName() + " no find.");
                }
                fields[i] = SQLUtils.resolveColumnName(entityClass, field);
            }
        }
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
