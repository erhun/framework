package org.erhun.framework.rbac.services;


import org.erhun.framework.basic.security.User;
import org.erhun.framework.rbac.*;
import org.erhun.framework.rbac.entities.UserInfo;
import org.erhun.framework.rbac.entities.UserSetting;
import org.erhun.framework.rbac.utils.tree.RbacTree;

import java.util.List;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
public class UserImpl implements RbacUser {
	
	public final static User EMPTY_USER = new UserImpl(new UserInfo());

	private String authToken;
	
	private UserInfo userInfo;
	
	private List <RbacRole> roles;

	private List <RbacGroup> groups;
	
	private RbacTree<RbacModule> moduleTree;

	private List <RbacModule> modules;

	private List <RbacApplication> applications;
	
	private UserSetting userSetting;
	
	public UserImpl(){
	}
	
	public UserImpl(UserInfo userInfo){
		this.userInfo = userInfo;
	}

	@Override
	public String getId() {
		
		return userInfo.getId();
	}

	@Override
	public String getName() {
		return userInfo.getName();
	}

	public void setUserInfo( UserInfo userInfo ) {
		this.userInfo = userInfo;
	}

	@Override
	public List <RbacRole> getRoles() {
		return roles;
	}

	public void setRoles( List <RbacRole> roles ) {
		this.roles = roles;
	}

	@Override
	public String getToken() {
		return null;
	}

	public UserSetting getUserSetting() {
		return userSetting;
	}

	public void setUserSetting(UserSetting userSetting) {
		this.userSetting = userSetting;
	}

	@Override
	public RbacTree<RbacModule> getModuleTree() {
		return this.moduleTree;
	}

	public void setModuleTree( RbacTree<RbacModule> moduleTree ) {
		this.moduleTree = moduleTree;
	}

    @Override
    public String getAccount() {
        return userInfo.getAccount();
    }

    @Override
    public String getCode() {
        return userInfo.getCode();
    }

    @Override
    public List<RbacApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<RbacApplication> applications) {
        this.applications = applications;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public boolean isAdmin() {
        return "admin".equals(userInfo.getAccount());
    }

    @Override
    public boolean isNormalAdmin() {
        return (userInfo.isAdmin() != null && userInfo.isAdmin());
    }

	public List<RbacModule> getModules() {
		return modules;
	}

	public void setModules(List<RbacModule> modules) {
		this.modules = modules;
	}

	public List<RbacGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<RbacGroup> groups) {
		this.groups = groups;
	}
}
