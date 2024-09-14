package com.geeke.toll.entity;

import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.toll.entity.TollInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 收费管理Entity
 * @author lc
 * @version 2022-06-15
 */
public class TollDetail extends DataEntity<TollDetail> {

	private static final long serialVersionUID = 1028225822184669188L;
	private RecipelInfo recipel;      // 处方 
	private TollInfo tollId;      // 收费主表 

	
	public TollDetail() {
		super();
	}

	public TollDetail(String id){
		super(id);
	}
	
	public TollDetail(TollInfo tollId){
		this.tollId = tollId;
	}

    public RecipelInfo getRecipel() {
        return recipel;
    }

    public void setRecipel(RecipelInfo recipel) {
        this.recipel = recipel;
    }
    
    public TollInfo getTollId() {
        return tollId;
    }

    public void setTollId(TollInfo tollId) {
        this.tollId = tollId;
    }
    
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "toll_detail";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1028225822184669188";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return false;
    }
}