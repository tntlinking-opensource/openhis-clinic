package com.geeke.outpatient.vo;

import java.io.Serializable;

/**
 * 处方审查统计表-处方统计
 */
public class PrescriptionStatisticsVO implements Serializable {
    /**
     * 处方总数
     */
    private String total;
    /**
     * 已发药数
     */
    private String dispensedMedicine;
    /**
     * 未发药数
     */
    private String undispensedMedicine;
    /**
     * 已点评数
     */
    private String alreadyReviewed;
    /**
     * 合格数
     */
    private String good;
    /**
     * 不合格数
     */
    private String bad;
    /**
     * 合格率
     */
    private String rate;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDispensedMedicine() {
        return dispensedMedicine;
    }

    public void setDispensedMedicine(String dispensedMedicine) {
        this.dispensedMedicine = dispensedMedicine;
    }

    public String getUndispensedMedicine() {
        return undispensedMedicine;
    }

    public void setUndispensedMedicine(String undispensedMedicine) {
        this.undispensedMedicine = undispensedMedicine;
    }

    public String getAlreadyReviewed() {
        return alreadyReviewed;
    }

    public void setAlreadyReviewed(String alreadyReviewed) {
        this.alreadyReviewed = alreadyReviewed;
    }

    public String getGood() {
        return good;
    }

    public void setGood(String good) {
        this.good = good;
    }

    public String getBad() {
        return bad;
    }

    public void setBad(String bad) {
        this.bad = bad;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
