package org.erhun.framework.rbac.entities;


import org.erhun.framework.domain.entities.AbstractBizEntity;
import org.erhun.framework.orm.annotations.Ignore;
import org.erhun.framework.orm.annotations.Table;
import org.erhun.framework.orm.annotations.Value;
import org.erhun.framework.orm.annotations.validator.Duplicate;

/**
 * 
 * @author weichao<groilla@aliyun.com>
 *
 */
@Duplicate({@Value({"code", "moduleId"})})
@Table(value = "t_rbac_function",alias = "功能")
public class FunctionInfo extends AbstractBizEntity<String> {

    private String moduleId;
    
    @Ignore
    private String moduleText;
    
	private String name;
	
	private String icon;
	
	/**
	 * new 新增、edit 编辑、remove 删除 、query 查询、preview 预览、save 保存
	 */
    private String type;

    private String url;
    
    private String showMode;
    
    private String style;
    
    private String css;
    
    private String html;
    
    private String formId;
	
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getShowMode() {
        return showMode;
    }

    public void setShowMode(String showMode) {
        this.showMode = showMode;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleText() {
        return moduleText;
    }

    public void setModuleText(String moduleText) {
        this.moduleText = moduleText;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
