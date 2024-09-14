package com.geeke.cure.entity;

import com.geeke.org.entity.Company;
import javax.validation.constraints.NotNull;

import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.outpatient.entity.Registration;
import com.geeke.treatment.entity.CostItem;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 检验检查Entity
 * @author rys
 * @version 2022-10-18
 */
public class InspectionCheck extends DataEntity<InspectionCheck> {

	private static final long serialVersionUID = 1212102786706588488L;
	private Company company;      // 诊所id 
	private Patient patient;		// 患者id
	private Registration registration;		// 登记信息id
	private CostItem costItem;		// 诊疗项目id
	private String type;		// 类型（0：检验；1：检查）
	private String status;		// 填写状态（0：未填写；1：已填写）
	private String patientName;		// 患者姓名
	private String sex;		// 性别
	private String phone;		// 手机号
	private String diagnose;		// 诊断
	private String completeBy;		// 开单医生
	private Date completeDate;		// 开单时间
	private RecipelInfo recipelInfo;  //处方
	private RecipelDetail recipelDetail;  //处方详情

	
	public InspectionCheck() {
		super();
	}

	public InspectionCheck(String id){
		super(id);
	}
	

	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Registration getRegistration() {
		return registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
	}

	public CostItem getCostItem() {
		return costItem;
	}

	public void setCostItem(CostItem costItem) {
		this.costItem = costItem;
	}

	@Length(min=1, max=1, message="类型（0：检验；1：检查）长度必须介于 1 和 1 之间")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	@Length(min=1, max=1, message="填写状态（0：未填写；1：已填写）长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	@Length(min=0, max=100, message="患者姓名长度必须介于 0 和 100 之间")
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
	
	@Length(min=0, max=100, message="性别长度必须介于 0 和 100 之间")
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	
	@Length(min=0, max=100, message="手机号长度必须介于 0 和 100 之间")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	@Length(min=0, max=100, message="诊断长度必须介于 0 和 100 之间")
	public String getDiagnose() {
		return diagnose;
	}
	public void setDiagnose(String diagnose) {
		this.diagnose = diagnose;
	}
	
	
	@Length(min=0, max=100, message="开单医生长度必须介于 0 和 100 之间")
	public String getCompleteBy() {
		return completeBy;
	}
	public void setCompleteBy(String completeBy) {
		this.completeBy = completeBy;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCompleteDate() {
		return completeDate;
	}
	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public RecipelInfo getRecipelInfo() {
		return recipelInfo;
	}

	public void setRecipelInfo(RecipelInfo recipelInfo) {
		this.recipelInfo = recipelInfo;
	}

	public RecipelDetail getRecipelDetail() {
		return recipelDetail;
	}

	public void setRecipelDetail(RecipelDetail recipelDetail) {
		this.recipelDetail = recipelDetail;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "inspection_check";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1212102786706588488";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}