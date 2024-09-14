package com.geeke.sys.entity;

import com.geeke.sys.entity.DictType;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 字典类型Entity
 * @author lys
 * @version 2021-12-07
 */
public class DictItem extends DataEntity<DictItem> {

	private static final long serialVersionUID = 4005L;
	private DictType dictType;      // 字典类型表ID 
	private String value;		// 值

	
	public DictItem() {
		super();
	}

	public DictItem(String id){
		super(id);
	}
	
	public DictItem(DictType dictType){
		this.dictType = dictType;
	}

	@NotNull(message="字典类型表ID不能为空")
    public DictType getDictType() {
        return dictType;
    }

    public void setDictType(DictType dictType) {
        this.dictType = dictType;
    }
    
	@Length(min=1, max=128, message="值长度必须介于 1 和 128 之间")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_dict_item";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4005";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}