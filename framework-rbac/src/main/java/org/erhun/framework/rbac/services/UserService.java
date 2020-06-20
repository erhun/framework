package org.erhun.framework.rbac.services;

import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.domain.services.IBusinessService;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.entities.*;
import org.erhun.framework.rbac.utils.tree.RbacTree;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
//@Service
@Component
public interface UserService extends IBusinessService<String, UserInfo> {

    List <String> queryAppIdByUserId(String userId);

    List <String> queryGroupCodeByUserId(String userId);

    List <ApplicationInfo> queryAppByUserId(String userId);
    
    List <String> queryGroupIdByUserId(String userId);

    RbacUser login(String userName, String password) throws BusinessException;

    RbacUser findByAccountAndPassword(String account, String password);

    RbacUser login(String account);

    boolean saveGroup(String userId, String groupId, String groupName);
    
    boolean save(UserLinkApplication ula) throws BusinessException;

    List<String> queryResourceIdByUserId(String applicationId, String userId, String resType);

    List<Object[]> queryMau();

    boolean save(UserLinkGroup ulg) throws BusinessException;
    
    boolean changePwd(String id, String password, String newPassword, String authCode) throws BusinessException;
    
    boolean retrievePwd(String account, String newPassword, String authCode) throws BusinessException;

    RbacTree<?> getModuleTreeByUserId(String appId, String userId);

    boolean changePwd(String id, String newPassword) throws BusinessException;

    boolean unlock(String id) throws BusinessException;

    boolean lock(String id) throws BusinessException;

    boolean save(UserLinkResource ula) throws BusinessException;

    List <ApplicationInfo> queryGroupAppByUserId(String userId);

    List<GroupInfo> queryGroupByUserId(String userId);

    @Transactional
    boolean logout(String userId, String applicationId) throws BusinessException;

    @Transactional
    boolean logon(String userId, String applicationId) throws BusinessException;
}
