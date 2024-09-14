package com.geeke.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;

import javax.validation.constraints.NotNull;

/**
 * 用户管理Entity
 * @author lys
 * @version 2021-08-25
 */
public class UserRole extends DataEntity<UserRole> {

	private static final long serialVersionUID = 1L;
	private User user;      // 用户标识 
	private Role role;      // 角色标识
    private Company company; //所在租户

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }


	public UserRole() {
		super();
	}

	public UserRole(String id){
		super(id);
	}
	

	public UserRole(User user){
		this.user = user;
	}

	@NotNull(message="用户标识不能为空")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
	@NotNull(message="角色标识不能为空")
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_user_role";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4014";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}