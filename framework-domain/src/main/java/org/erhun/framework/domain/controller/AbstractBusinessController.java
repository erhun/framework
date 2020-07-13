package org.erhun.framework.domain.controller;

import org.erhun.framework.basic.utils.ResultPack;
import org.erhun.framework.domain.entities.BaseEntity;
import org.erhun.framework.domain.services.IBusinessService;
import org.erhun.framework.orm.dto.BaseDTO;
import org.erhun.framework.orm.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 *
 * @author weichao<gorilla@aliyun.com>
 *
 */
public abstract class AbstractBusinessController <Id extends Serializable, E extends BaseEntity<Id>, D extends BaseDTO> extends AbstractUserController {

    @Autowired
    protected IBusinessService<Id, E> businessService;

    /**
     *
     * @param queryParam
     * @return
     */
    @GetMapping()
    public ResultPack list(QueryParam <D> queryParam){
        return businessService.queryByPage(queryParam);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResultPack get(@PathVariable("id") Id id){
        return ResultPack.succeed().model(businessService.get(id));
    }

    /**
     *
     * @param entity
     * @return
     */
    @PostMapping()
    public ResultPack save(@RequestBody E entity){
        businessService.save(entity);
        return ResultPack.SUCCEED;
    }

    /**
     *
     * @param entity
     * @return
     */
    @PutMapping()
    public ResultPack update(@RequestBody E entity){
        businessService.save(entity);
        return ResultPack.SUCCEED;
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public ResultPack delete(@PathVariable String id){
        businessService.deleteAll(id);
        return ResultPack.SUCCEED;
    }


}
