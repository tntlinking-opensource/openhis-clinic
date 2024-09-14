package com.geeke.outpatient.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 完成接诊保存数据组装实体
 * @author helong
 * @date 2022/8/22 16:00
 */
public class ReceptionEvt implements Serializable
{
    private static final long serialVersionUID = -4774843195581832448L;
    /**
     * 完成接诊数据ID(普通接诊必传)
     */
    private String id;
    /**
     * 登记信息
     */
    private Registration registration;
    /**
     * 基础信息（即患者信息）
     */
    private Patient patient;
    /**
     * 病例填写信息
     */
    private MedicalRecord medicalRecord;
    /**
     * 开具处方信息（包含处方主子明细）
     */
    private List<RecipelInfoEvt> recipelInfoEvtList;
    /**
     * 接诊类型:0-普通接诊  1-快速接诊  2-零售收费
     */
    private int type = 0;
    /**
     * 删除的附件id列表
     */
    private List<String> deleteFileIdList;

    public List<String> getDeleteFileIdList() {
        return deleteFileIdList;
    }

    public void setDeleteFileIdList(List<String> deleteFileIdList) {
        this.deleteFileIdList = deleteFileIdList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public List<RecipelInfoEvt> getRecipelInfoEvtList() {
        return recipelInfoEvtList;
    }

    public void setRecipelInfoEvtList(List<RecipelInfoEvt> recipelInfoEvtList) {
        this.recipelInfoEvtList = recipelInfoEvtList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
