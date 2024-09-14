package com.geeke.org.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 行政区域划分Entity
 * @author txl
 * @version 2022-06-21
 */
public class AdministrativeDivision extends DataEntity<AdministrativeDivision> {

	private static final long serialVersionUID = 986666876811870222L;
    private AdministrativeDivision parten;      // 上级区划代码 	        
	private Integer level;		// 区划级别

	
	public AdministrativeDivision() {
		super();
	}

	public AdministrativeDivision(String id){
		super(id);
	}
	

	public AdministrativeDivision getParten() {
		return parten;
	}

	public void setParten(AdministrativeDivision parten) {
		this.parten = parten;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "administrative_division";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "986666876811870222";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return false;
    }
}