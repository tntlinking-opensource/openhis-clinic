package com.geeke.cure.entity;

import com.geeke.org.entity.Company;
import javax.validation.constraints.NotNull;
import com.geeke.cure.entity.InspectionCheck;
import com.geeke.treatment.entity.CostItems;
import org.hibernate.validator.constraints.Length;
import com.geeke.outpatient.entity.Patient;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 检验检查明细Entity
 * @author rys
 * @version 2022-10-19
 */
public class InspectionCheckInfo extends DataEntity<InspectionCheckInfo> {

	private static final long serialVersionUID = 1213629183723971377L;
	private Company company;      // 诊所id 
	private InspectionCheck inspectionCheck;      // 主表id 
	private String fileId;		// 文件id
	private Patient patient;      // 患者id 
	private Date samplingDate;		// 采样时间
	private String samplingNum;		// 样本号
	private String instrument;		// 检验仪器
	private String checkoutNum;		// 检验号
	private Date checkoutDate;		// 检验时间
	private String checkoutOffice;		// 检验科室
	private String reportConclusion;		// 报告结论
	private String checkType;          //检查类型
	private String checkPart;         //检查部位
	private String checkAdvise;      //检查所见
	private String opinion;      //意见
	private String doctor;      //医师
	private String checkProject;   //检查项目
	private List<CostItems> costItemsList;  //诊疗项目
	private List<InspectionCheckDetail> inspectionCheckDetails;

	public InspectionCheckInfo() {
		super();
	}

	public InspectionCheckInfo(String id){
		super(id);
	}
	

	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@NotNull(message="主表id不能为空")
    public InspectionCheck getInspectionCheck() {
        return inspectionCheck;
    }

    public void setInspectionCheck(InspectionCheck inspectionCheck) {
        this.inspectionCheck = inspectionCheck;
    }
    
	@Length(min=0, max=20, message="文件id长度必须介于 0 和 20 之间")
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	
	@NotNull(message="患者id不能为空")
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSamplingDate() {
		return samplingDate;
	}
	public void setSamplingDate(Date samplingDate) {
		this.samplingDate = samplingDate;
	}
	
	
	@Length(min=0, max=100, message="样本号长度必须介于 0 和 100 之间")
	public String getSamplingNum() {
		return samplingNum;
	}
	public void setSamplingNum(String samplingNum) {
		this.samplingNum = samplingNum;
	}
	
	
	@Length(min=0, max=100, message="检验仪器长度必须介于 0 和 100 之间")
	public String getInstrument() {
		return instrument;
	}
	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}
	
	
	@Length(min=0, max=100, message="检验号长度必须介于 0 和 100 之间")
	public String getCheckoutNum() {
		return checkoutNum;
	}
	public void setCheckoutNum(String checkoutNum) {
		this.checkoutNum = checkoutNum;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCheckoutDate() {
		return checkoutDate;
	}
	public void setCheckoutDate(Date checkoutDate) {
		this.checkoutDate = checkoutDate;
	}
	
	
	@Length(min=0, max=100, message="检验科室长度必须介于 0 和 100 之间")
	public String getCheckoutOffice() {
		return checkoutOffice;
	}
	public void setCheckoutOffice(String checkoutOffice) {
		this.checkoutOffice = checkoutOffice;
	}
	
	
	@Length(min=0, max=255, message="报告结论长度必须介于 0 和 255 之间")
	public String getReportConclusion() {
		return reportConclusion;
	}
	public void setReportConclusion(String reportConclusion) {
		this.reportConclusion = reportConclusion;
	}

	public List<CostItems> getCostItemsList() {
		return costItemsList;
	}

	public void setCostItemsList(List<CostItems> costItemsList) {
		this.costItemsList = costItemsList;
	}

	public List<InspectionCheckDetail> getInspectionCheckDetails() {
		return inspectionCheckDetails;
	}

	public void setInspectionCheckDetails(List<InspectionCheckDetail> inspectionCheckDetails) {
		this.inspectionCheckDetails = inspectionCheckDetails;
	}

	public String getCheckAdvise() {
		return checkAdvise;
	}

	public void setCheckAdvise(String checkAdvise) {
		this.checkAdvise = checkAdvise;
	}

	public String getCheckPart() {
		return checkPart;
	}

	public void setCheckPart(String checkPart) {
		this.checkPart = checkPart;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getDoctor() {
		return doctor;
	}

	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}

	public String getCheckProject() {
		return checkProject;
	}

	public void setCheckProject(String checkProject) {
		this.checkProject = checkProject;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "inspection_check_info";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1213629183723971377";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return false;
    }
}