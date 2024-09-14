package com.geeke.common.persistence;

import java.util.Date;

import com.geeke.org.entity.Company;
import org.hibernate.validator.constraints.Length;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.geeke.common.IUser;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.utils.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * 数据Entity类
 * @author lys
 * @version 2014-05-16
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class DataEntity<T> extends BaseEntity<T> {

	private static final long serialVersionUID = 1L;

	protected Company company;      // 诊所ID
	protected String name;		//名称
	protected String remarks;	// 备注
	protected String createBy;	// 创建者
	protected Date createDate;	// 创建日期
	protected String updateBy;	// 更新者
	protected Date updateDate;	// 更新日期
	protected String delFlag; 	// 删除标记（0：正常；1：删除；2：审核）

	public DataEntity() {
		super();
		this.delFlag = DEL_FLAG_NORMAL;
	}
	
	public DataEntity(String id) {
		super(id);
	}
	
	/**
	 * 插入之前执行方法，需要手动调用
	 */
	@Override
	@JsonIgnore
	public void preInsert(){
		super.preInsert();
		JSONObject userObj = SessionUtils.getUserJson();
		if (userObj != null && StringUtils.isNotBlank(userObj.getString("id"))){
			this.updateBy = userObj.getString("name") + "(" + userObj.getString("loginName") + ")";
			this.createBy = userObj.getString("name") + "(" + userObj.getString("loginName") + ")";
		}
		this.updateDate = new Date();
		this.createDate = this.updateDate;
	}

	/**
	 * 更新之前执行方法，需要手动调用
	 */
	@Override
	@JsonIgnore
	public void preUpdate(){
		super.preUpdate();
		JSONObject userObj = SessionUtils.getUserJson();
		if (userObj != null && StringUtils.isNotBlank(userObj.getString("id"))){
			this.updateBy = userObj.getString("name") + "(" + userObj.getString("loginName") + ")";
		}
		this.updateDate = new Date();
	}

	@NotNull(message="诊所ID不能为空")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	@Length(min=1, max=100, message="名称长度必须介于 1 和 64 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}		
	
	@Length(min=0, max=255)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@JsonIgnore
	@Length(min=1, max=1)
	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

}
