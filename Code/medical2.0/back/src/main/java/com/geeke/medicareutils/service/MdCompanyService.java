package com.geeke.medicareutils.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.medicareutils.domain.respo.MdCompanyData;
import com.geeke.medicareutils.domain.respo.MdStaffData;
import com.geeke.org.entity.ClinicOffice;
import springfox.documentation.spring.web.json.Json;

import java.util.List;


/**
 * 诊所服务
 */
public interface MdCompanyService {


    /**
     * 获取诊疗机构信息
     */
     JSONObject getOrgCompanyInfo(String infoNo,String name,String fixmedinsCode);


    /**
     * 科室信息上传
     */
    JSONObject upCompanyData(ClinicOffice clinicOffice,String infoNo);



    /**
     * 批量科室信息上传
     */
    JSONObject upListCompanyData(List<ClinicOffice> clinicOffice, String infoNo);

    /**
     * 科室信息撤销
     * @param clinicOffice
     * @return
     */
    JSONObject delCompanyData(ClinicOffice clinicOffice);

    /**
     * 获取医保医疗机构科室信息
     * @return
     */
    MdCompanyData getCompanyInfo();

    /**
     * 获取医疗机构医保人员信息
     * @return
     */
    MdStaffData getStaffInfo(User user);



}
