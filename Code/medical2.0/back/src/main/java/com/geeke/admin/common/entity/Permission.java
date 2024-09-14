package com.geeke.admin.common.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * 权限dto类
 * 
 * @author Lining
 * @date 2018/2/27
 */
public final class Permission implements java.io.Serializable {
	private static final long serialVersionUID = -8394651001749623325L;
	
	public static final String ROUTER_TYPE = "router";
	public static final String RESOURCE_TYPE = "resource";

	private String id;
	private String name;
	private String type;
	private String parentId;
	private String code;
	private List<Permission> children;
	private String key;
	
	private String[] routerIds;
    private String[] resourceIds;
    private List<DataPermission> dataPermissions;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@JsonIgnore
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	@JsonInclude(value=JsonInclude.Include.NON_NULL) 
	public List<Permission> getChildren() {
		return children;
	}
	
	@JsonDeserialize
	public void setChildren(List<Permission> children) {
		this.children = children;
	}
	
	@JsonInclude(value=JsonInclude.Include.NON_NULL) 
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	@JsonIgnore
	public String[] getRouterIds() {
		return routerIds;
	}
	@JsonDeserialize
	public void setRouterIds(String[] routerIds) {
		this.routerIds = routerIds;
	}
	
	@JsonIgnore
	public String[] getResourceIds() {
		return resourceIds;
	}
	@JsonDeserialize
	public void setResourceIds(String[] resourceIds) {
		this.resourceIds = resourceIds;
	}
	@JsonIgnore
	public List<DataPermission> getDataPermissions() {
		return dataPermissions;
	}
	@JsonDeserialize
	public void setDataPermissions(List<DataPermission> dataPemissions) {
		this.dataPermissions = dataPemissions;
	}
	
	
}
