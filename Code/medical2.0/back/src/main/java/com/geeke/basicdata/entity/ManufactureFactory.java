package com.geeke.basicdata.entity;

import com.geeke.org.entity.Company;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 生产厂家Entity
 * @author txl
 * @version 2022-06-22
 */
public class ManufactureFactory extends DataEntity<ManufactureFactory> {

	private static final long serialVersionUID = 1016206064147988493L;
	private Company company;      // 诊所id 
	private String people;		// 联系人
	private String phone;		// 电话
	private String province;		// 省
	private String city;		// 市
	private String area;		// 区
	private String address;		// 地址
	private String status;      //启用标志
	private String type;       //厂家类型标志

	
	public ManufactureFactory() {
		super();
	}

	public ManufactureFactory(String id){
		super(id);
	}
	

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=0, max=45, message="联系人长度必须介于 0 和 45 之间")
	public String getPeople() {
		return people;
	}
	public void setPeople(String people) {
		this.people = people;
	}
	
	
	@Length(min=0, max=45, message="电话长度必须介于 0 和 45 之间")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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

	@Length(min=0, max=45, message="地址长度必须介于 0 和 45 之间")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "manufacture_factory";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1016206064147988493";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}