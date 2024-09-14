package com.geeke.admin.common.entity;



/**
 * 数据权限类
 * 
 * @author lys
 * @date 2020/6/24
 */
public final class DataPermission implements java.io.Serializable {
	private static final long serialVersionUID = 397454118479080301L;

	private String id;
	private String routerId;
	private String roleId;
	private String metaId;	// 源数据Id
	private String conditions;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getRouterId() {
		return routerId;
	}
	public void setRouterId(String routerId) {
		this.routerId = routerId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getMetaId() {
		return metaId;
	}
	public void setMetaId(String metaId) {
		this.metaId = metaId;
	}
	
	public String getConditions() {
		return conditions;
	}
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}
	
}
