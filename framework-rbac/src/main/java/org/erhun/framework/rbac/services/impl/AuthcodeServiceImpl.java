package org.erhun.framework.rbac.services.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.basic.utils.http.HttpClientUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.basic.utils.uuid.ObjectId;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.rbac.dao.UserDAO;
import org.erhun.framework.rbac.entities.UserInfo;
import org.erhun.framework.rbac.services.AuthcodeService;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
@Service
public class AuthcodeServiceImpl implements AuthcodeService {
    
    private Logger logger = LoggerFactory.getLogger(AuthcodeServiceImpl.class);
    
    @Autowired
    private UserDAO userDao;
    
    @Autowired
    private Redisson redis;

    @Override
    public boolean send(String userId) throws BusinessException {
        
        UserInfo user = userDao.get(userId);
        
        if(user == null) {
            throw new BusinessException("无效用户"); 
         }
        
        if(StringUtils.isNotBlank(user.getMobile())) {
            String authCode = String.valueOf(Math.random()).substring(2, 8);
            RMap rMap = redis.getMap("USER_CHANGE_PWD:" + userId);
            rMap.put("USER_CHANGE_PWD:" + userId, authCode);
            rMap.expire(60, TimeUnit.MILLISECONDS);
            sendMessage(user.getMobile(), user.getName() + "您好!您正在修改用户中心密码验证码为" + authCode + "如不是本人操作请及时修改密码或联系管理人员!");
            return true;
        }
        
        throw new BusinessException("用户未绑定手机号");
    }
    
    @Override
    public boolean sendByAccount(String account) throws BusinessException {
        
        UserInfo user = userDao.findByColumn(PV.nv("account", account));
        
        if(user == null) {
           throw new BusinessException("无效用户"); 
        }
        
        if(StringUtils.isNotBlank(user.getMobile())) {
            String authCode = String.valueOf(Math.random()).substring(2, 8);
            RMap rMap = redis.getMap("USER_CHANGE_PWD:" + account);
            rMap.put("USER_CHANGE_PWD:" + account, authCode);
            rMap.expire(60, TimeUnit.MILLISECONDS);
            sendMessage(user.getMobile(), user.getName() + "您好!您正在修改用户中心密码验证码为" + authCode + "如不是本人操作请及时修改密码或联系管理人员!");
            return true;
        }
        
        throw new BusinessException("用户未绑定手机号或邮箱");
    }
    
    
    public void sendMessage(String to, String content) {
        
        String appid = "1";
        String appkey = "1";
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        String templateid = "1018";
        String coopOrderNo = ObjectId.id();
        
        String sign = DigestUtils.md5Hex("appId"+appid+"content"+content+"coopOrderNo"+coopOrderNo+"templateId"+templateid+"timestamp"+timestamp+"to" + to +appkey);
        
        String url = "http://sms.tx.phone580.com/fzs-sms/webservice/sms/send?";
        
        try {
            url += "appId="+appid+"&content="+URLEncoder.encode(content, "UTF-8")+"&coopOrderNo="+coopOrderNo+"&to=" + to + "&timestamp="+timestamp+"&templateId="+templateid+"&sign="+sign;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        String rs = HttpClientUtils.doGet(url);
        
        logger.info("message result: " + rs);
    }
    
}
