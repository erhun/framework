package com.erhun.framework.domain.entities;

import com.erhun.framework.orm.entities.ICodeEntity;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
public abstract class AbstractCodeEntity <Id> extends BaseEntity <Id> implements ICodeEntity {

	protected String code;
	
	@Override
	public String getCode(){
		return code;
	}

	@Override
	public void setCode(String code ){
		this.code = code;
	}

}
