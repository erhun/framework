package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractBizEntity;
import org.erhun.framework.orm.annotations.Table;

/**
 * 
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Table("t_rbac_application_security")
public class ApplicationSecurity extends AbstractBizEntity<String> {

    private static final long serialVersionUID = 1L;

    private String applicationId;
    
    private Integer appId;
    
    private String appKey;
    
    private String privateKey;
    
    private String publicKey;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
