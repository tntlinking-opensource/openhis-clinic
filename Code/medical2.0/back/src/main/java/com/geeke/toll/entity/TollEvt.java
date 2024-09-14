package com.geeke.toll.entity;

import com.geeke.member.entity.MemberManagement;
import com.geeke.outpatient.entity.RecipelInfoEvt;

import java.util.List;

/**
 * LC
 * 保存收费信息
 */

public class TollEvt {
    private TollInfo tollInfo;
    private List<RecipelInfoEvt> recipelInfos; //处方信息
    private String chargeStatus; //收费状态  0- 待收费   1-部分收费   2- 已收费   3-  部分退费    4- 已退费
    private MemberManagement memberManagement;   //保存的会员卡信息

    public String getChargeStatus() {
        return chargeStatus;
    }

    public void setChargeStatus(String chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    public TollInfo getTollInfo() {
        return tollInfo;
    }

    public void setTollInfo(TollInfo tollInfo) {
        this.tollInfo = tollInfo;
    }

    public List<RecipelInfoEvt> getRecipelInfos() {
        return recipelInfos;
    }

    public void setRecipelInfos(List<RecipelInfoEvt> recipelInfos) {
        this.recipelInfos = recipelInfos;
    }

    public MemberManagement getMemberManagement() {
        return memberManagement;
    }

    public void setMemberManagement(MemberManagement memberManagement) {
        this.memberManagement = memberManagement;
    }
}
