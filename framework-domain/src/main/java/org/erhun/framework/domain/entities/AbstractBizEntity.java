package org.erhun.framework.domain.entities;


import org.erhun.framework.orm.entities.ICodeEntity;
import org.erhun.framework.orm.entities.IVirtualDeleteEntity;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
public abstract class AbstractBizEntity<Id> extends AbstractHistoryEntity <Id> implements ICodeEntity, IVirtualDeleteEntity {

	private String code;

	protected Boolean deleted;

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Boolean getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

}
