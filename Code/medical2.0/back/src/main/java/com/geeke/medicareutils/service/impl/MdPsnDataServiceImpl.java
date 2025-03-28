package com.geeke.medicareutils.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.geeke.medicareutils.domain.respo.Output_1101;
import com.geeke.medicareutils.service.MdPsnDataService;
import com.geeke.medicareutils.util.YbWebApiUtil;
import com.geeke.outpatient.dao.PatientDao;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.entity.PatientMdData;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.PatientMdDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MdPsnDataServiceImpl  implements MdPsnDataService {

    //private final MdRequestUtil mdRequestUtil;

    private final YbWebApiUtil webApiUtil;

    private final PatientMdDataService patientMdDataService;

    @Autowired
    private PatientDao patientDao;


    @Override
    public Boolean getAndSetPsnData(Registration registration) {
        //构建请求参数
        JSONObject data = new JSONObject();
        //获取患者信息
        Patient patientTemp = patientDao.getPatientByregistrationId(registration.getId()) ;
        //就诊凭证类型 待定 TODO 读卡获取
        data.put("mdtrt_cert_type",registration.getCardType().getValue());
        //“01”时填写电子凭证 令牌，为“02”时填写身份证号，为“03”时填写社会保障卡卡号
        data.put("mdtrt_cert_no",registration.getMdtrtCertNo());
        //卡识别码 03时必填
        data.put("card_sn","");
        if("03".equals(registration.getCardType().getValue())){
            data.put("card_sn",patientTemp.getCard());
        }
        //开始时间，获取历史参保信息时传入
        data.put("begntime","");
        //人员证件类型 默认身份证 TODO 待对接传值
        data.put("psn_cert_type","01");
        //人员证件号码
        data.put("certno",patientTemp.getCard());
        //人员姓名
        data.put("psn_name",patientTemp.getName());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        Output_1101 output1101 = JSONObject.parseObject(webApiUtil.getMedicareData("1101", jsonObject.toJSONString()), Output_1101.class);
        //构建医保患者基本信息
        PatientMdData patientMdData = new PatientMdData();
        //基本信息
        patientMdData.setPatientId(Long.valueOf(patientTemp.getId()));
        //patientMdData.setOrganizeId(Long.valueOf(SessionUtils.getLoginTenantId()));

        // 获取 BaseInfo 类中的信息
        Output_1101.BaseInfo baseInfo = output1101.getBaseinfo();
        patientMdData.setPsnNo(baseInfo.getPsn_no());
        patientMdData.setPsnCertType(baseInfo.getPsn_cert_type());
        patientMdData.setPsnName(baseInfo.getPsn_name());
        patientMdData.setCertno(baseInfo.getCertno());
        patientMdData.setGend(baseInfo.getGend());
        patientMdData.setNaty(baseInfo.getNaty());
        patientMdData.setBrdy(baseInfo.getBrdy());
        patientMdData.setAge(baseInfo.getAge());
        patientMdData.setExpcontent("");
        // 获取 InsuInfo 类中的信息
        if (output1101.getInsuinfo() != null && !output1101.getInsuinfo().isEmpty()) {
            Output_1101.InsuInfo insuInfo = output1101.getInsuinfo().get(0); // 假设只取第一个参保信息
            patientMdData.setBalc(insuInfo.getBalc());
            patientMdData.setInsutype(insuInfo.getInsutype());
            patientMdData.setPsnType(insuInfo.getPsn_type());
            patientMdData.setPsnInsuStas(insuInfo.getPsn_insu_stas());
            patientMdData.setPsnInsuDate(insuInfo.getPsn_insu_date());
            patientMdData.setPausInsuDate(insuInfo.getPaus_insu_date());
            patientMdData.setCvlservFlag(insuInfo.getCvlserv_flag());
            patientMdData.setInsuplcAdmdvs(insuInfo.getInsuplc_admdvs());
            patientMdData.setEmpName(insuInfo.getEmp_name());
        }


        //根据患者id插入或更新
        return   patientMdDataService.saveOrUpdate(patientMdData, new LambdaQueryWrapper<PatientMdData>()
                .eq(PatientMdData::getPatientId, patientMdData.getPatientId()));
    }




}
