package com.geeke.toll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.admin.entity.User;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.MedicalRecord;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
/**
 * 收费管理Entity
 * @author lc
 * @version 2022-06-22
 */
public class TollInfo extends DataEntity<TollInfo> {

	private static final long serialVersionUID = 1024846610635145502L;
    private String jgid;
    private String jgmc;
	private Company company;      // 诊所id 
	private String department;      // 科室
	private String doseUnit;      // 剂量
	private RecipelInfo recipel;      // 处方
	private BigDecimal amountReceivable;		// 应收(退)金额
	private BigDecimal amountReceived;		// 实收金额
	private BigDecimal amountDiscounted;		// 优惠金额
	private Float discount;		// 折扣额度 
	private DictItem paymentType;      // 支付类型
	private DictItem amountStatus;      // 单据状态，参考枚举 
	private String tollNumber;		// 收费单号
	private DictItem tollType;      // 收费类型，参考枚举 
	private Patient patient;      // 患者 
	private MedicalRecord medical;      // 病例 
    private User doctor;	// 开单科室/医生
    private RecipelDetail recipelDetail;	// 收费项目，药品/材料/诊疗名称&数量
    private String registrationFeeId;   //挂号费记录登记信息id
    private int returnType;            //收费单退费标记
	
	public TollInfo() {
		super();
	}

	public TollInfo(String id){
		super(id);
	}

    public String getDoseUnit() {
        return doseUnit;
    }

    public void setDoseUnit(String doseUnit) {
        this.doseUnit = doseUnit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
    public RecipelInfo getRecipel() {
        return recipel;
    }

    public void setRecipel(RecipelInfo recipel) {
        this.recipel = recipel;
    }
    
	public BigDecimal getAmountReceivable() {
		return amountReceivable;
	}
	public void setAmountReceivable(BigDecimal amountReceivable) {
		this.amountReceivable = amountReceivable;
	}
	
	
	public BigDecimal getAmountReceived() {
		return amountReceived;
	}
	public void setAmountReceived(BigDecimal amountReceived) {
		this.amountReceived = amountReceived;
	}
	
	
	public BigDecimal getAmountDiscounted() {
		return amountDiscounted;
	}
	public void setAmountDiscounted(BigDecimal amountDiscounted) {
		this.amountDiscounted = amountDiscounted;
	}
	
	
	public Float getDiscount() {
		return discount;
	}
	public void setDiscount(Float discount) {
		this.discount = discount;
	}
	
	
	@NotNull(message="支付类型不能为空")
    public DictItem getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(DictItem paymentType) {
        this.paymentType = paymentType;
    }
    
	@NotNull(message="单据状态，参考枚举不能为空")
    public DictItem getAmountStatus() {
        return amountStatus;
    }

    public void setAmountStatus(DictItem amountStatus) {
        this.amountStatus = amountStatus;
    }
    
	@Length(min=1, max=64, message="收费单号长度必须介于 1 和 64 之间")
	public String getTollNumber() {
		return tollNumber;
	}
	public void setTollNumber(String tollNumber) {
		this.tollNumber = tollNumber;
	}
	
	
	@NotNull(message="收费类型，参考枚举不能为空")
    public DictItem getTollType() {
        return tollType;
    }

    public void setTollType(DictItem tollType) {
        this.tollType = tollType;
    }
    
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public MedicalRecord getMedical() {
        return medical;
    }

    public void setMedical(MedicalRecord medical) {
        this.medical = medical;
    }



    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public RecipelDetail getRecipelDetail() {
        return recipelDetail;
    }

    public void setRecipelDetail(RecipelDetail recipelDetail) {
        this.recipelDetail = recipelDetail;
    }

    public String getRegistrationFeeId() {
        return registrationFeeId;
    }

    public void setRegistrationFeeId(String registrationFeeId) {
        this.registrationFeeId = registrationFeeId;
    }

    public int getReturnType() {
        return returnType;
    }

    public void setReturnType(int returnType) {
        this.returnType = returnType;
    }

    /**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "toll_info";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1024846610635145502";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}