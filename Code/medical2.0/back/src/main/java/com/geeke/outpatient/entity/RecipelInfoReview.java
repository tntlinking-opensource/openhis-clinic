package com.geeke.outpatient.entity;

import com.geeke.common.persistence.DataEntity;

import java.util.Date;

/**
 * 处方信息审查Entity
 * @author lc
 * @version 2023-10-09
 */
public class RecipelInfoReview extends DataEntity<RecipelInfoReview> {
    private RecipelInfo recipelInfo; //处方信息
    private MedicalRecord medicalRecord; //病例信息
    private Integer reviewResult = 1; // 审查结果：1表示合理，0表示不合理
    private String reviewContent = ""; // 审查内容
    private String reviewerName = ""; // 审查人的名字,审查时间取createDate
    private Integer reviewStatus = 0; // 审查状态：0表示未审查，1表示已审查

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public RecipelInfo getRecipelInfo() {
        return recipelInfo;
    }

    public void setRecipelInfo(RecipelInfo recipelInfo) {
        this.recipelInfo = recipelInfo;
    }

    public Integer getReviewResult() {
        return reviewResult;
    }

    public void setReviewResult(Integer reviewResult) {
        this.reviewResult = reviewResult;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}
