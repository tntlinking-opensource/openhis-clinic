package com.geeke.outpatient.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.admin.entity.User;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.ClinicOffice;
import com.geeke.org.entity.Company;
import com.geeke.org.entity.Department;
import com.geeke.sys.entity.DictItem;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 挂号信息Entity
 * @author txl
 * @version 2022-06-15
 */
@Data
public class Registration extends DataEntity<Registration> {

	private static final long serialVersionUID = 1008534118685450402L;
	private Company company;      // 诊所id 
	private ClinicOffice clinicOffice;      // 科室
	private Patient patientId;      // 患者 
	private User doctor;      // 医生 
	private DictItem treatType;      // 治疗类型 
	private DictItem infectType;      // 传染病
	private Date receptionStartDate;		// 接诊开始时间
	private DictItem source;      // 来源 
	private Date receptionEndDate;		// 接诊结束时间
	private String pulse;		// 脉搏
	private String breathe;		// 呼吸
	private String temperature;		// 体温
	private String isGoHigharea;		// 是否去过疫区
	private String highArea;		// 疫区地点
	private String freeRegistrationFee;		// 免挂号费
	private BigDecimal registrationFee;		// 挂号费
	private DictItem payType;      // 支付方式
	private DictItem status;      // 状态 
	private String dispensingStatus;		// 发药状态
	private Date dispensingDate;		// 发/退药时间
	private String chargeStatus;		// 收费状态        0- 待收费   1-部分收费   2- 已收费   3-  部分退费    4- 已退费
	private Date chargeDate;		// 收费时间
	private DictItem refundRegistrationPayType;		// 退挂号费支付方式
	private String refundRegistrationRemarks;		// 退挂号费备注
	private  BigDecimal practicleRegistrationFee;   //实际支付挂号费
	private String practicalRegistrationPayRemarks;	//实际支付挂号费备注
	private String guardianName;      //监护人姓名
	private String guardianPhone;     //监护人电话
	private String workplace;     //工作单位
	private String nation;     //民族
	private String card;     //身份证号
	private String occupation;     //职业
	private Date signedDate;		// 签到时间
	private Date exitNumberDate;		// 退号时间
	private Date followUpDate;		// 复诊时间
	private Date payDate;		// 支付时间

	private String recipeStatus;//所有处方作废的状态显示

	private String bloodPressure;  //血压

	private Date subscribeDate;   //预约时间
	private String subscribeProject; //预约项目

	private Date retreatsDate;    //退费时间
	private Date returnDate;     //退药时间
	private Date morbidityTime;     //发病时间

	private Date treatmentDate;  //诊疗完成时间
	private String overlook;//是否忽略
     /**医保字段**/
	private  String mdtrtId ;// 医保接口唯一流水号
	//暂定11普通门诊 12 门诊挂号 13 急诊
    private  DictItem medType; //医疗类别
	private  DictItem cardType; //就诊凭证类型
	private  String iptOtpNo;// 住院/门诊号 院内唯一流水
	private  String mdtrtCertNo;//就诊凭证号 03社保卡时才填写
	private  String setlId;//医保结算id

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public Date getSignedDate() {
		return signedDate;
	}

	public void setSignedDate(Date signedDate) {
		this.signedDate = signedDate;
	}

	public Date getExitNumberDate() {
		return exitNumberDate;
	}

