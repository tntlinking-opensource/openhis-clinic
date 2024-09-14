package com.geeke.org.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.clinic.entity.ClinicVersion;
import com.geeke.common.persistence.TreeEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
/**
 * 公司管理Entity
 * @author lys
 * @version 2022-05-25
 */
public class Company extends TreeEntity<Company> {

	private static final long serialVersionUID = 41040096140492800L;
	private String fullName;		// 全称
	private String code;		// 编码
	private String addressProvince;		// 地址（省）
	private String addressCity;		// 地址（市）
	private String addressRegion;		// 地址（区）
	private String address;		// 地址
	private String phoneNumber;		// 电话
	private String licenseSubject;		// 执业许可科目
	private String fileId;		// 诊所许可证图片
	private Company lesseeId;      // 租户
	private String contactName;
	private String tenantryId; // 租户id

	public String getTenantryId() {
		return tenantryId;
	}

	public void setTenantryId(String tenantryId) {
		this.tenantryId = tenantryId;
	}

	public Company getParentId() {
		return parentId;
	}

	public void setParentId(Company parentId) {
		this.parentId = parentId;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	private Company parentId;    //zuhu id
	private ClinicVersion version;      // 诊所版本
	private Integer status;		// 状态 1启用 0停用
	private Integer isInstitution;		// 是否为机构 1是 0不是
	private Date startUseDate;		// 诊所启用时间
	private Date expireDate;		// 诊所到期时间

	
	public Company() {
		super();
	}

	public Company(String id){
		super(id);
	}
	

	@Length(min=1, max=512, message="全称长度必须介于 1 和 512 之间")
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	
	@Length(min=1, max=64, message="编码长度必须介于 1 和 64 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	@Length(min=1, max=10, message="地址（省）长度必须介于 1 和 10 之间")
	public String getAddressProvince() {
		return addressProvince;
	}
	public void setAddressProvince(String addressProvince) {
		this.addressProvince = addressProvince;
	}
	
	
	@Length(min=1, max=10, message="地址（市）长度必须介于 1 和 10 之间")
	public String getAddressCity() {
		return addressCity;
	}
	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}
	
	
	@Length(min=1, max=10, message="地址（区）长度必须介于 1 和 10 之间")
	public String getAddressRegion() {
		return addressRegion;
	}
	public void setAddressRegion(String addressRegion) {
		this.addressRegion = addressRegion;
	}
	
	
	@Length(min=1, max=100, message="地址长度必须介于 1 和 100 之间")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	@Length(min=1, max=21, message="电话长度必须介于 1 和 21 之间")
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
	@Length(min=0, max=100, message="执业许可科目长度必须介于 0 和 100 之间")
	public String getLicenseSubject() {
		return licenseSubject;
	}
	public void setLicenseSubject(String licenseSubject) {
		this.licenseSubject = licenseSubject;
	}
	
	
	@Length(min=0, max=21, message="诊所许可证图片长度必须介于 0 和 21 之间")
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	
    public ClinicVersion getVersion() {
        return version;
    }

    public void setVersion(ClinicVersion version) {
        this.version = version;
    }
    
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getIsInstitution() {
		return isInstitution;
	}
	public void setIsInstitution(Integer isInstitution) {
		this.isInstitution = isInstitution;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getStartUseDate() {
		return startUseDate;
	}
	public void setStartUseDate(Date startUseDate) {
		this.startUseDate = startUseDate;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	@NotNull(message="租户不能为空")
	public Company getLesseeId() {
		return lesseeId;
	}

	public void setLesseeId(Company lesseeId) {
		this.lesseeId = lesseeId;
	}
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "org_company";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "41040096140492800";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}
