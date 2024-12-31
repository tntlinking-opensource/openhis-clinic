package com.geeke.org.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.clinic.entity.ClinicVersion;
import com.geeke.common.persistence.DataEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
/**
 * 诊所管理Entity
 * @author txl
 * @version 2022-05-19
 */
@Data
public class Clinic extends DataEntity<Clinic> {

	private static final long serialVersionUID = 986498806151577752L;
	private Lessee lesseeId;      // 租户
	private String addressProvince;		// 地址(省)
	private String addressCity;		// 地址(市)
	private String addressRegion;		// 地址(区)
	private String address;		// 地址
	private String phoneNumber;		// 电话
	private String licenseSubject;		// 执业许可科目
	private String fileId;		// 诊所许可证图片
	private ClinicVersion version;      // 产品版本 
	private Integer status;		// 状态
	private Integer isInstitution;		// 是否为机构
	private Date startUseDate;		// 诊所开始时间
	private Date expireDate;		// 诊所到期时间

	private  String fixmedinsCode;// 定点医疗机构代码

	
	public Clinic() {
		super();
	}

	public Clinic(String id){
		super(id);
	}
	
	public Clinic(Lessee lesseeId){
		this.lesseeId = lesseeId;
	}

	@NotNull(message="租户不能为空")
    public Lessee getLesseeId() {
        return lesseeId;
    }

    public void setLesseeId(Lessee lesseeId) {
        this.lesseeId = lesseeId;
    }
    
	@Length(min=0, max=10, message="地址(省)长度必须介于 0 和 10 之间")
	public String getAddressProvince() {
		return addressProvince;
	}
	public void setAddressProvince(String addressProvince) {
		this.addressProvince = addressProvince;
	}
	
	
	@Length(min=0, max=10, message="地址(市)长度必须介于 0 和 10 之间")
	public String getAddressCity() {
		return addressCity;
	}
	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}
	
	
	@Length(min=0, max=10, message="地址(区)长度必须介于 0 和 10 之间")
	public String getAddressRegion() {
		return addressRegion;
	}
	public void setAddressRegion(String addressRegion) {
		this.addressRegion = addressRegion;
	}
	
	
	@Length(min=0, max=100, message="地址长度必须介于 0 和 100 之间")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	@Length(min=1, max=20, message="电话长度必须介于 1 和 20 之间")
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
	
	
	@NotNull(message="产品版本不能为空")
    public ClinicVersion getVersion() {
        return version;
    }

    public void setVersion(ClinicVersion version) {
        this.version = version;
    }
    
	@NotNull(message="状态不能为空")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	@NotNull(message="是否为机构不能为空")
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
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "clinic";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "986498806151577752";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}