package com.geeke.toll.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.geeke.common.data.Page;
import com.geeke.utils.excel.ExcelCell;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zh
 */
@Data
public class OutpatientLog {

    private Page page;

    /** ID */
    private String id;

    /** 患者姓名 */
    @ExcelCell(name = "患者姓名")
    private String patientName;

    /**家长姓名 */
    @ExcelCell(name = "家长姓名")
    private String patriarchName;

    /** 性别 */
    @ExcelCell(name = "性别",convertKey = "OutpatientLog-gender")
    private String sex;

    /** 患者年龄 */
    @ExcelCell(name = "患者年龄")
    private String age;

    /** 民族 */
    @ExcelCell(name = "民族")
    private String nation;

    /** 职业 */
    @ExcelCell(name = "职业")
    private String occupation;

    /** 详细地址 */
    @ExcelCell(name = "详细地址")
    private String address;

    /** 就诊日期 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
/*
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd'T'HH:mm:ss")
*/
    @ExcelCell(name = "就诊日期")
    private Date visitDate;
    /** 初/复诊 */
    @ExcelCell(name = "初/复诊",convertKey = "OutpatientLog-initialVisit")
    private String initialVisit;

    /** 血压 */
    @ExcelCell(name = "血压")
    private String bloodPressure;

    /** 临床症状 */
    @ExcelCell(name = "临床症状")
    private String symptom;

    /** 体温发热 */
    @ExcelCell(name = "体温发热")
    private String fever;

     /** 流行病学史 */
     @ExcelCell(name = "流行病学史")
    private String epidemicDisease;

    /** 西医诊断 */
    @ExcelCell(name = "西医诊断")
    private String westernDiagnose;

    /** 中医诊断 */
    @ExcelCell(name = "中医诊断")
    private String chinaDiagnose;

    /** 传染病 */
    @ExcelCell(name = "传染病",convertKey = "OutpatientLog-infect")
    private String infect;

    /** 处理情况 */
    @ExcelCell(name = "处理情况")
    private String handle;

    /** 有效证件号 */
    @ExcelCell(name = "有效证件号")
    private String certificate;

    /** 工作单位 */
    @ExcelCell(name = "工作单位")
    private String unit;

    /** 医生签名 */
    @ExcelCell(name = "医生签名")
    private String signature;

    /** 贫困标志 */
    @ExcelCell(name = "贫困标志")
    private String poverty;

    /** 联系电话 */
    @ExcelCell(name = "联系电话")
    private String telephone;

    /** 发病日期 */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @ExcelCell(name = "发病日期")
    private Date morbidityDate;

    /** 实验室阳性结果 */
    @ExcelCell(name = "实验室阳性结果")
    private String positiveResult;

    /** 个体化健康教育 */
    @ExcelCell(name = "个体化健康教育")
    private String healthEducation;

    public OutpatientLog() {
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatriarchName() {
        return patriarchName;
    }

    public void setPatriarchName(String patriarchName) {
        this.patriarchName = patriarchName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getVisitDate() {
        return (Date) visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getInitialVisit() {
        return initialVisit;
    }

    public void setInitialVisit(String initialVisit) {
        this.initialVisit = initialVisit;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getFever() {
        return fever;
    }

    public void setFever(String fever) {
        this.fever = fever;
    }

    public String getEpidemicDisease() {
        return epidemicDisease;
    }

    public void setEpidemicDisease(String epidemicDisease) {
        this.epidemicDisease = epidemicDisease;
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

    public String getInfect() {
        return infect;
    }

    public void setInfect(String infect) {
        this.infect = infect;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPoverty() {
        return poverty;
    }

    public void setPoverty(String poverty) {
        this.poverty = poverty;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Date getMorbidityDate() {
        return (Date) morbidityDate;
    }

    public void setMorbidityDate(Date morbidityDate) {
        this.morbidityDate = (Date) morbidityDate;
    }

    public String getPositiveResult() {
        return positiveResult;
    }

    public void setPositiveResult(String positiveResult) {
        this.positiveResult = positiveResult;
    }

    public String getHealthEducation() {
        return healthEducation;
    }

    public void setHealthEducation(String healthEducation) {
        this.healthEducation = healthEducation;
    }
}