	public void setExitNumberDate(Date exitNumberDate) {
		this.exitNumberDate = exitNumberDate;
	}

	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}


	public Registration() {
		super();
	}

	public Registration(String id){
		super(id);
	}

	public Registration(Patient patientId){
		this.patientId = patientId;
	}

	public DictItem getRefundRegistrationPayType() {
		return refundRegistrationPayType;
	}

	public void setRefundRegistrationPayType(DictItem refundRegistrationPayType) {
		this.refundRegistrationPayType = refundRegistrationPayType;
	}

	public String getRefundRegistrationRemarks() {
		return refundRegistrationRemarks;
	}

	public void setRefundRegistrationRemarks(String refundRegistrationRemarks) {
		this.refundRegistrationRemarks = refundRegistrationRemarks;
	}

	public BigDecimal getPracticleRegistrationFee() {
		return practicleRegistrationFee;
	}

	public void setPracticleRegistrationFee(BigDecimal practicleRegistrationFee) {
		this.practicleRegistrationFee = practicleRegistrationFee;
	}

	public String getPracticalRegistrationPayRemarks() {
		return practicalRegistrationPayRemarks;
	}

	public void setPracticalRegistrationPayRemarks(String practicalRegistrationPayRemarks) {
		this.practicalRegistrationPayRemarks = practicalRegistrationPayRemarks;
	}

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

	public ClinicOffice getClinicOffice() {
		return clinicOffice;
	}

	public void setClinicOffice(ClinicOffice clinicOffice) {
		this.clinicOffice = clinicOffice;
	}

	public Patient getPatientId() {
        return patientId;
    }

    public void setPatientId(Patient patientId) {
        this.patientId = patientId;
    }
    
    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }
    
    public DictItem getTreatType() {
        return treatType;
    }

    public void setTreatType(DictItem treatType) {
        this.treatType = treatType;
    }

    public DictItem getInfectType() {
        return infectType;
    }

    public void setInfectType(DictItem infectType) {
        this.infectType = infectType;
    }
    
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getReceptionStartDate() {
		return receptionStartDate;
	}
	public void setReceptionStartDate(Date receptionStartDate) {
		this.receptionStartDate = receptionStartDate;
	}
	
	
    public DictItem getSource() {
        return source;
    }

    public void setSource(DictItem source) {
        this.source = source;
    }
    
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getReceptionEndDate() {
		return receptionEndDate;
	}
	public void setReceptionEndDate(Date receptionEndDate) {
		this.receptionEndDate = receptionEndDate;
	}
	
	
	@Length(min=0, max=45, message="脉搏长度必须介于 0 和 45 之间")
	public String getPulse() {
		return pulse;
	}
	public void setPulse(String pulse) {
		this.pulse = pulse;
	}
	
	
	@Length(min=0, max=45, message="呼吸长度必须介于 0 和 45 之间")
	public String getBreathe() {
		return breathe;
	}
	public void setBreathe(String breathe) {
		this.breathe = breathe;
	}
	
	
	@Length(min=0, max=45, message="体温长度必须介于 0 和 45 之间")
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	
	
	@Length(min=0, max=64, message="是否去过疫区长度必须介于 0 和 64 之间")
	public String getIsGoHigharea() {
		return isGoHigharea;
	}
	public void setIsGoHigharea(String isGoHigharea) {
		this.isGoHigharea = isGoHigharea;
	}
	
	
	@Length(min=0, max=128, message="疫区地点长度必须介于 0 和 128 之间")
	public String getHighArea() {
		return highArea;
	}
	public void setHighArea(String highArea) {
		this.highArea = highArea;
	}
	
	
	@Length(min=0, max=64, message="免挂号费长度必须介于 0 和 64 之间")
	public String getFreeRegistrationFee() {
		return freeRegistrationFee;
	}
	public void setFreeRegistrationFee(String freeRegistrationFee) {
		this.freeRegistrationFee = freeRegistrationFee;
	}
	
	
	public BigDecimal getRegistrationFee() {
		return registrationFee;
	}
	public void setRegistrationFee(BigDecimal registrationFee) {
		this.registrationFee = registrationFee;
	}


    public DictItem getPayType() {
        return payType;
    }

    public void setPayType(DictItem payType) {
        this.payType = payType;
    }
    
    public DictItem getStatus() {
        return status;
    }

    public void setStatus(DictItem status) {
        this.status = status;
    }
    
	@Length(min=0, max=1, message="发药状态长度必须介于 0 和 1 之间")
	public String getDispensingStatus() {
		return dispensingStatus;
	}
	public void setDispensingStatus(String dispensingStatus) {
		this.dispensingStatus = dispensingStatus;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getDispensingDate() {
		return dispensingDate;
	}
	public void setDispensingDate(Date dispensingDate) {
		this.dispensingDate = dispensingDate;
	}
	
	
	@Length(min=0, max=1, message="收费状态长度必须介于 0 和 1 之间")
	public String getChargeStatus() {
		return chargeStatus;
	}
	public void setChargeStatus(String chargeStatus) {
		this.chargeStatus = chargeStatus;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getChargeDate() {
		return chargeDate;
	}
	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}

	public String getGuardianName() {
		return guardianName;
	}

	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

	public String getGuardianPhone() {
		return guardianPhone;
	}

	public void setGuardianPhone(String guardianPhone) {
		this.guardianPhone = guardianPhone;
	}

	public String getWorkplace() {
		return workplace;
	}

	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getRecipeStatus() {
		return recipeStatus;
	}

	public void setRecipeStatus(String recipeStatus) {
		this.recipeStatus = recipeStatus;
	}

	public String getBloodPressure() {
		return bloodPressure;
	}

	public void setBloodPressure(String bloodPressure) {
		this.bloodPressure = bloodPressure;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date getSubscribeDate() {
		return subscribeDate;
	}

	public void setSubscribeDate(Date subscribeDate) {
		this.subscribeDate = subscribeDate;
	}

	public String getSubscribeProject() {
		return subscribeProject;
	}

	public void setSubscribeProject(String subscribeProject) {
		this.subscribeProject = subscribeProject;
	}

	public Date getretreatsDate() {
		return retreatsDate;
	}

	public void setretreatsDate(Date retreatsDate) {
		this.retreatsDate = retreatsDate;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public Date getMorbidityTime() {
		return morbidityTime;
	}

	public void setMorbidityTime(Date morbidityTime) {
		this.morbidityTime = morbidityTime;
	}


	public Date getTreatmentDate() {
		return treatmentDate;
	}

	public void setTreatmentDate(Date treatmentDate) {
		this.treatmentDate = treatmentDate;
	}

	public String getOverlook(){return overlook;}

	public void setOverlook(String overlook){this.overlook=overlook;}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "registration";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1008534118685450402";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}