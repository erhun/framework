package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.domain.controller.AbstractBusinessController;
import org.erhun.framework.domain.services.BusinessException;
import org.erhun.framework.orm.QueryParam;
import org.erhun.framework.rbac.dto.EntityQueryDTO;
import org.erhun.framework.rbac.entities.ApplicationInfo;
import org.erhun.framework.rbac.entities.EntityInfo;
import org.erhun.framework.rbac.services.ApplicationService;
import org.erhun.framework.rbac.services.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * @author weichao (gorilla@aliyun.com)
 */
@Controller
@RequestMapping("/admin/rbac/entities")
public class EntityController extends AbstractBusinessController <String, EntityInfo, EntityQueryDTO> {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(method = RequestMethod.GET)
    public String listPage() throws Exception {
        return "entity";
    }

    @Override
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, headers = "accept=application/json")
    public ResultPack list(QueryParam <EntityQueryDTO> queryDto){
        if (StringUtils.isBlank(queryDto.param().getApplicationId())) {
            throw new BusinessException("应用ID不能为空");
        }
        return entityService.queryByPage(queryDto);

    }

    @ResponseBody
    @RequestMapping(value="queryByAppId", method = RequestMethod.GET)
    public List<EntityInfo> queryByAppId(String appId)
            throws Exception {
        return entityService.queryByAppId(appId);

    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(HttpServletRequest request, @RequestParam String applicationId) throws Exception {

        ApplicationInfo applicationInfo = applicationService.get(applicationId);
        EntityInfo entity = new EntityInfo();
        entity.setApplicationId(applicationId);
        entity.setApplicationText(applicationInfo.getName());
        request.setAttribute("entity", entity);

        return "entity_edit";
    }

    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, @PathVariable String id) throws Exception {

        EntityInfo entity = entityService.get(id);
        ApplicationInfo app = applicationService.get(entity.getApplicationId());

        entity.setApplicationText(app.getName());

        request.setAttribute("entity", entity);

        return "entity_edit";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE)
    public ResultPack del(HttpServletRequest request, String id) throws Exception {
        if(entityService.delete(id)) {
            return SUCCEED;
        }
        return FAILED;
    }

}
