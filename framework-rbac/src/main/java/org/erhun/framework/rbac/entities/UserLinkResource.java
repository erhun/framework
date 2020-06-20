package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.BaseEntity;
import org.erhun.framework.orm.annotations.Table;

import javax.persistence.Entity;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Entity
@Table("t_rbac_user_link_resource")
public class UserLinkResource extends BaseEntity<String> {

	private String userId;
	
	private String resourceId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}
