package org.erhun.framework.rbac.controller;

import org.erhun.framework.basic.servlet.session.SessionConstants;
import org.erhun.framework.domain.controller.AbstractUserController;
import org.erhun.framework.domain.entities.BaseEntity;
import org.erhun.framework.orm.dto.BaseDTO;
import org.erhun.framework.rbac.RbacUser;

import java.io.Serializable;

/**
 *
 * @author weichao<gorilla@aliyun.com>
 * 
 */
public abstract class AbstractRbacUserController<Id extends Serializable, E extends BaseEntity<Id>, D extends BaseDTO> extends AbstractUserController{


    protected RbacUser getRbacUser(){
        return (RbacUser) getRequest().getSession().getAttribute(SessionConstants.LOGIN_USER);
    }

}
