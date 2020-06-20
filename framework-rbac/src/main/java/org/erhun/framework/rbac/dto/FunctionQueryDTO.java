package org.erhun.framework.rbac.dto;

import org.erhun.framework.orm.dto.BaseDTO;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2020/6/11
 */
public class FunctionQueryDTO implements BaseDTO {

    private String moduleId;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
