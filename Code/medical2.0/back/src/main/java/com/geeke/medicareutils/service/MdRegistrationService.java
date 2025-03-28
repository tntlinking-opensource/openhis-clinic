package com.geeke.medicareutils.service;

import com.alibaba.fastjson.JSONArray;
import com.geeke.medicareutils.domain.respo.MdFeeDetail;
import com.geeke.outpatient.entity.Registration;

import java.time.LocalDateTime;

/**
 * 医保挂号、就诊、诊断、结算
 */
public interface MdRegistrationService {
    /**
     * 门诊医保挂号
     * @return
     */
    String  getRegistrationInfo_2201(Registration registration);

    /**
     * 门诊挂号取消
     * @param registration
     * @return
     */
    String revokeRegistrationInfo_2202(Registration registration);


    /**
     * 门诊就诊信息上传
     * @param registration
     * @return
     */
    String upRegistrationInfo_2203(Registration registration);


    /**
     * 门诊就诊信息上传 2203A
     *
     * @param registration
     * @return
     */
    String upRegistrationInfoList_2203A(Registration registration);


    /**
     * 门诊费用明细信息上传 2204
     *
     * @param
     * @return
     */
    String upRegistrationMoneyInfo_2204(JSONArray jsonArray);

    /**
     * 门诊费用明细撤销明细 2205
     *
     * @param
     * @return
     */
    String revokeRegistrationMoneyInfo_2205(String mdtrtId, String chrgBchno, String psnNo, String expContent);



    /**
     * 门诊预结算 2206
     *
     * @param registration 挂号信息
     * @param mdFeeDetail  收费信息
     * @return
     */
    String processOutpatientPreSettlement_2206(Registration registration, MdFeeDetail mdFeeDetail, String chrgBchno, String acctUsedFlag);


    /**
     * 门诊结算 2207
     *
     * @param registration 挂号信息
     * @param mdFeeDetail  费用明细输出
     * @param chrgBchno    收费批次号
     * @param acctUsedFlag 是否自费
     * @return
     */
    String executeOutpatientPreSettlement_2207(Registration registration, MdFeeDetail mdFeeDetail, String chrgBchno, String acctUsedFlag);

    /**
     * 门诊结算撤销  2208
     *
     * @param setlId
     * @param psnNo
     * @param mdtrtId
     * @return
     */
    String revokeOutpatientSettlement_2208(String setlId, String  psnNo, String mdtrtId);


    /**
     * 就诊信息查询
     *
     * @param registration
     * @param begntime
     * @param endtime
     * @return
     */
    String getPsnVisitData_5201(Registration registration, LocalDateTime begntime, LocalDateTime endtime);

    /**
     * 诊断信息查询
     *
     * @param registration
     * @return
     */
    String getPsnDiseData_5202(Registration registration);

    /**
     * 结算信息查询
     *
     * @return
     */
    String getSettlementInfo_5203(String psnNo, String setlId, String mdtrtId);

    /**
     * 费用明细查询
     *
     * @param psnNo
     * @param setlId
     * @param mdtrtId
     * @return
     */
    String getExpenseDetails_5204(String psnNo, String setlId, String mdtrtId);

    /**
     * 门急诊诊疗记录
     *
     * @param registration 挂号信息
     * @return
     */
    String getEmergencyOutpatientRecords_4301(Registration registration);

    /**
     * 明细审核事前分析服务
     *
     * @param registration
     * @return
     */
    String analyzeDetailReviewPreCheck_3301(Registration registration);

    /**
     * 明细审核事中分析服务
     *
     * @param registration 3102
     * @return
     */
    String analyzeDetailReviewDuringProcess_3102(Registration registration);


    /**
     * 医药机构费用结算对总账
     *
     * @return
     */
    String matchPharmacyCostsWithGeneralLedger_3201();

    /**
     * 医药机构费用结算对明细账
     *
     * @return
     */
    String matchPharmacyCostsWithDetailAccounts_3202();
}
