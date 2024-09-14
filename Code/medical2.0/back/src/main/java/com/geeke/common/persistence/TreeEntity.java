package com.geeke.common.persistence;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geeke.utils.StringUtils;


/**
 * 数据Entity类
 * @author lys
 * @version 2018-05-16
 */
public abstract class TreeEntity<T> extends DataEntity<T> {

	private static final long serialVersionUID = 1L;

	protected T parent;	// 父级
	protected String ids; // 全编号
	protected Integer sort;		// 排序
	protected List<T> children;		//子节点
	
	public TreeEntity() {
		super();
	}
	
	public TreeEntity(String id) {
		super(id);
	}
	
	
	/**
	 * 插入之前执行方法，需要手动调用
	 */
	@Override
	public void preInsert(){
		super.preInsert();
		
		TreeEntity<T> treeParent = (TreeEntity<T>)parent;
		if(!StringUtils.isBlank(treeParent.getIds())) {
			setIds(treeParent.getIds()  + id + ".");
		} else {
			setIds(id + ".");
		}		
	}

	/**
	 * 更新之前执行方法，需要手动调用
	 */
	@Override
	public void preUpdate(){
		super.preUpdate();
		
		TreeEntity<T> treeParent = (TreeEntity<T>)parent;
		if(!StringUtils.isBlank(treeParent.getIds())) {
			setIds(treeParent.getIds()  + id + ".");
		} else {
			setIds(id + ".");
		}	
	}	
	
	//@JsonBackReference
	@NotNull(message="父级标识不能为空")
	public T getParent() {
		return parent;
	}

	public void setParent(T parent) {
		this.parent = parent;
	}	

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	@JsonInclude(value=JsonInclude.Include.NON_NULL) 
	public List<T> getChildren() {
		return children;
	}

	@JsonDeserialize
	public void setChildren(List<T> children) {
		this.children = children;
	}
	
	/*
	 * 客户端selecttree组件  需要的lable字段
	 */
	public String getLabel() {
		return this.getName();
	}
}
