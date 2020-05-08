package com.erhun.framework.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * VO 视图对象，通常用于在页面显示时传递此对象给前端
 *
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class ViewObject implements Cloneable {

	private String id;
	
	//@JSONField(serialize=false)
	private Model model;

	private String operateType;

	//@JSONField(serialize=false)
	@JsonIgnore
	protected HttpServletRequest request;

	private String moduleName;
	
	//@JSONField(serialize=false)
	private List <String> operateButtons;
	
	//@JSONField(serialize=false)
	//private User user;
	
	//@JSONField(serialize=false)
	private String listPage;
	
	private String pageMode;
	
	private String showMode;
	
	public ViewObject(){

	}

	public void initialize( HttpServletRequest request ) {

		/*this.request = request;
		if(StringUtils.isEmpty(id)){
			this.id = request.getParameter("id");
		}
		this.module = (Module) request.getAttribute("module");
		this.user = (User) request.getSession().getAttribute(SessionConstants.LOGIN_USER);
		this.operateType = request.getParameter("op");*/

	}
	
	/*public <T> T getCurrentEntity() {

		if( currentEntity == null){
			try{
				currentEntity = (BaseEntity) this.entityClass.newInstance();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			ParameterUtils.copyProperties(request.getParameterMap(), currentEntity);
		}

		return (T) currentEntity;

	}*/

	public String getEntityAlias() {
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest( HttpServletRequest request ) {
		this.request = request;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName( String moduleName ) {
		this.moduleName = moduleName;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType( String operateType ) {
		this.operateType = operateType;
	}

	public List <String> getOperateButtons() {
		return operateButtons;
	}

	public void setOperateButtons( List <String> operateButtons ) {
		this.operateButtons = operateButtons;
	}

	public String getListPage() {
		return listPage;
	}

	public void setListPage( String listPage ) {
		this.listPage = listPage;
	}

	public String getPageMode() {
		return pageMode;
	}

	public void setPageMode( String pageMode ) {
		this.pageMode = pageMode;
	}

	public String getShowMode() {
		return showMode;
	}

	public void setShowMode( String showMode ) {
		this.showMode = showMode;
	}

	/*public BaseEntity getEntity() {
		if( entity == null && moduleSetting.getEntityClass() != null){
			try{
				entity = (BaseEntity)  moduleSetting.getEntityClass().newInstance();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			ParameterUtils.copyProperties(request.getParameterMap(), entity);
		}

		return entity;
	}*/

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
