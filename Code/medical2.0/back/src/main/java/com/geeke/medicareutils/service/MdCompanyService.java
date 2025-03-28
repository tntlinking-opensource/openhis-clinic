package com.geeke.medicareutils.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.medicareutils.domain.respo.MdCompanyData;
import com.geeke.medicareutils.domain.respo.MdStaffData;
import com.geeke.org.entity.ClinicOffice;

import java.util.List;


/**
 * 诊所服务
 */
public interface MdCompanyService {


    /**
     * 获取诊疗机构信息
     */
    String getOrgCompanyInfo_1201(String infoNo, String name, String fixmedinsCode);


    /**
     * 科室信息上传
     */
    String upCompanyData_3401_3402(ClinicOffice clinicOffice, String infoNo);



    /**
     * 批量科室信息上传
     */
    String upListCompanyData_3402A(List<ClinicOffice> clinicOffice, String infoNo);

    /**
     * 科室信息撤销
     * @param clinicOffice
     * @return
     */
    String delCompanyData_3403(ClinicOffice clinicOffice);

    /**
     * 获取医保医疗机构科室信息
     * @return
     */
    String getCompanyInfo_5101();

    /**
     * 获取医疗机构医保人员信息
     * @return
     */
    String getStaffInfo_5102(User user);



}
