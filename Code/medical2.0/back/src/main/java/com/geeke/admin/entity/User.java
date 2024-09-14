package com.geeke.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geeke.common.IUser;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.org.entity.Department;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
/**
 * 用户管理Entity
 * @author lys
 * @version 2021-08-25
 */
@Component
public class User extends DataEntity<User> implements IUser {

	private static final long serialVersionUID = 1L;
	private Company company;      // 公司 
	private Department department;      // 部门 
	private String loginName;		// 登录名
	private String status;		// 登录状态
	private String loginDuration;		// 登录时长
	private boolean  loginPasswordUpdate; // 是否修改 密码
	private String loginPassword;		// 密码
	private Integer isLocked;		// 禁用
	private String phone;		// 手机号
	private String email;		// 邮箱
	private String qyOpenId;		// 企业openid
	private List<String> clinics;  // 诊所id
	private UserExt userExt; //用户的扩展信息
	private String office; //所属科室
	private String isWxuser="0";  //是否为微信患者用户

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLoginDuration() {
		return loginDuration;
	}

	public void setLoginDuration(String loginDuration) {
		this.loginDuration = loginDuration;
	}

	public UserExt getUserExt() {
		return userExt;
	}

	public void setUserExt(UserExt userExt) {
		this.userExt = userExt;
	}

	public List<String> getClinics() {
		return clinics;
	}

	public void setClinics(List<String> clinics) {
		this.clinics = clinics;
	}

	public Integer getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}

	private Integer isAdmin;


	private List<UserRole> userRoleList = Lists.newArrayList();		// 子表列表
	
	public User() {
		super();
	}

	public User(String id){
		super(id);
	}
	

	public User(Company company){
		this.company = company;
	}

	@NotNull(message="公司不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@NotNull(message="部门不能为空")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
    
	@Length(min=1, max=32, message="登录名长度必须介于 1 和 32 之间")
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	
	@Length(min=1, max=64, message="密码长度必须介于 1 和 64 之间")
	@JsonIgnore
	public String getLoginPassword() {
		return loginPassword;
	}
    @JsonDeserialize
	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	
	public boolean getLoginPasswordUpdate() {
		return loginPasswordUpdate;
	}
	
	public void setLoginPasswordUpdate(boolean loginPasswordUpdate) {
		this.loginPasswordUpdate = loginPasswordUpdate;
	}	
	
	public Integer getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Integer isLocked) {
		this.isLocked = isLocked;
	}
	@Length(min=0, max=20, message="手机号长度必须介于 0 和 20 之间")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}


	@Length(min=0, max=100, message="邮箱长度必须介于 0 和 100 之间")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}


	@Length(min=0, max=100, message="企业openid长度必须介于 0 和 100 之间")
	public String getQyOpenId() {
		return qyOpenId;
	}
	public void setQyOpenId(String qyOpenId) {
		this.qyOpenId = qyOpenId;
	}


	public List<UserRole> getUserRoleList() {
		return userRoleList;
	}

	public void setUserRoleList(List<UserRole> userRoleList) {
		this.userRoleList = userRoleList;
	}


	public String getIsWxuser() {
		return isWxuser;
	}

	public void setIsWxuser(String isWxuser) {
		this.isWxuser = isWxuser;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_user";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4004";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }

	@Override
	public String getCompanyId() {
		if(company != null) {
			return company.getId();
		}
		
		return null;
	}
}