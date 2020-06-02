package org.erhun.framework.orm.entities;

import java.time.Instant;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
public interface IHistoryEntity {

	/**
	 * 获取创建时间
	 * @return
	 */
	Instant getCreateTime();

	/**
	 * 设置创建时间
	 * @param createTime
	 */
	void setCreateTime(Instant createTime);

	/**
	 * 获取更新时间
	 * @return
	 */
	Instant getUpdateTime();

	/**
	 * 设置更新时间
	 * @param updateTime
	 */
	void setUpdateTime(Instant updateTime);

}
