package org.erhun.framework.rbac.services.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.erhun.framework.basic.cache.CacheKeys;
import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.basic.utils.collection.ListUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.basic.utils.uuid.ObjectId;
import org.erhun.framework.domain.services.AbstractBusinessService;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.domain.utils.EntityUtils;
import org.erhun.framework.rbac.RbacUser;
import org.erhun.framework.rbac.dao.*;
import org.erhun.framework.rbac.entities.*;
import org.erhun.framework.rbac.services.ModuleService;
import org.erhun.framework.rbac.services.UserImpl;
import org.erhun.framework.rbac.services.UserService;
import org.erhun.framework.rbac.tree.FullModuleTreeGenerator;
import org.erhun.framework.rbac.utils.tree.RbacTree;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author weichao<gorilla@aliyun.com>
 */
@Service
public class UserServiceImpl extends AbstractBusinessService<String, UserInfo> implements UserService {

    @Autowired
    private UserDAO userDao;

    @Autowired
    private UserOnlineDAO userOnlineDao;

    @Autowired
    private UserLinkGroupDAO userGroupDao;

    @Autowired
    private UserLinkApplicationDAO ulaDao;

    @Autowired
    private UserLinkResourceDAO ulrDao;

    @Autowired
    private PermissionDAO permissionDao;

    @Autowired
    private ApplicationDAO appDao;

    @Autowired
    private ResourceDAO resourceDao;

    @Autowired
    private GroupDAO groupDao;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private Redisson redisson;
    
    @Value("application.id")
    private String appId;
    
    @Override
    @Transactional
    public UserInfo add(UserInfo entity) throws BusinessException {
        
        String pwd = entity.getPassword();
        
        if (StringUtils.isNotBlank(pwd)) {
            entity.setPassword(DigestUtils.md5Hex(DigestUtils.md5Hex(pwd)));
        }
        
        entity.setStatus("1");
        
        super.add(entity);
        
        UserLinkGroup app = new UserLinkGroup();
        
        app.setId(ObjectId.id());
        app.setGroupId(entity.getGroupId());
        app.setUserId(entity.getId());
        
        userGroupDao.add(app);
        
        return entity;
    }

    @Override
    public UserInfo update(UserInfo entity) throws BusinessException {
        
        String pwd = entity.getPassword();

        if (StringUtils.isNotBlank(pwd)) {
            entity.setPassword(DigestUtils.md5Hex(DigestUtils.md5Hex(pwd)));
        }
        
        return super.update(entity);

    }

    @Override
    public boolean saveGroup(String userId, String groupId, String groupName) {

        userGroupDao.deleteByColumn(PV.of("user_id", userId));

        String ids[] = groupId.split(",");

        for (String id : ids) {
            UserLinkGroup ug = new UserLinkGroup();
            ug.setUserId(userId);
            ug.setGroupId(id);
            ug.setId(ObjectId.id());
            save(ug);
        }

        return true;

    }

    @Override
    public RbacUser findByAccountAndPassword(String account, String password) {

        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            return null;
        }

        UserInfo userInfo = userDao.findByAccountAndPassword(account, password);

