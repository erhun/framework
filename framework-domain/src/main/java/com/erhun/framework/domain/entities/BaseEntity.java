package com.erhun.framework.domain.entities;

import com.erhun.framework.orm.entities.IEntity;

/**
 * 
 * @author weichao <gorilla@aliyun.com>
 */
public abstract class BaseEntity <Id> implements IEntity<Id> {

	private Id id;

	@Override
	public Id getId() {
		return id;
	}

	@Override
	public void setId(Id id) {
		this.id = id;
	}
	
}
