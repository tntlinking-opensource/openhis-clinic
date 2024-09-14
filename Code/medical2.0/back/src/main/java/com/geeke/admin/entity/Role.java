package com.geeke.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
/**
 * 角色管理Entity
 * @author lys
 * @version 2022-05-24
 */
public class Role extends DataEntity<Role> {

	private static final long serialVersionUID = 4012L;
	private Company company;      // 公司 
	private String code;		// 代码
	private Integer isLocked;		// 禁用

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	private String parentId;  //父类id

	public Role() {
		super();
	}

	public Role(String id){
		super(id);
	}
	
	public Role(Company company){
		this.company = company;
	}

	@NotNull(message="公司不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=1, max=32, message="代码长度必须介于 1 和 32 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	public Integer getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Integer isLocked) {
		this.isLocked = isLocked;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_role";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4012";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}