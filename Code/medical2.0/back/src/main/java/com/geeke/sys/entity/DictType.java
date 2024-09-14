package com.geeke.sys.entity;

import org.hibernate.validator.constraints.Length;
import java.util.List;
import com.google.common.collect.Lists;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 字典类型Entity
 * @author lys
 * @version 2021-12-07
 */
public class DictType extends DataEntity<DictType> {

	private static final long serialVersionUID = 4002L;
	private String code;		// 字典类型编码
	private String isSystem;		// 系统级

	private List<DictItem> dictItemList = Lists.newArrayList();		// 子表列表
	
	public DictType() {
		super();
	}

	public DictType(String id){
		super(id);
	}
	

	@Length(min=1, max=64, message="字典类型编码长度必须介于 1 和 64 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	@Length(min=1, max=1, message="系统级长度必须介于 1 和 1 之间")
	public String getIsSystem() {
		return isSystem;
	}
	public void setIsSystem(String isSystem) {
		this.isSystem = isSystem;
	}
	
	
	
	public List<DictItem> getDictItemList() {
		return dictItemList;
	}

	public void setDictItemList(List<DictItem> dictItemList) {
		this.dictItemList = dictItemList;
	}
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_dict_type";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4002";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}