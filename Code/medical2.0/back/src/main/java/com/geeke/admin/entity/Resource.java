package com.geeke.admin.entity;

import org.hibernate.validator.constraints.Length;
import com.geeke.admin.entity.Router;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 资源管理Entity
 * @author lys
 * @version 2021-11-18
 */
public class Resource extends DataEntity<Resource> {

	private static final long serialVersionUID = 4006L;
	private String code;		// 代码
	private String url;		// Url
	private String permission;		// 权限
	private String canPermission;		// 可授权
	private String isDefault;		// 默认权限
	private Integer isLocked;		// 禁用
	private Router router;      // 路由 

	
	public Resource() {
		super();
	}

	public Resource(String id){
		super(id);
	}
	
	public Resource(Router router){
		this.router = router;
	}

	@Length(min=1, max=64, message="代码长度必须介于 1 和 64 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	@Length(min=1, max=128, message="Url长度必须介于 1 和 128 之间")
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	@Length(min=1, max=32, message="权限长度必须介于 1 和 32 之间")
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	
	@Length(min=1, max=1, message="可授权长度必须介于 1 和 1 之间")
	public String getCanPermission() {
		return canPermission;
	}
	public void setCanPermission(String canPermission) {
		this.canPermission = canPermission;
	}
	
	
	@Length(min=1, max=1, message="默认权限长度必须介于 1 和 1 之间")
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	
	
	public Integer getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Integer isLocked) {
		this.isLocked = isLocked;
	}
	
	
	@NotNull(message="路由不能为空")
    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }
    
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_resource";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4006";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}