        return new UserImpl(userInfo);

    }

    @Override
    public RbacUser login(String account) {

        if (StringUtils.isBlank(account)) {
            return null;
        }

        UserInfo userInfo = userDao.findByColumn(PV.nv("account", account));

        return initUser(userInfo);

    }

    @Override
    public RbacUser login(String userName, String password) throws BusinessException {

        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            return null;
        }

        UserInfo userInfo = userDao.findByAccountAndPassword(userName, password);
        
        if (userInfo == null) {
            userInfo = userDao.findByColumn(PV.nv("account", userName));
            if(userInfo != null) {
                if(userInfo.getErrorCount() != null && userInfo.getErrorCount() >= 5) {
                    userInfo.setStatus("2");
                    update(userInfo, "status");
                    throw new BusinessException("登录错误次数过多，帐号已被锁定");
                }else {
                    userInfo.setErrorCount(userInfo.getErrorCount() == null ? 0 : userInfo.getErrorCount()+1);
                    update(userInfo, "errorCount");
                }
                
            }
            //log(null, "LOGIN", "登录", "登录", "失败", "[登录]用户" + userName + "在" + DateFormatUtils.toDateTimeString(new Date()) + "发生登录,状态为[失败]");
            return null;
        }
        
        if(userInfo.getErrorCount() != null && userInfo.getErrorCount() >0) {
            userInfo.setErrorCount(0);
            update(userInfo, "errorCount");
        }
        
        if("2".equals(userInfo.getStatus())) {
            throw new BusinessException("用户状态为锁定");
        }
        
        if("2".equals(userInfo.getType())) {
            throw new BusinessException("外部用户不允许在门户中登录");
        }
        
        if(userInfo.getErrorCount() != null && userInfo.getErrorCount() > 0) {
            userInfo.setErrorCount(0);
        }
        
        //log(null, "LOGIN", "登录", "登录", "成功", "[登录]用户" + userName + "在" + DateFormatUtils.toDateTimeString(new Date()) + "发生登录,状态为[成功]");

        userInfo.setLoginTime(new Date());
        update(userInfo, "loginTime");
        
        return initUser(userInfo);

    }

    @SuppressWarnings("unchecked")
    private RbacUser initUser(UserInfo userInfo) {

        UserImpl user = new UserImpl();

        user.setUserInfo(userInfo);

        List <GroupInfo> groups = userDao.queryGroupById(userInfo.getId());
        List <RoleInfo> roles = userDao.queryRoleById(userInfo.getId());

        if (ListUtils.isNotEmpty(roles)) {
            List <ApplicationInfo> apps = this.queryAppByUserId(user.getId());
            ApplicationInfo currentApp = appDao.get(appId);
            List <String> appList = new ArrayList <String> (1);
            appList.add(currentApp.getId());
            user.setRoles(ListUtils.as(roles));
            user.setApplications(ListUtils.as(apps));
            user.setGroups(ListUtils.as(groups));
            //user.setModules(ListUtils.as(moduleService.findByAppId(currentApp.getId())));
            user.setModuleTree(new FullModuleTreeGenerator(appList, EntityUtils.idList(roles)).generate());
        }
        
        return user;

    }

    @Override
    public List <String> queryAppIdByUserId(String userId){
        return ulaDao.queryByColumn("application_id", PV.of("user_id", userId));
    }
    
    @Override
    public boolean save(UserLinkApplication ula) throws BusinessException {
        
        if(StringUtils.isBlank(ula.getUserId()) || StringUtils.isBlank(ula.getApplicationId())){
            throw new BusinessException("用户ID或组应用D不能为空!");
        }
        
        UserLinkApplication app = ulaDao.findByColumn(PV.of("user_id", ula.getUserId(), "application_id", ula.getApplicationId()));
        
        UserInfo user = userDao.get(ula.getUserId());
        ApplicationInfo appInfo = appDao.get(ula.getApplicationId());
        
        if("2".equals(user.getType()) && "1".equals(appInfo.getType())) {
            throw new BusinessException("外部用户不能分配内部应用。");
        }
       
        if(app == null){
           ula.setId(ObjectId.id());
           ulaDao.add(ula);
           //log(ula, "APP->USER", "分配应用", "设置", "成功", "[分配应用]用户["+user.getAccount()+"]被["+context.getUser().getAccount()+"]分配应用["+appInfo.getName()+"]");
        }else{
           ulaDao.delete(app.getId());
           //log(ula, "APP->USER", "分配应用", "设置", "成功", "[分配应用]用户["+user.getAccount()+"]被["+context.getUser().getAccount()+"]删除应用["+appInfo.getName()+"]");
        }
        
        return true;
    }

    @Override
    public boolean save(UserLinkResource ulr) throws BusinessException {

        if(StringUtils.isBlank(ulr.getUserId()) || StringUtils.isBlank(ulr.getResourceId())){
            throw new BusinessException("用户ID或资源ID不能为空!");
        }

        UserLinkResource ur = ulrDao.findByColumn(PV.of("user_id", ulr.getUserId(), "resource_id", ulr.getResourceId()));

        UserInfo user = userDao.get(ulr.getUserId());
        ResourceInfo resourceInfo = resourceDao.get(ulr.getResourceId());

        if(ur == null){
            ulr.setId(ObjectId.id());
            ulrDao.add(ulr);
            //log(ulr, "APP->RESOURCE", "分配资源", "设置", "成功", "[分配资源]用户["+user.getAccount()+"]被["+context.getUser().getAccount()+"]分配资源["+resourceInfo.getName()+"]");
        }else{
            ulrDao.delete(ur.getId());
            permissionDao.deleteByUserIdAndValue(ulr.getUserId(), ulr.getResourceId());
            //log(ulr, "APP->RESOURCE", "分配资源", "设置", "成功", "[分配资源]用户["+user.getAccount()+"]被["+context.getUser().getAccount()+"]删除资源["+resourceInfo.getName()+"]");
        }

        return true;
    }
    
    @Override
    public List <ApplicationInfo> queryGroupAppByUserId(String userId) {
        return userDao.queryGroupAppById(userId);
    }
    
    @Override
    public List <GroupInfo> queryGroupByUserId(String userId) {
        return userDao.queryGroupById(userId);
    }

    @Override
    public List <String> queryGroupCodeByUserId(String userId) {
        return userDao.queryGroupCodeById(userId);
    }

    @Override
    public List <ApplicationInfo> queryAppByUserId(String userId) {
        return userDao.queryAppById(userId);
    }

    @Override
    public List<String> queryGroupIdByUserId(String userId) { 
        return userGroupDao.queryByColumn("group_id", PV.of("user_id", userId));
    }

    @Override
    public List<String> queryResourceIdByUserId(String applicationId, String userId, String resType) {
        return resourceDao.queryResourceIdByUserId(applicationId, userId, resType);
    }

    @Override
    public List<Object[]> queryMau() {
        return userDao.queryMau();
    }

    @Override
    public boolean save(UserLinkGroup ulg) throws BusinessException {
        
        if(StringUtils.isBlank(ulg.getUserId()) || StringUtils.isBlank(ulg.getGroupId())){
            throw new BusinessException("用户ID或组ID不能为空!");
        }
        
        UserLinkGroup tmp = userGroupDao.findByColumn(PV.of("user_id", ulg.getUserId(), "group_id", ulg.getGroupId()));
        
        UserInfo user = userDao.get(ulg.getUserId());
        GroupInfo groupInfo = groupDao.get(ulg.getGroupId());
        
        if(tmp == null){
            ulg.setId(ObjectId.id());
            userGroupDao.add(ulg);
            //log(ulg, "GROUP->USER", "分配组", "设置", "成功", "[分配组]用户["+user.getAccount()+"]被["+context.getUser().getAccount()+"]加入用户组["+groupInfo.getName() + "("+groupInfo.getCode()+")" +"]");
        }else{
            userGroupDao.deleteByColumn(PV.of("user_id", ulg.getUserId(), "group_id", ulg.getGroupId()));
            //log(ulg, "GROUP->USER", "分配组", "设置", "成功", "[分配组]用户["+user.getAccount()+"]被["+context.getUser().getAccount()+"]从用户组["+groupInfo.getName() + "("+groupInfo.getCode()+")" + "]中移除");
        }
         
        return true;
    }
    
    @Override
    public RbacTree<ModuleInfo> getModuleTreeByUserId(String appId, String userId) {
        
        List <String> roles = userDao.queryRoleIdById(userId);
        
        if(ListUtils.isEmpty(roles)) {
            return null;
        }
        
        return new FullModuleTreeGenerator(appId, roles).generate();
        
    }

    @Override
    public boolean changePwd(String id, String password, String newPassword, String authCode) throws BusinessException{
        
        if(StringUtils.isBlank(password)) {
            throw new BusinessException("原密码不能为空");
        }
        
        if(StringUtils.isBlank(newPassword)) {
            throw new BusinessException("新密码不能为空");
        }
        
        String cacheKey = "USER_CHANGE_PWD:" + id;
        
        String cacheCode = (String) redisson.getMap(CacheKeys.COMMON_CACHE_KEY).get(cacheKey);
        
        if(StringUtils.isBlank(cacheCode)) {
            throw new BusinessException("验证码已过期，请重新发送。");
        }
        
        if(!cacheCode.equals(authCode)) {
            throw new BusinessException("验证码验证不通过。");
        }
        
        UserInfo user = userDao.get(id);
        
        if(user == null) {
            throw new BusinessException("用户不存在");
        }
        
        String md5Pwd = DigestUtils.md5Hex(DigestUtils.md5Hex(password));
        
        if(!md5Pwd.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        String newPwd = DigestUtils.md5Hex(DigestUtils.md5Hex(newPassword));
        
        if(newPwd.equals(user.getPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }
        
        userDao.updateColumn(user.getId(), "password", DigestUtils.md5Hex(DigestUtils.md5Hex(newPassword)));
        
        redisson.getMap(CacheKeys.COMMON_CACHE_KEY).remove(cacheKey);
        
        return false;
    }
    
    @Override
    public boolean retrievePwd(String account, String newPassword, String authCode)
            throws BusinessException {
        
        if(StringUtils.isBlank(newPassword)) {
            throw new BusinessException("新密码不能为空");
        }
        
        if(StringUtils.isBlank(authCode)) {
            throw new BusinessException("验证码不能为空");
        }
        
        String cacheKey = "USER_CHANGE_PWD:" + account;
        
        String cacheCode = (String) redisson.getMap(CacheKeys.COMMON_CACHE_KEY).get(cacheKey);
        
        if(StringUtils.isBlank(cacheCode)) {
            throw new BusinessException("验证码已过期，请重新发送。");
        }
        
        if(!cacheCode.equals(authCode)) {
            throw new BusinessException("验证码验证不通过。");
        }
        
        UserInfo user = userDao.findByColumn(PV.nv("account", account));//(account);
        
        if(user == null) {
            throw new BusinessException("用户不存在");
        }
        
        String newPwd = DigestUtils.md5Hex(DigestUtils.md5Hex(newPassword));
        
        if(newPwd.equals(user.getPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }
        
        userDao.updateColumn(user.getId(), "password", DigestUtils.md5Hex(DigestUtils.md5Hex(newPassword)));

        redisson.getMap(CacheKeys.COMMON_CACHE_KEY).remove(cacheKey);

        return false;
    }
    
    @Override
    public boolean changePwd(String id, String newPassword) throws BusinessException{
        if(StringUtils.isBlank(newPassword)) {
            throw new BusinessException("新密码不能为空");
        }
        userDao.updateColumn(id, "password", DigestUtils.md5Hex(DigestUtils.md5Hex(newPassword)));
        return true;
    }
    
    @Override
    public boolean lock(String id) throws BusinessException{
        if(StringUtils.isBlank(id)) {
            throw new BusinessException("ID不能为空");
        }
        userDao.updateColumn(id, "status", "2");
        return true;
    }

    @Override
    public boolean unlock(String id) throws BusinessException{
        if(StringUtils.isBlank(id)) {
            throw new BusinessException("ID不能为空");
        }
        UserInfo user = new UserInfo();
        user.setId(id);
        user.setStatus("1");
        user.setErrorCount(0);
        update(user, "status", "errorCount");
        return true;
    }

    @Override
    @Transactional
    public boolean logout(String userId, String applicationId) throws BusinessException{

        if(StringUtils.isBlank(userId)) {
            throw new BusinessException("userId不能为空");
        }

        if(StringUtils.isBlank(applicationId)) {
            throw new BusinessException("applicationId不能为空");
        }

        ApplicationInfo app = appDao.get(applicationId);

        userOnlineDao.deleteByColumn(PV.of("user_id", userId, "application_id", applicationId));

        long count = userOnlineDao.count(PV.nv("user_id", userId));

        if(count == 0) {
            userDao.updateColumn(userId, "is_online", "0");
        }

        UserInfo userInfo = userDao.get(userId);

        //log(null, "LOGOUT", "退出", "退出", "成功", "[退出]用户"+userInfo.getAccount() + "在" + DateFormatUtils.toDateTimeString(new Date()) +"退出应用["+app.getName()+"]");

        return true;
    }

    @Override
    @Transactional
    public boolean logon(String userId, String applicationId) throws BusinessException{

        if(StringUtils.isBlank(userId)) {
            throw new BusinessException("userId不能为空");
        }

        UserOnline userOnline = userOnlineDao.findByColumn(PV.of("user_id", userId, "application_id", applicationId));

        if(userOnline != null) {
            return true;
        }

        userOnline = new UserOnline();
        userOnline.setId(ObjectId.id());
        userOnline.setUserId(userId);
        userOnline.setApplicationId(applicationId);
        userOnlineDao.add(userOnline);

        userDao.updateColumn(userId, "is_online", "1");

        return true;
    }

}
