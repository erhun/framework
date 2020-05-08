package com.erhun.framework.domain.entities;

import com.erhun.framework.orm.entities.IHistoryEntity;

import java.time.Instant;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 * @param <Id>
 */
public abstract class AbstractHistoryEntity <Id> extends BaseEntity <Id> implements IHistoryEntity {

	/**
	 * 创建时间
	 */
	protected Instant created;
    
    /**
     * 更新时间
     */
    protected Instant updated;

	@Override
	public Instant getCreateTime() {
		return created;
	}

	@Override
	public void setCreateTime(Instant createTime) {
		this.created = createTime;
	}

	@Override
	public Instant getUpdateTime() {
		return updated;
	}

	@Override
	public void setUpdateTime(Instant updateTime) {
		this.updated = updateTime;
	}
}
