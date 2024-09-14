package com.geeke.collect.entity;

import com.geeke.admin.entity.User;
import javax.validation.constraints.NotNull;
import com.geeke.admin.entity.Router;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 收藏夹Entity
 * @author szy
 * @version 2021-07-28
 */
public class SysCollect extends DataEntity<SysCollect> {

	private static final long serialVersionUID = 1L;
	private User userId;      // 用户id 
	private Router routerId;      // 路由id 
    private Long sort;

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    public SysCollect() {
		super();
	}

	public SysCollect(String id){
		super(id);
	}
	

	@NotNull(message="用户id不能为空")
    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }
    
	@NotNull(message="路由id不能为空")
    public Router getRouterId() {
        return routerId;
    }

    public void setRouterId(Router routerId) {
        this.routerId = routerId;
    }
    
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_collect";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "549885676523610115";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}