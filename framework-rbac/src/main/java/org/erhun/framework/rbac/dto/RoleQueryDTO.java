package org.erhun.framework.rbac.dto;

import org.erhun.framework.orm.dto.BaseDTO;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2020/6/11
 */
public class RoleQueryDTO implements BaseDTO {

    private String groupId;

    private String applicationId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
