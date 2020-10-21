package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractBizEntity;
import org.erhun.framework.orm.annotations.AttrDef;
import org.erhun.framework.orm.annotations.Table;
import org.erhun.framework.orm.annotations.validator.Length;
import org.erhun.framework.orm.annotations.validator.Prefix;
import org.erhun.framework.orm.annotations.validator.Unique;
import org.erhun.framework.rbac.RbacApplication;
import org.erhun.framework.rbac.RbacModule;

import java.util.List;

/**
 *
 * @author weichao<gorilla@gliyun.com>
 *
 */
@Table(value = "t_rbac_application", alias = "应用")
public class ApplicationInfo extends AbstractBizEntity<String> implements RbacApplication {

    private String name;

    /**
     * 1 内部应用、2 外部应用
     */
    @AttrDef(item="YYLX", creatable = false)
    private String type;

    @Unique
    @AttrDef(alias="AppId")
    private Integer appId;

    //@ValueGenerator(AppKeyGenerator.class)
    @Length(min=32, max=32)
    private String appKey;

    /**
     * 自定义首页
     */
    private String indexUrl;

    @Prefix("http://,https://")
    @AttrDef(alias = "登录URL")
    private String loginUrl;

    @Prefix("http://,https://")
    @AttrDef(alias = "退出URL")
    private String logoutUrl;

    private String privateExponent;

    private String privateModulus;

    private String publicExponent;

    private String publicModulus;

    private String icon;

    private String memo;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String getLoginUrl() {
        return loginUrl;
    }

    @Override
    public String getLogoutUrl() {
        return logoutUrl;
    }

    @Override
    public List<RbacModule> getModules() {
        return null;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    @Override
    public String toString() {
        return getId();
    }

    public String getPrivateExponent() {
        return privateExponent;
    }

    public void setPrivateExponent(String privateExponent) {
        this.privateExponent = privateExponent;
    }

    public String getPrivateModulus() {
        return privateModulus;
    }

    public void setPrivateModulus(String privateModulus) {
        this.privateModulus = privateModulus;
    }

    public String getPublicExponent() {
        return publicExponent;
    }

    public void setPublicExponent(String publicExponent) {
        this.publicExponent = publicExponent;
    }

    public String getPublicModulus() {
        return publicModulus;
    }

    public void setPublicModulus(String publicModulus) {
        this.publicModulus = publicModulus;
    }

    public String getIndexUrl() {
        return indexUrl;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
    }
}
