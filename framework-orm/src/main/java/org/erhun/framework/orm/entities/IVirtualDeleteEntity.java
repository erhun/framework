package org.erhun.framework.orm.entities;

/**
 * 逻辑删除实体接口需要逻辑删除时继承此实体
 * @author weichao<gorilla@gliyun.com>
 *
 */
public interface IVirtualDeleteEntity {

	/**
	 * 
	 * @return
	 */
	public Boolean getDeleted();
	
	/**
	 * 
	 * @param deleted
	 */
	public void setDeleted(Boolean deleted);
	
}
