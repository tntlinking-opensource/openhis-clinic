package com.geeke.outpatient.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.admin.entity.User;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 病历填写Entity
 * @author txl
 * @version 2022-06-13
 */
public class MedicalRecord extends DataEntity<MedicalRecord> {

	private static final long serialVersionUID = 1014786989773332526L;
	private Company company;      // 诊所id 
	private String diagnosisId;      // 远程诊断id
	private User doctor;      // 医生id
	private Registration registration;      // 挂号id
	private String patientTell;		// 主诉
	private String nowHistory;		// 现病史
	private String beforeHistory;		// 既往史
	private String diagnose;		// 诊断
	private String personalHistory;		// 个人史
	private String allergyHistory;		// 过敏史
	private String diseaseHistory;		// 疾病史
	private String pregnancyHistory;		// 婚孕史
	private String infectiousHistory;		// 传染病史
	private String lunariaHistory;		// 月经史
	private String familyHistory;		// 家族史
	private String surgeryHistory;		// 手术史
	private String transfusionHistory;		// 输血史
	private String physiqueCheck;		// 体格检查
	private String assistCheck;		// 辅助检查
	private String emergencyDiagnose;		// 急诊诊断
	private String emergencyEffect;		// 急诊效果
	private String otherCheck;		// 其他检查
	private String handlingSituation;		// 处理情况
	private String healthEducation;		// 个体化健康教育
	private String epidemicDisease;		// 流行病学史
	private Date diagnoseDate;		// 诊断时间
	private String doctorAdvice;		// 医嘱事项
	private BigDecimal fee;		// 总费用
	private String fileId;		// 上传附件
	private String westernDiagnose; //西医诊断
	private String chinaDiagnose; //中医诊断
	private String medic; // 远程诊疗医生
	private String department; // 远程诊疗医院
	private String register; // 挂号

	public String getDiagnosisId() {
		return diagnosisId;
	}

	public void setDiagnosisId(String diagnosisId) {
		this.diagnosisId = diagnosisId;
	}

	public String getMedic() {
		return medic;
	}

	public void setMedic(String medic) {
		this.medic = medic;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getRegister() {
		return register;
	}

	public void setRegister(String register) {
		this.register = register;
	}

	public String getWesternDiagnose() {
		return westernDiagnose;
	}

	public void setWesternDiagnose(String westernDiagnose) {
		this.westernDiagnose = westernDiagnose;
	}

	public String getChinaDiagnose() {
		return chinaDiagnose;
	}

	public void setChinaDiagnose(String chinaDiagnose) {
		this.chinaDiagnose = chinaDiagnose;
	}
	
	public MedicalRecord() {
		super();
	}

	public MedicalRecord(String id){
		super(id);
	}
	
	public MedicalRecord(Registration registration){
		this.registration = registration;
	}

	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@NotNull(message="医生id不能为空")
    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }
    
