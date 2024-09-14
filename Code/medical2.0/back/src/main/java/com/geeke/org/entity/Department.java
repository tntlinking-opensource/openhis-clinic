package com.geeke.org.entity;

import com.geeke.org.entity.Company;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.geeke.admin.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.TreeEntity;
/**
 * 部门管理Entity
 * @author lys
 * @version 2021-07-13
 */
public class Department extends TreeEntity<Department> {

	private static final long serialVersionUID = 1L;
	private Company company;      // 公司 
	private String code;		// 编码
	private User director;      // 总监 
	private User manager;      // 经理 

	
	public Department() {
		super();
	}

	public Department(String id){
		super(id);
	}
	

	public Department(Company company){
		this.company = company;
	}

	@NotNull(message="公司不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=1, max=10, message="编码长度必须介于 1 和 10 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
    public User getDirector() {
        return director;
    }

    public void setDirector(User director) {
        this.director = director;
    }
    
    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }
    
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "org_department";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "52676365136650250";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}