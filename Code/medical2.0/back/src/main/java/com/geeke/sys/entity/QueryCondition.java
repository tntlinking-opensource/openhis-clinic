package com.geeke.sys.entity;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 查询条件Entity
 * @author lys
 * @version 2021-07-05
 */
public class QueryCondition extends DataEntity<QueryCondition> {

	private static final long serialVersionUID = 1L;
	@JsonSerialize(using= ToStringSerializer.class)
	private Long userId;		// 用户Id
	@JsonSerialize(using= ToStringSerializer.class)
	private Long routerId;		// 路由Id
	private String conditions;		// 条件

	
	public QueryCondition() {
		super();
	}

	public QueryCondition(String id){
		super(id);
	}
	

	@NotNull(message="用户Id不能为空")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	@NotNull(message="路由Id不能为空")
	public Long getRouterId() {
		return routerId;
	}
	public void setRouterId(Long routerId) {
		this.routerId = routerId;
	}
	
	
	@Length(min=1, max=4000, message="条件长度必须介于 1 和 4000 之间")
	public String getConditions() {
		return conditions;
	}
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_condition";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "515882179918733313";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}