package com.geeke.outpatient.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.sys.entity.DictItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
/**
 * 患者信息Entity
 * @author txl
 * @version 2022-06-23
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class Patient extends DataEntity<Patient> {

	private static final long serialVersionUID = 1008489176147648530L;
	private Company company;      // 诊所id 
	private DictItem gender;      // 性别 
	private String age;		// 年龄
	private String month;		// 月
	private Date birthday;		// 出生日期
	private String phone;		// 联系方式
	private String card;		// 证件号
	private String cardType;		// 证件类型
	private DictItem withPatientNexus;      // 与患者关系 
	private String healthCardNo;		// 健康卡号
	private String province;		// 省
	private String city;		// 市
	private String area;		// 区
	private String address;		// 详细地址
	private String guardianName;		// 监护人
	private String guardianPhone;		// 监护人电话
	private String breathe;
	private String temperature;
	private String pulse;
	private String bloodPressure;
	private String openId;
	private String poverty;

	public Patient() {
		super();
	}

	public Patient(String id){
		super(id);
	}
	

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@NotNull(message="性别不能为空")
    public DictItem getGender() {
        return gender;
    }

    public void setGender(DictItem gender) {
        this.gender = gender;
    }
    
	@Length(min=1, max=45, message="年龄长度必须介于 1 和 45 之间")
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	
	
	@Length(min=1, max=45, message="月长度必须介于 1 和 45 之间")
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	
	@Length(min=1, max=45, message="联系方式长度必须介于 1 和 45 之间")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	@Length(min=1, max=45, message="身份证号长度必须介于 1 和 45 之间")
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	
	
    public DictItem getWithPatientNexus() {
        return withPatientNexus;
    }

    public void setWithPatientNexus(DictItem withPatientNexus) {
        this.withPatientNexus = withPatientNexus;
    }
    
	@Length(min=0, max=45, message="健康卡号长度必须介于 0 和 45 之间")
	public String getHealthCardNo() {
		return healthCardNo;
	}
	public void setHealthCardNo(String healthCardNo) {
		this.healthCardNo = healthCardNo;
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
	
	
	@Length(min=0, max=45, message="详细地址长度必须介于 0 和 45 之间")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	@Length(min=0, max=100, message="监护人长度必须介于 0 和 100 之间")
	public String getGuardianName(){
		return guardianName;
	}

	public void setGuardianPhone(String guardianPhone) {
		this.guardianPhone = guardianPhone;
	}

	@Length(min=0, max=100, message="监护人电话长度必须介于 0 和 100 之间")
	public String getGuardianPhone(){
		return guardianPhone;
	}

	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

	public String getBreathe() {
		return breathe;
	}

	public void setBreathe(String breathe) {
		this.breathe = breathe;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getPulse() {
		return pulse;
	}

	public void setPulse(String pulse) {
		this.pulse = pulse;
	}

	public String getBloodPressure() {
		return bloodPressure;
	}

	public void setBloodPressure(String bloodPressure) {
		this.bloodPressure = bloodPressure;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getPoverty() {
		return poverty;
	}

	public void setPoverty(String poverty) {
		this.poverty = poverty;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "patient";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1008489176147648530";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}