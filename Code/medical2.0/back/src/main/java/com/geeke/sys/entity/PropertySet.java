package com.geeke.sys.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 属性集管理Entity
 * @author lys
 * @version 2021-12-26
 */
public class PropertySet extends DataEntity<PropertySet> {

	private static final long serialVersionUID = 773152267153252816L;
	private String propertiesDef;		// 属性定义

	
	public PropertySet() {
		super();
	}

	public PropertySet(String id){
		super(id);
	}
	

	public String getPropertiesDef() {
		return propertiesDef;
	}
	public void setPropertiesDef(String propertiesDef) {
		this.propertiesDef = propertiesDef;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_property_set";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "773152267153252816";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}