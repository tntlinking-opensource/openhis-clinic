package com.geeke.medicareutils.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.outpatient.entity.MedicalRecord;
import com.geeke.outpatient.entity.Registration;

public interface MdCommonService {
    /**
     * 冲正数据交易
     * Description
     * 可用交易码：【2101】医生结算、【2102】药店结算、【2103】药店结算撤销、【2207】门诊结算、
     * 【2208】门诊结算撤销、【2304】住院结算、【2305】住院结算撤销、【2401】入院办理、
     * 【2304A】住院结算；
     */
    JSONObject reversalData(String psnNo,String omsgid,String oinfno);


    /**
     * 电子病历上传 4701
     * @return
     */
    JSONObject upMedicalRecord(Registration registration);










}