	@NotNull(message="挂号id不能为空")
    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }
    
	@Length(min=1, max=128, message="主诉长度必须介于 1 和 128 之间")
	public String getPatientTell() {
		return patientTell;
	}
	public void setPatientTell(String patientTell) {
		this.patientTell = patientTell;
	}
	
	
	@Length(min=0, max=128, message="现病史长度必须介于 0 和 128 之间")
	public String getNowHistory() {
		return nowHistory;
	}
	public void setNowHistory(String nowHistory) {
		this.nowHistory = nowHistory;
	}
	
	
	@Length(min=0, max=128, message="既往史长度必须介于 0 和 128 之间")
	public String getBeforeHistory() {
		return beforeHistory;
	}
	public void setBeforeHistory(String beforeHistory) {
		this.beforeHistory = beforeHistory;
	}
	
	
	@Length(min=1, max=128, message="诊断长度必须介于 1 和 128 之间")
	public String getDiagnose() {
		return diagnose;
	}
	public void setDiagnose(String diagnose) {
		this.diagnose = diagnose;
	}
	
	
	@Length(min=0, max=128, message="个人史长度必须介于 0 和 128 之间")
	public String getPersonalHistory() {
		return personalHistory;
	}
	public void setPersonalHistory(String personalHistory) {
		this.personalHistory = personalHistory;
	}
	
	
	@Length(min=0, max=128, message="过敏史长度必须介于 0 和 128 之间")
	public String getAllergyHistory() {
		return allergyHistory;
	}
	public void setAllergyHistory(String allergyHistory) {
		this.allergyHistory = allergyHistory;
	}
	
	
	@Length(min=0, max=128, message="疾病史长度必须介于 0 和 128 之间")
	public String getDiseaseHistory() {
		return diseaseHistory;
	}
	public void setDiseaseHistory(String diseaseHistory) {
		this.diseaseHistory = diseaseHistory;
	}
	
	
	@Length(min=0, max=128, message="婚孕史长度必须介于 0 和 128 之间")
	public String getPregnancyHistory() {
		return pregnancyHistory;
	}
	public void setPregnancyHistory(String pregnancyHistory) {
		this.pregnancyHistory = pregnancyHistory;
	}
	
	
	@Length(min=0, max=128, message="传染病史长度必须介于 0 和 128 之间")
	public String getInfectiousHistory() {
		return infectiousHistory;
	}
	public void setInfectiousHistory(String infectiousHistory) {
		this.infectiousHistory = infectiousHistory;
	}
	
	
	@Length(min=0, max=128, message="月经史长度必须介于 0 和 128 之间")
	public String getLunariaHistory() {
		return lunariaHistory;
	}
	public void setLunariaHistory(String lunariaHistory) {
		this.lunariaHistory = lunariaHistory;
	}
	
	
	@Length(min=0, max=128, message="家族史长度必须介于 0 和 128 之间")
	public String getFamilyHistory() {
		return familyHistory;
	}
	public void setFamilyHistory(String familyHistory) {
		this.familyHistory = familyHistory;
	}
	
	
	@Length(min=0, max=128, message="手术史长度必须介于 0 和 128 之间")
	public String getSurgeryHistory() {
		return surgeryHistory;
	}
	public void setSurgeryHistory(String surgeryHistory) {
		this.surgeryHistory = surgeryHistory;
	}
	
	
	@Length(min=0, max=128, message="输血史长度必须介于 0 和 128 之间")
	public String getTransfusionHistory() {
		return transfusionHistory;
	}
	public void setTransfusionHistory(String transfusionHistory) {
		this.transfusionHistory = transfusionHistory;
	}
	
	
	@Length(min=0, max=128, message="体格检查长度必须介于 0 和 128 之间")
	public String getPhysiqueCheck() {
		return physiqueCheck;
	}
	public void setPhysiqueCheck(String physiqueCheck) {
		this.physiqueCheck = physiqueCheck;
	}
	
	
	@Length(min=0, max=128, message="辅助检查长度必须介于 0 和 128 之间")
	public String getAssistCheck() {
		return assistCheck;
	}
	public void setAssistCheck(String assistCheck) {
		this.assistCheck = assistCheck;
	}
	
	
	@Length(min=0, max=128, message="急诊诊断长度必须介于 0 和 128 之间")
	public String getEmergencyDiagnose() {
		return emergencyDiagnose;
	}
	public void setEmergencyDiagnose(String emergencyDiagnose) {
		this.emergencyDiagnose = emergencyDiagnose;
	}
	
	
	@Length(min=0, max=128, message="急诊效果长度必须介于 0 和 128 之间")
	public String getEmergencyEffect() {
		return emergencyEffect;
	}
	public void setEmergencyEffect(String emergencyEffect) {
		this.emergencyEffect = emergencyEffect;
	}
	
	
	@Length(min=0, max=128, message="其他检查长度必须介于 0 和 128 之间")
	public String getOtherCheck() {
		return otherCheck;
	}
	public void setOtherCheck(String otherCheck) {
		this.otherCheck = otherCheck;
	}

	@Length(min=0, max=128, message="处理情况长度必须介于 0 和 128 之间")
	public String getHandlingSituation() {
		return handlingSituation;
	}
	public void setHandlingSituation(String handlingSituation) {
		this.handlingSituation = handlingSituation;
	}

	@Length(min=0, max=128, message="个体化健康教育长度必须介于 0 和 128 之间")
	public String getHealthEducation() {
		return healthEducation;
	}
	public void setHealthEducation(String healthEducation) {
		this.healthEducation = healthEducation;
	}

	@Length(min=0, max=128, message="流行病学史长度必须介于 0 和 128 之间")
	public String getEpidemicDisease() {
		return epidemicDisease;
	}
	public void setEpidemicDisease(String epidemicDisease) {
		this.epidemicDisease = epidemicDisease;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getDiagnoseDate() {
		return diagnoseDate;
	}
	public void setDiagnoseDate(Date diagnoseDate) {
		this.diagnoseDate = diagnoseDate;
	}
	
	
	@Length(min=0, max=128, message="医嘱事项长度必须介于 0 和 128 之间")
	public String getDoctorAdvice() {
		return doctorAdvice;
	}
	public void setDoctorAdvice(String doctorAdvice) {
		this.doctorAdvice = doctorAdvice;
	}
	
	
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	
	
	@Length(min=0, max=45, message="上传附件长度必须介于 0 和 45 之间")
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "medical_record";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1014786989773332526";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }


}