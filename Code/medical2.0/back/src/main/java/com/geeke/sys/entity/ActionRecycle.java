package com.geeke.sys.entity;

import com.geeke.sys.entity.Action;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 操作日志Entity
 * @author lys
 * @version 2019-12-12
 */
public class ActionRecycle extends DataEntity<ActionRecycle> {

	private static final long serialVersionUID = 1L;
	private Action action;      // 日志 
	private String tableName;		// 表名
	private String objectId;		// 业务对象
	private String objectName;		// 对象名称

	
	public ActionRecycle() {
		super();
	}

	public ActionRecycle(String id){
		super(id);
	}
	

	public ActionRecycle(Action action){
		this.action = action;
	}

	@NotNull(message="日志不能为空")
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
    
	@Length(min=1, max=200, message="表名长度必须介于 1 和 200 之间")
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	
	@Length(min=1, max=40, message="业务对象长度必须介于 1 和 40 之间")
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	
	@Length(min=0, max=200, message="对象名称长度必须介于 0 和 200 之间")
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_action_recycle";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4013";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}