package org.erhun.framework.domain.entities;

import org.erhun.framework.orm.entities.IHistoryEntity;

import java.time.Instant;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 * @param <Id>
 */
public abstract class AbstractHistoryEntity <Id> extends BaseEntity <Id> implements IHistoryEntity {

	private String creator;

	private String modificator;

	/**
	 * 创建时间
	 */
	protected Instant createTime;
    
    /**
     * 更新时间
     */
    protected Instant updateTime;

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModificator() {
		return modificator;
	}

	public void setModificator(String modificator) {
		this.modificator = modificator;
	}

	@Override
	public Instant getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Instant createTime) {
		this.createTime = createTime;
	}

	@Override
	public Instant getUpdateTime() {
		return updateTime;
	}

	@Override
	public void setUpdateTime(Instant updateTime) {
		this.updateTime = updateTime;
	}
}
