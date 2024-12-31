package com.geeke.medicareutils.service;


import com.alibaba.fastjson.JSONObject;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.entity.Registration;

import java.time.LocalDateTime;

/**
 * 患者医保信息
 */
public interface MdPsnDataService {
    /**
     * 获取人员基本信息
     * @param
     * @return
     */
    Boolean  getAndSetPsnData( Registration registration);



}
