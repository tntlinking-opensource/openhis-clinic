package com.geeke.medicareutils.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geeke.medicareutils.domain.respo.MdFeeDetail;
import com.geeke.medicareutils.domain.respo.MdPsnDiseData;
import com.geeke.medicareutils.domain.respo.MdPsnVisitData;
import com.geeke.outpatient.entity.MedicalRecord;
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
    JSONObject getRegistrationInfo(Registration registration);

    /**
     * 门诊挂号取消
     * @param registration
     * @return
     */
    JSONObject revokeRegistrationInfo(Registration registration);


    /**
     * 门诊就诊信息上传
     * @param registration
     * @return
     */
    JSONObject upRegistrationInfo(Registration registration);


    /**
     * 门诊就诊信息上传 2203A
     * @param registration
     * @return
     */
    JSONObject upRegistrationInfoList(Registration registration);


    /**
     * 门诊费用明细信息上传 2204
     * @param
     * @return
     */
    MdFeeDetail upRegistrationMoneyInfo(JSONArray jsonArray);

    /**
     * 门诊费用明细撤销明细 2205
     * @param
     * @return
     */
    JSONObject revokeRegistrationMoneyInfo(String mdtrtId,String chrgBchno,String psnNo,String expContent);



    /**
     * 门诊预结算 2206
     * @param registration 挂号信息
     * @param mdFeeDetail 收费信息
     * @return
     */
    JSONObject processOutpatientPreSettlement (Registration registration, MdFeeDetail mdFeeDetail,String chrgBchno,String acctUsedFlag);


    /**
     * 门诊结算 2207
     * @param registration 挂号信息
     * @param mdFeeDetail  费用明细输出
     * @param chrgBchno  收费批次号
     * @param acctUsedFlag 是否自费
     * @return
     */
    JSONObject executeOutpatientPreSettlement(Registration registration, MdFeeDetail mdFeeDetail,String chrgBchno,String acctUsedFlag);

    /**
     * 门诊结算撤销  2208
     * @param setlId
     * @param psnNo
     * @param mdtrtId
     * @return
     */
    JSONObject revokeOutpatientSettlement(String setlId,String  psnNo,String mdtrtId);


    /**
     * 就诊信息查询
     * @param registration
     * @param begntime
     * @param endtime
     * @return
     */
    MdPsnVisitData getPsnVisitData(Registration registration, LocalDateTime begntime, LocalDateTime endtime);

    /**
     * 诊断信息查询
     * @param registration
     * @return
     */
    MdPsnDiseData getPsnDiseData(Registration registration);

    /**
     * 结算信息查询
     * @return
     */
    JSONObject getSettlementInfo(String psnNo,String setlId,String mdtrtId);

    /**
     * 费用明细查询
     * @param psnNo
     * @param setlId
     * @param mdtrtId
     * @return
     */
    JSONObject getExpenseDetails(String psnNo,String setlId,String mdtrtId);

    /**
     * 门急诊诊疗记录
     * @param registration 挂号信息
     * @return
     */
    JSONObject getEmergencyOutpatientRecords(Registration registration);

    /**
     * 明细审核事前分析服务
     * @param registration
     * @return
     */
    JSONObject analyzeDetailReviewPreCheck(Registration registration);

    /**
     * 明细审核事中分析服务
     * @param registration 3102
     * @return
     */
    JSONObject analyzeDetailReviewDuringProcess(Registration registration);


    /**
     * 医药机构费用结算对总账
     * @return
     */
    JSONObject matchPharmacyCostsWithGeneralLedger();

    /** 医药机构费用结算对明细账

     * @return
     */
    JSONObject  matchPharmacyCostsWithDetailAccounts();
}
