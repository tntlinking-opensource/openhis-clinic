package com.geeke.sys.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 系统主题Entity
 * @author lys
 * @version 2021-09-19
 */
public class Theme extends DataEntity<Theme> {

	private static final long serialVersionUID = 532039984332456179L;
	private String theme;		// 主题
	private String isDefault;		// 默认

	
	public Theme() {
		super();
	}

	public Theme(String id){
		super(id);
	}
	

	@Length(min=1, max=2000, message="主题长度必须介于 1 和 2000 之间")
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	
	@Length(min=0, max=1, message="默认长度必须介于 0 和 1 之间")
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_theme";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "532039984332456179";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}