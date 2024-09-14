package com.geeke.admin.entity;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.TreeEntity;
/**
 * 路由管理Entity
 * @author lys
 * @version 2021-11-18
 */
public class Router extends TreeEntity<Router> {

	private static final long serialVersionUID = 4003L;
	private String code;		// 代码
	private String url;		// URL
	private String canPermission;		// 能否分配
	private Integer isLocked;		// 禁用
	private String properties;		// 扩展属性

	
	public Router() {
		super();
	}

	public Router(String id){
		super(id);
	}
	

	@Length(min=0, max=64, message="代码长度必须介于 0 和 64 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	@Length(min=0, max=128, message="URL长度必须介于 0 和 128 之间")
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	@Length(min=1, max=1, message="能否分配长度必须介于 1 和 1 之间")
	public String getCanPermission() {
		return canPermission;
	}
	public void setCanPermission(String canPermission) {
		this.canPermission = canPermission;
	}
	
	
	public Integer getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Integer isLocked) {
		this.isLocked = isLocked;
	}
	
	
	@Length(min=1, max=1024, message="扩展属性长度必须介于 1 和 1024 之间")
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_router";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4003";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}