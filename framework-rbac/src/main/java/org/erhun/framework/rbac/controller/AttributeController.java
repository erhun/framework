package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.controller.AbstractBusinessController;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.orm.QueryParam;
import org.erhun.framework.rbac.entities.AttributeInfo;
import org.erhun.framework.rbac.entities.EntityInfo;
import org.erhun.framework.rbac.dto.AttributeQueryDTO;
import org.erhun.framework.rbac.services.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author weichao (gorilla@aliyun.com)
 */
@Controller
@RequestMapping("/admin/rbac/attributes")
public class AttributeController extends AbstractBusinessController <String, AttributeInfo, AttributeQueryDTO> {

    @Autowired
    private EntityService entityService;

    @RequestMapping(method = RequestMethod.GET)
    public String listPage() throws Exception {
        return "attribute";
    }

    @Override
    public ResultPack list(QueryParam <AttributeQueryDTO> queryParam) {
        if (StringUtils.isBlank(queryParam.param().getEntityId())) {
            throw new BusinessException("实体ID不能为空");
        }
        return businessService.queryByPage(queryParam);
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(HttpServletRequest request, @RequestParam String entityId) throws Exception {
        EntityInfo entity = entityService.get(entityId);
        AttributeInfo attribute = new AttributeInfo();
        attribute.setEntityId(entity.getId());
        attribute.setEntityText(entity.getName());
        request.setAttribute("attribute", attribute);
        return "attribute_edit";
    }

    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, @PathVariable String id) throws Exception {

        AttributeInfo attribute = businessService.get(id);

        EntityInfo module = entityService.get(attribute.getEntityId());

        attribute.setEntityText(module.getName());

        request.setAttribute("attribute", attribute);

        return "attribute_edit";
    }

}
