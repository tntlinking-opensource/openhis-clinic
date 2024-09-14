package com.geeke.outpatient.entity;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.geeke.utils.SessionUtils;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 慢病处方详情Entity
 * @author txl
 * @version 2022-06-07
 */
@Data
public class ChronicDiseaseEvt implements Serializable {

	/**
	 * 费别
	 */
	private String febbe = "费别：普通病人";
	/**
	 * 患者姓名
	 */
	private String name;
	/**
	 * 性别
	 */
	private String gender;
	/**
	 * 年龄
	 */
	private String age;
	/**
	 * 体温
	 */
	private String temperature;
	/**
	 * 门诊/住院病历号，取挂号单号
	 */
	private String registrationId;
	/**
	 * 科别/病区/床位号，取科室
	 */
	private String clinicOffice;
	/**
	 * 临床诊断，取西药诊断
	 */
	private String westernDiagnose;
	/**
	 * 其他诊断，取中药诊断
	 */
	private String chinaDiagnose;
	/**
	 * 并发疾病，数据先为空
	 */
	private String concurrentDisease="并发疾病：";
	/**
	 * 备注，取医嘱事项
	 */
	private String doctorAdvice;
	/**
	 * 开具日期，取medicalRecord的创建时间
	 */
	private String createDate;
	/**
	 * 医师
	 */
	private String doctor;
	/**
	 * 药品金额
	 */
	private String amount;
	/**
	 * 收费员
	 */
	private String tollCollector = "收费员：";
	/**
	 * 注射费
	 */
	private String injectionFee;
	/**
	 * 审核/调配
	 */
	private String deploy = "审核/调配：";
	/**
	 * 核对发药
	 */
	private String dispensing = "核对/发药：";
	/**
	 * 大额处方患者意见
	 */
	private String patientOpinion = "大额处方患者意见：同意 不同意";
	/**
	 * 患者签名
	 */
	private String signature = "患者签名：";
	/**
	 * 联系地址和电话
	 */
	private String address;
	/**
	 * 医院名称
	 */
	private String clinicName;
	/**
	 * 处方小类，慢病出发，普通处方
	 */
	private String smallType ="慢病处方";

	private String tips = "（收费票据请贴附处方背面）";
	public ChronicDiseaseEvt(Patient patient, Registration registration,
							 MedicalRecord medicalRecord,RecipelInfo recipelInfo,
							 List<RecipelDetail> detailList) {
		this.buildPatient(patient);
		this.buildRegistration(registration);
		this.buildMedicalRecord(medicalRecord);
		this.buildRecipelInfo(recipelInfo);
		this.buildRecipelDetail(detailList);
	}

	private void buildRecipelDetail(List<RecipelDetail> detailList) {
		if (CollUtil.isNotEmpty(detailList)) {
			BigDecimal injectionFeeSum = detailList.stream()
					.filter(detail -> detail.getIsExtra() == 1)
					.map(RecipelDetail::getAllFee)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			this.injectionFee = "附加费：￥" + injectionFeeSum;
			BigDecimal amountSum = detailList.stream()
					.filter(detail -> detail.getIsExtra() == 0)
					.map(RecipelDetail::getAllFee)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			this.amount = "药品金额：￥" + amountSum;
		}else {
			this.injectionFee = "附加费：￥ 0";
			this.amount = "药品金额：￥ 0";
		}
	}

	private void buildRecipelInfo(RecipelInfo recipelInfo) {
		this.smallType = recipelInfo.getChronicDisease() ? "慢病处方" : "普通处方";
		this.doctorAdvice = "备注：" + (StrUtil.isBlank(recipelInfo.getEntrust()) ? "" : recipelInfo.getEntrust());
	}

	private void buildPatient(Patient patient){
		this.name = "患者姓名：" + patient.getName();
		this.gender = "性别：" + patient.getGender().getName();
		this.age = "年龄：" + (StrUtil.isBlank(patient.getAge()) ? "" : patient.getAge());
		this.address = "联系电话或地址（患者自愿填写）：" + patient.getAddress();
		this.clinicName = patient.getCompany().getName()+"处方笺";
	}
	private void buildRegistration(Registration registration){
		this.registrationId ="门诊/住院病历号："+ registration.getId();
//		this.clinicOffice = "科别/病区/床位号："+registration.getClinicOffice().getName();
		this.clinicOffice = "科别："+registration.getClinicOffice().getName();
		this.temperature = "体温：" + (StrUtil.isBlank(registration.getTemperature()) ? "" : registration.getTemperature())+"℃";
	}

	private void buildMedicalRecord(MedicalRecord medicalRecord){
		this.westernDiagnose = "西医诊断：" + medicalRecord.getWesternDiagnose();
		this.chinaDiagnose = "中医诊断：" + medicalRecord.getChinaDiagnose();
		this.createDate = "开具日期：" + DateUtil.formatDateTime(medicalRecord.getCreateDate());
		this.doctor = "医师：" + medicalRecord.getDoctor().getName();
	}

}