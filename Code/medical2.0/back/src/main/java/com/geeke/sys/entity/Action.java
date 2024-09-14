package com.geeke.sys.entity;

import com.geeke.sys.entity.DictItem;
import com.geeke.gen.entity.GenTable;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;
import com.google.common.collect.Lists;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 操作日志Entity
 * @author lys
 * @version 2019-12-11
 */
public class Action extends DataEntity<Action> {

	private static final long serialVersionUID = 1L;
	private DictItem actionType;      // 操作 
	private GenTable objectType;      // 对象类型 
	private String objectId;		// 业务对象
	private String objectName;		// 对象名称
	private String status;		// 还原

	private List<ActionRecycle> actionRecycleList = Lists.newArrayList();		// 子表列表
	
	public Action() {
		super();
	}

	public Action(String id){
		super(id);
	}
	

    public DictItem getActionType() {
        return actionType;
    }

    public void setActionType(DictItem actionType) {
        this.actionType = actionType;
    }
    
    public GenTable getObjectType() {
        return objectType;
    }

    public void setObjectType(GenTable objectType) {
        this.objectType = objectType;
    }
    
	@Length(min=0, max=40, message="业务对象长度必须介于 0 和 40 之间")
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	
	@Length(min=0, max=512, message="对象名称长度必须介于 0 和 512 之间")
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	
	@Length(min=1, max=1, message="还原长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	public List<ActionRecycle> getActionRecycleList() {
		return actionRecycleList;
	}

	public void setActionRecycleList(List<ActionRecycle> actionRecycleList) {
		this.actionRecycleList = actionRecycleList;
	}
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_action";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4000";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}