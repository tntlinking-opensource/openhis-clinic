package com.geeke.outpatient.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 处方信息Entity
 * @author txl
 * @version 2022-07-20
 */
public class RecipelInfo extends DataEntity<RecipelInfo> {

	private static final long serialVersionUID = 1014474470772900008L;
	private Company company;      // 诊所id
	private Registration registration;      // 登记信息（对应ID）
	private DictItem recipelType;      // 处方类别
	private String dosage;		// 剂量
	private DictItem recipelUse;      // 用法
	private DictItem frequency;      // 频次用量
	private DictItem takeFrequency;      // 服用频次
	private String singleDosage;		// 单次用量
	private BigDecimal fee;		// 处方总价
	private BigDecimal amountReceivedTotal;		// 实收金额合计
	private String isPay;		// 是否付款
	private Date payDate;		// 付款时间
	private String isDispension;		// 是否发药
	private Date dispensionDate;		// 发药时间
	private Integer seq;		// 排序
	private DictItem smallType;		// 小类小类【字典数据】
	private String entrust;		// 医嘱
	private Integer status;		// 基本状态（1：开具完成；-1：  作废处方）
	private Integer chargeStatus;		// 收费状态（0：待收费；1：已收费 ；-1：已退费）
	private Integer dispensionStatus;		// 发药状态（0：待发药；1： 已发药 ；-1：已退药）
	private Date retreatChargeDate;		// 退费时间
	private Date retreatDispensionDate;		// 退药时间
	private Date invalidDate;		// 作废时间
	private Integer isFollowUp;		// 是否复诊处方
	private Date followUpDate;		// 复诊时间
	private String payUser;		// 付款操作人
	private String retreatChargeUser;		// 退费操作人
	private String retreatDispensionUser;		// 退药操作人
	private String dispensionUser;		// 发药操作人
	private String code;		// 处方编号
	private Date retreatDate;   //退费时间
	private String drippingSpeed;  //输液滴速
	private DictItem infusionDay;    //输液天数
	private DictItem infusionFrequency;   //输液频次
	private DictItem infusionUse;			//输液用法
	private int cureType;            //诊疗项目完成标志
	private int infuseType;          //输液执行完成标志
	private Boolean chronicDisease;  //是否慢病标志
	private Integer notShow; //目前针对门诊-工作台-不合格处方查询，是否显示 0 不显示 1 显示
	private String diagnosisId; // 远程诊疗记录id
	private String chinessNotes; //中药备注

	public String getDiagnosisId() {
		return diagnosisId;
	}

	public void setDiagnosisId(String diagnosisId) {
		this.diagnosisId = diagnosisId;
	}

	public String getChinessNotes() {
		return chinessNotes;
	}

	public void setChinessNotes(String chinessNotes) {
		this.chinessNotes = chinessNotes;
	}

	public Integer getNotShow() {
		return notShow;
	}

	public void setNotShow(Integer notShow) {
		this.notShow = notShow;
	}

	public Boolean getChronicDisease() {
		return chronicDisease;
	}

	public void setChronicDisease(Boolean chronicDisease) {
		this.chronicDisease = chronicDisease;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public RecipelInfo() {
		super();
	}

	public RecipelInfo(String id){
		super(id);
	}


	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Registration getRegistration() {
		return registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
	}

	public DictItem getRecipelType() {
		return recipelType;
	}

	public void setRecipelType(DictItem recipelType) {
		this.recipelType = recipelType;
	}

	@Length(min=0, max=45, message="剂量长度必须介于 0 和 45 之间")
	public String getDosage() {
		return dosage;
	}
	public void setDosage(String dosage) {
		this.dosage = dosage;
	}


	public DictItem getRecipelUse() {
		return recipelUse;
	}

	public void setRecipelUse(DictItem recipelUse) {
		this.recipelUse = recipelUse;
	}

	public DictItem getFrequency() {
		return frequency;
	}

	public void setFrequency(DictItem frequency) {
		this.frequency = frequency;
	}

	public DictItem getTakeFrequency() {
		return takeFrequency;
	}

	public void setTakeFrequency(DictItem takeFrequency) {
		this.takeFrequency = takeFrequency;
	}

	@Length(min=0, max=11, message="单次用量长度必须介于 0 和 11 之间")
	public String getSingleDosage() {
		return singleDosage;
	}
	public void setSingleDosage(String singleDosage) {
		this.singleDosage = singleDosage;
	}


	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getAmountReceivedTotal() {
		return amountReceivedTotal;
	}

	public void setAmountReceivedTotal(BigDecimal amountReceivedTotal) {
		this.amountReceivedTotal = amountReceivedTotal;
	}

	@Length(min=0, max=1, message="是否付款长度必须介于 0 和 1 之间")
	public String getIsPay() {
		return isPay;
	}
	public void setIsPay(String isPay) {
		this.isPay = isPay;
	}


	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}


