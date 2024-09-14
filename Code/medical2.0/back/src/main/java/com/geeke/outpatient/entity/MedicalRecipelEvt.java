package com.geeke.outpatient.entity;

import java.util.List;

/**
 * 病历Evt
 * @author txl
 * @version 2022-06-08
 */
public class MedicalRecipelEvt {

	private static final long serialVersionUID = 1014786989773332527L;

	private MedicalRecord medicalRecord;
	private Patient patient;
	private List<RecipelInfoEvt> recipelInfoEvtList;
	private int isInstant;//0-非快速接诊 1-是快速接诊

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

	public int getIsInstant() {
		return isInstant;
	}

	public void setIsInstant(int isInstant) {
		this.isInstant = isInstant;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}