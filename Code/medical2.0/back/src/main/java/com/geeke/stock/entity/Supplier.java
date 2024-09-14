package com.geeke.stock.entity;

import com.geeke.org.entity.Company;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 供应商管理Entity
 * @author txl
 * @version 2022-06-22
 */
public class Supplier extends DataEntity<Supplier> {

	private static final long serialVersionUID = 1005526731044757538L;
	private Company company;      // 诊所id 
	private String linkman;		// 联系人
	private String phone;		// 电话
	private String mail;		// 邮箱
	private String province;		// 省
	private String city;		// 市
	private String creditCode;		// 信用代码
	private String area;		// 区
	private String address;		// 地址
	private String status;      //是否启用
	private String type;		//供应商类型标志
	private String pinyinCode;  //拼音码
	
	public Supplier() {
		super();
	}

	public Supplier(String id){
		super(id);
	}
	

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=0, max=45, message="联系人长度必须介于 0 和 45 之间")
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	
	
	@Length(min=0, max=45, message="电话长度必须介于 0 和 45 之间")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	@Length(min=0, max=128, message="邮箱长度必须介于 0 和 128 之间")
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	
	@Length(min=0, max=45, message="省长度必须介于 0 和 45 之间")
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	
	
	@Length(min=0, max=45, message="市长度必须介于 0 和 45 之间")
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	
	@Length(min=0, max=128, message="信用代码长度必须介于 0 和 128 之间")
	public String getCreditCode() {
		return creditCode;
	}
	public void setCreditCode(String creditCode) {
		this.creditCode = creditCode;
	}
	
	
	@Length(min=0, max=45, message="区长度必须介于 0 和 45 之间")
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	
	
	@Length(min=0, max=128, message="地址长度必须介于 0 和 128 之间")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
		this.status=status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPinyinCode() {
		return pinyinCode;
	}

	public void setPinyinCode(String pinyinCode) {
		this.pinyinCode = pinyinCode;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "supplier";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1005526731044757538";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}