	@Length(min=0, max=1, message="是否发药长度必须介于 0 和 1 之间")
	public String getIsDispension() {
		return isDispension;
	}
	public void setIsDispension(String isDispension) {
		this.isDispension = isDispension;
	}


	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getDispensionDate() {
		return dispensionDate;
	}
	public void setDispensionDate(Date dispensionDate) {
		this.dispensionDate = dispensionDate;
	}


	@NotNull(message="排序不能为空")
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@Length(min=1, max=255, message="小类小类【字典数据】长度必须介于 1 和 255 之间")
	public DictItem getSmallType() {
		return smallType;
	}
	public void setSmallType(DictItem smallType) {
		this.smallType = smallType;
	}


	@Length(min=0, max=4000, message="医嘱长度必须介于 0 和 4000 之间")
	public String getEntrust() {
		return entrust;
	}
	public void setEntrust(String entrust) {
		this.entrust = entrust;
	}


	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}


	public Integer getChargeStatus() {
		return chargeStatus;
	}
	public void setChargeStatus(Integer chargeStatus) {
		this.chargeStatus = chargeStatus;
	}


	public Integer getDispensionStatus() {
		return dispensionStatus;
	}
	public void setDispensionStatus(Integer dispensionStatus) {
		this.dispensionStatus = dispensionStatus;
	}


	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRetreatChargeDate() {
		return retreatChargeDate;
	}
	public void setRetreatChargeDate(Date retreatChargeDate) {
		this.retreatChargeDate = retreatChargeDate;
	}


	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRetreatDispensionDate() {
		return retreatDispensionDate;
	}
	public void setRetreatDispensionDate(Date retreatDispensionDate) {
		this.retreatDispensionDate = retreatDispensionDate;
	}


	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getInvalidDate() {
		return invalidDate;
	}
	public void setInvalidDate(Date invalidDate) {
		this.invalidDate = invalidDate;
	}


	@NotNull(message="是否复诊处方不能为空")
	public Integer getIsFollowUp() {
		return isFollowUp;
	}
	public void setIsFollowUp(Integer isFollowUp) {
		this.isFollowUp = isFollowUp;
	}


	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getFollowUpDate() {
		return followUpDate;
	}
	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}


	@Length(min=0, max=100, message="付款操作人长度必须介于 0 和 100 之间")
	public String getPayUser() {
		return payUser;
	}
	public void setPayUser(String payUser) {
		this.payUser = payUser;
	}


	@Length(min=0, max=100, message="退费操作人长度必须介于 0 和 100 之间")
	public String getRetreatChargeUser() {
		return retreatChargeUser;
	}
	public void setRetreatChargeUser(String retreatChargeUser) {
		this.retreatChargeUser = retreatChargeUser;
	}


	@Length(min=0, max=100, message="退药操作人长度必须介于 0 和 100 之间")
	public String getRetreatDispensionUser() {
		return retreatDispensionUser;
	}
	public void setRetreatDispensionUser(String retreatDispensionUser) {
		this.retreatDispensionUser = retreatDispensionUser;
	}


	@Length(min=0, max=100, message="发药操作人长度必须介于 0 和 100 之间")
	public String getDispensionUser() {
		return dispensionUser;
	}
	public void setDispensionUser(String dispensionUser) {
		this.dispensionUser = dispensionUser;
	}

	public Date getRetreatDate() {
		return retreatDate;
	}

	public void setRetreatDate(Date retreatDate) {
		this.retreatDate = retreatDate;
	}

	public String getDrippingSpeed() {
		return drippingSpeed;
	}

	public void setDrippingSpeed(String drippingSpeed) {
		this.drippingSpeed = drippingSpeed;
	}

	public DictItem getInfusionDay() {
		return infusionDay;
	}

	public void setInfusionDay(DictItem infusionDay) {
		this.infusionDay = infusionDay;
	}

	public DictItem getInfusionFrequency() {
		return infusionFrequency;
	}

	public void setInfusionFrequency(DictItem infusionFrequency) {
		this.infusionFrequency = infusionFrequency;
	}

	public DictItem getInfusionUse() {
		return infusionUse;
	}

	public void setInfusionUse(DictItem infusionUse) {
		this.infusionUse = infusionUse;
	}

	public int getCureType() {
		return cureType;
	}

	public void setCureType(int cureType) {
		this.cureType = cureType;
	}

	public int getInfuseType() {
		return infuseType;
	}

	public void setInfuseType(int infuseType) {
		this.infuseType = infuseType;
	}
	/**
	 * 获取实体对应表名
	 */
	@Override
	@JsonIgnore
	public String getBusTableName() {
		return "recipel_info";
	}

	/**
	 * 获取实体对应Id
	 */
	@Override
	@JsonIgnore
	public String getBusTableId() {
		return "1014474470772900008";
	}


	/**
	 * 获取实体表中是否存在del_flag字段
	 */
	@JsonIgnore
	public boolean getBusTableHasDelFlag() {
		return true;
	}
}