package com.geeke.outpatient.vo;

import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.entity.RecipelInfoReview;

import java.io.Serializable;
import java.util.List;

/**
 * 处方审查VO
 */
public class ReviewVO implements Serializable {
    private RecipelInfoReview recipelInfoReview;
    /**
     * 处方明细
     */
    private List<RecipelDetail> recipelDetails;
    /**
     * 附加费
     */
    private List<RecipelDetail> additionalCharges;
    /**
     * 西医诊断
     */
    private String westernDiagnose;
    /**
     * 中医诊断
     */
    private String chinaDiagnose;

    public List<RecipelDetail> getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(List<RecipelDetail> additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public RecipelInfoReview getRecipelInfoReview() {
        return recipelInfoReview;
    }

    public void setRecipelInfoReview(RecipelInfoReview recipelInfoReview) {
        this.recipelInfoReview = recipelInfoReview;
    }

    public List<RecipelDetail> getRecipelDetails() {
        return recipelDetails;
    }

    public void setRecipelDetails(List<RecipelDetail> recipelDetails) {
        this.recipelDetails = recipelDetails;
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
}
