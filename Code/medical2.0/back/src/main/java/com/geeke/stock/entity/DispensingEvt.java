package com.geeke.stock.entity;

import java.util.List;

/**
 * 入库Evt
 * @author txl
 * @version 2022-06-07
 */
public class DispensingEvt {

    private static final long serialVersionUID = 1005591224273698941L;
    private String registrationId;
    //  退药时的处方id
    private List<String> recipelInfoIdList;
    //  0|1|2|3|4 未发|部分已发|已发|已退|部分已退
    private String dispensingStatus;
    //  0|1 发药|退药
    private String dispensingType;
    private List<DispensingDetailEvt> dispensingDetailEvtList;

    public String getRegistrationId() {
        return registrationId;
    }

    public List<String> getRecipelInfoIdList() {
        return recipelInfoIdList;
    }

    public void setRecipelInfoIdList(List<String> recipelInfoIdList) {
        this.recipelInfoIdList = recipelInfoIdList;
    }

    public String getDispensingStatus() {
        return dispensingStatus;
    }

    public void setDispensingStatus(String dispensingStatus) {
        this.dispensingStatus = dispensingStatus;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getDispensingType() {
        return dispensingType;
    }

    public void setDispensingType(String dispensingType) {
        this.dispensingType = dispensingType;
    }

    public List<DispensingDetailEvt> getDispensingDetailEvtList() {
        return dispensingDetailEvtList;
    }

    public void setDispensingDetailEvtList(List<DispensingDetailEvt> dispensingDetailEvtList) {
        this.dispensingDetailEvtList = dispensingDetailEvtList;
    }
}
