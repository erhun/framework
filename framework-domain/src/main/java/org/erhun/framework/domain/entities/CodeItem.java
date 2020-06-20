package org.erhun.framework.domain.entities;


import org.erhun.framework.orm.annotations.Table;
import org.erhun.framework.orm.entities.IOrderEntity;
import org.erhun.framework.orm.entities.IVirtualDeleteEntity;

import java.util.List;


@Table("t_code_item")
public class CodeItem extends BaseEntity<String> implements IOrderEntity, IVirtualDeleteEntity {
	
	private String groupId;
	
	private String code;
	
	private String name;
	
	private String alias;
	
	private Integer showIndex;
	
	private String tag;
	
	private Integer level;
	
	private Boolean deleted;
	
	private String parent;
	
	private List<CodeItem> children;

	@Override
	public Boolean getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public Integer getShowIndex() {
		return showIndex;
	}

	@Override
	public void setShowIndex(Integer showIndex) {
		this.showIndex = showIndex;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

    public List<CodeItem> getChildren() {
        return children;
    }

    public void setChildren(List<CodeItem> children) {
        this.children = children;
    }
}
