package com.erhun.framework.orm;

import com.erhun.framework.orm.entities.IEntity;
import com.erhun.framework.basic.utils.PV;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 * @param <E>
 * @param <Id>
 */
public interface IGenericDAO<Id extends Serializable, E extends IEntity>{

	/**
	 * 插入
	 * @param entity
	 * @return
	 */
	int add(@Param("entity") E entity);
	
	/**
	 * 批量插入
	 */
	int addList(@Param("entities") List<E> entities);

	/**
	 * 通过ID获取记录
	 * @param id
	 * @return
	 */
	E get(@Param("id") Id id);

	/**
	 * 根据id查找一条记录
	 * @param id
	 * @return
	 */
	E findById(@Param("id") Id id);

    /**
     * 根据pv查找一条记录(pv可以使用表达式进行查询)
     * @param pv the column name
     * @return
     */
    E findByColumn(@Param("pvs") PV... pv);

	/**
	 * 根据criteria查找一条记录
	 * @param criteria
	 * @return
	 */
	E findByCriteria(@Param("criteria") Criteria criteria);

	/**
	 * 查询所有记录
	 * @return
	 */
	List <E> queryAll();

	/**
	 * 查询指定id记录
	 * @param ids more id a split by ','
	 * @return
	 */
    List <E> queryById(@Param("ids") String ids);

	/**
     * 根据criteria查询记录
     * @param criteria
     * @return
     */
    List <E> queryByCriteria(@Param("criteria") Criteria criteria);

	/**
     * 根据pvs查询记录
     * @param pv
     * @return
     */
    List <E> queryByColumn(@Param("pv") PV... pv);

    /**
     * 根据pvs查询记录并返回指定列
     * @param columns
     * @param pv
     * @return
     */
    List <E> queryByColumn(@Param("columns") String columns, @Param("pv") PV... pv);

	/**
	 *
	 * 根据entity分页查询并返回指定列并
	 * @param entity
	 * @param fetchColumns
	 * @param limit
	 * @return
	 */
	List <E> queryByPage(@Param("entity") QueryParam entity, @Param("fetchColumns") String fetchColumns[], @Param("limit") Limits limit);

	/**
	 * 根据queryParam查询记录并返回指定列
	 * @param queryParam
	 * @param fetchColumns
	 * @param limit
	 * @return
	 */
	<T> List <T> queryMetadata(@Param("entity") QueryParam queryParam, @Param("fetchColumns") String fetchColumns[], @Param("limit") Limits limit);

	/**
     * 统计记录
     * @param queryParam
     * @return
     */
    long countByPage(@Param("entity") QueryParam queryParam);

	/**
	 * 更新affects中指定的列
	 * @param entity
	 * @param affects 指定更新的列
	 * @return
	 */
    int update(@Param("entity") E entity, @Param("affects") PV... affects);

	/**
	 * 更新column中指定的列
	 * @param id
	 * @param column
	 * @param value
	 * @return
	 */
    int updateColumn(@Param("id") Id id, @Param("column") String column, @Param("value") Object value);

	/**
	 * 根据criteria更新entity
	 * @param criteria
	 * @return
	 */
	int updateByCriteria(@Param("criteria") Criteria criteria);

	/**
     * 根据id删除
     * @param id
     * @return
     */
    int delete(@Param("id") Id id);

    /**
     * 根据pvs删除
     * @param pvs
     * @return
     */
    int deleteByColumn(@Param("pvs") PV... pvs);

    /**
     * 统计pvs中是否有重复记录
     * @param id
     * @param pvs
     * @return
     */
    int duplicate(@Param("id") Serializable id, @Param("pvs") PV... pvs);

    /**
     * 是否存在
     * @param pvs
     * @return
     */
    boolean exist(@Param("pvs") PV... pvs);

	/**
	 * 统计
	 * @param pvs
	 * @return
	 */
	long count(@Param("pvs") PV... pvs);
	
	/**
     * 
     * @param column The column name
     * @param values
     * @return
     */
    List <E> in(@Param("column") String column, @Param("values") Serializable... values);
	
}
