package com.geeke.org.entity;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 租户管理Entity
 * @author txl
 * @version 2022-05-23
 */
public class Lessee extends DataEntity<Lessee> {

	private static final long serialVersionUID = 985180319911059468L;
	private String userName;		// 租户名
	private String phoneNumber;		// 电话号码
	private String company;		// 公司名称
	private String contactName;		// 联系人姓名
	private String addressProvince;		// 地址（省）
	private String addressCity;		// 地址（市）
	private String addressRegion;		// 地址（区）
	private String address;		// 联系地址
	private String isUse;		// 状态

	
	public Lessee() {
		super();
	}

	public Lessee(String id){
		super(id);
	}
	

	@Length(min=1, max=100, message="租户名长度必须介于 1 和 100 之间")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	@Length(min=1, max=20, message="电话号码长度必须介于 1 和 20 之间")
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	@Length(min=0, max=50, message="联系人姓名长度必须介于 0 和 50 之间")
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	
	@Length(min=0, max=10, message="地址（省）长度必须介于 0 和 10 之间")
	public String getAddressProvince() {
		return addressProvince;
	}
	public void setAddressProvince(String addressProvince) {
		this.addressProvince = addressProvince;
	}
	
	
	@Length(min=0, max=10, message="地址（市）长度必须介于 0 和 10 之间")
	public String getAddressCity() {
		return addressCity;
	}
	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}
	
	
	@Length(min=0, max=10, message="地址（区）长度必须介于 0 和 10 之间")
	public String getAddressRegion() {
		return addressRegion;
	}
	public void setAddressRegion(String addressRegion) {
		this.addressRegion = addressRegion;
	}
	
	
	@Length(min=0, max=100, message="联系地址长度必须介于 0 和 100 之间")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	@Length(min=1, max=1, message="状态长度必须介于 1 和 1 之间")
	public String getIsUse() {
		return isUse;
	}
	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "lessee";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "985180319911059468";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}