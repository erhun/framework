package com.erhun.framework.domain.controller;

import com.erhun.framework.basic.utils.ResultPack;
import com.erhun.framework.domain.entities.BaseEntity;
import com.erhun.framework.domain.services.IBusinessService;
import com.erhun.framework.orm.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 *
 * @author weichao<gorilla@aliyun.com>
 * 
 */
public abstract class AbstractBusinessController <Id extends Serializable, E extends BaseEntity<Id>> extends BaseController {

    @Autowired
    private IBusinessService<Id, E> businessService;

    /**
     *
     * @param queryParam
     * @return
     */
    @GetMapping()
    public ResultPack list(QueryParam queryParam){
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
    public ResultPack save(E entity){
        businessService.save(entity);
        return ResultPack.SUCCEED;
    }

    /**
     *
     * @param entity
     * @return
     */
    @PutMapping()
    public ResultPack update(E entity){
        businessService.save(entity);
        return ResultPack.SUCCEED;
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping
    public ResultPack delete(Id id){
        businessService.delete(id);
        return ResultPack.SUCCEED;
    }

}
