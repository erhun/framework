package com.erhun.framework.domain.entities;


import com.erhun.framework.orm.entities.IVirtualDeleteEntity;
import com.erhun.framework.orm.entities.IVersionEntity;

import javax.persistence.Column;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
public abstract class AbstractBizEntity<Id> extends AbstractHistoryEntity <Id> implements IVirtualDeleteEntity {

	@Column(name="deleted")
	protected Boolean deleted;

	@Override
	public Boolean getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

}
