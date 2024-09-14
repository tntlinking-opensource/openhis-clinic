package com.geeke.sys.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 系统主题Entity
 * @author lys
 * @version 2021-09-19
 */
public class PersonalTheme extends DataEntity<PersonalTheme> {

	private static final long serialVersionUID = 532290982221258937L;
    @JsonSerialize(using= ToStringSerializer.class)
	private Long userId;		// 用户
	private String theme;		// 主题

	
	public PersonalTheme() {
		super();
	}

	public PersonalTheme(String id){
		super(id);
	}
	

	@NotNull(message="用户不能为空")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	@Length(min=1, max=2000, message="主题长度必须介于 1 和 2000 之间")
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_personal_theme";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "532290982221258937";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}