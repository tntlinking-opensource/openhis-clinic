package com.geeke.grid.entity;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 自定义布局Entity
 * @author ycy
 * @version 2021-08-02
 */
public class UserGrid extends DataEntity<UserGrid> {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using= ToStringSerializer.class)
	private Long userId;		// 用户id
	@JsonSerialize(using= ToStringSerializer.class)
	private Long routerId;		//路由id
	private String gridInfo;		// 自定义布局信息
	private String defaultCheck;		// 选中展示的模块

	
	public UserGrid() {
		super();
	}

	public UserGrid(String id){
		super(id);
	}
	

	@NotNull(message="用户id不能为空")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	@Length(min=0, max=1000, message="自定义布局信息长度必须介于 0 和 1000 之间")
	public String getGridInfo() {
		return gridInfo;
	}
	public void setGridInfo(String gridInfo) {
		this.gridInfo = gridInfo;
	}
	
	
	@Length(min=0, max=200, message="选中展示的模块长度必须介于 0 和 200 之间")
	public String getDefaultCheck() {
		return defaultCheck;
	}
	public void setDefaultCheck(String defaultCheck) {
		this.defaultCheck = defaultCheck;
	}


	public Long getRouterId() {
		return routerId;
	}

	public void setRouterId(Long routerId) {
		this.routerId = routerId;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "user_grid";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "570619064204943377";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}