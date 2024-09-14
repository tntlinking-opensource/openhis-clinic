package com.geeke.schedule.entity;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 定时任务管理Entity
 * @author shenzy
 * @version 2021-10-28
 */
public class ScheduleJob extends DataEntity<ScheduleJob> {

	private static final long serialVersionUID = 683898740740005891L;
	private String code;		// 任务编号
	private String cron;		// 任务表达式
	private String status;		// 任务状态
	private String isLock;		// 是否禁用

	
	public ScheduleJob() {
		super();
	}

	public ScheduleJob(String id){
		super(id);
	}
	

	@Length(min=1, max=100, message="任务编号长度必须介于 1 和 100 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	@Length(min=1, max=30, message="任务表达式长度必须介于 1 和 30 之间")
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	
	
	@Length(min=0, max=100, message="任务状态长度必须介于 0 和 100 之间")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	@Length(min=0, max=1, message="是否禁用长度必须介于 0 和 1 之间")
	public String getIsLock() {
		return isLock;
	}
	public void setIsLock(String isLock) {
		this.isLock = isLock;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "schedule_job";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "683898740740005891";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}