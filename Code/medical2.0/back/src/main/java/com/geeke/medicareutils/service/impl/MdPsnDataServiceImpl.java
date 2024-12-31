package com.geeke.medicareutils.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.geeke.medicareutils.service.MdPsnDataService;
import com.geeke.medicareutils.util.MdRequestUtil;
import com.geeke.outpatient.dao.PatientDao;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.entity.PatientMdData;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.PatientMdDataService;
import com.geeke.utils.SessionUtils;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MdPsnDataServiceImpl  implements MdPsnDataService {

    private final MdRequestUtil mdRequestUtil;

    private final PatientMdDataService patientMdDataService;

    @Autowired
    private PatientDao patientDao;


    @Override
    public Boolean getAndSetPsnData(Registration registration) {
        //构建请求参数
        JSONObject data = new JSONObject();
        //获取患者信息
        Patient patientTemp = patientDao.getPatientByregistrationId(registration.getId()) ;
        //就诊凭证类型 待定
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
        //人员证件类型 默认身份证
        data.put("psn_cert_type","01");
        //人员证件号码
        data.put("certno",patientTemp.getCard());
        //人员姓名
        data.put("psn_name",patientTemp.getName());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        //人员基本信息医保结果
        JSONObject psnData = mdRequestUtil.getMedicareData("1101", jsonObject);
        //模拟数据
        //JSONObject psnData = JSON.parseObject("{\"baseinfo\":{\"psn_no\":\"131000202001001\",\"psn_cert_type\":\"2\",\"certno\":\"510000202001010000\",\"psn_name\":\"李四\",\"gend\":\"1\",\"naty\":\"01\",\"brdy\":\"2020-01-01\",\"age\":18},\"insuinfo\":{\"psn_insu_rlts_id\":\"133241523001001\",\"balc\":5000,\"insutype\":\"310\",\"psn_type\":\"1001\",\"cvlserv_flag\":\"0\",\"insuplc_admdvs\":\"131002\",\"emp_name\":\"测试单位\"},\"idetinfo\":[{\"psn_idet_type\":\"1\",\"psn_type_lv\":\"1\",\"memo\":\"\",\"begntime\":\"2020-01-01 00:00:00\",\"endtime\":\"\"},{\"psn_idet_type\":\"2\",\"psn_type_lv\":\"1\",\"memo\":\"\",\"begntime\":\"2020-01-01 00:00:00\",\"endtime\":\"\"}]}\n");
        //构建医保患者基本信息
        PatientMdData patientMdData = new PatientMdData();
        //基本信息
        patientMdData.setPatientId(Long.valueOf(patientTemp.getId()));
        patientMdData.setPsnNo(psnData.getJSONObject("baseinfo").getString("psn_no"));
        patientMdData.setPsnCertType(psnData.getJSONObject("baseinfo").getString("psn_cert_type"));
        patientMdData.setPsnName(psnData.getJSONObject("baseinfo").getString("psn_name"));
        patientMdData.setCertno(psnData.getJSONObject("baseinfo").getString("certno"));
        patientMdData.setGend(psnData.getJSONObject("baseinfo").getString("gend"));
        patientMdData.setNaty(psnData.getJSONObject("baseinfo").getString("naty"));
        patientMdData.setBrdy(psnData.getJSONObject("baseinfo").getString("brdy"));
        patientMdData.setAge(psnData.getJSONObject("baseinfo").getInteger("age"));
        patientMdData.setExpcontent(psnData.getJSONObject("baseinfo").getString("expContent"));
        //参保信息
        patientMdData.setBalc(psnData.getJSONObject("insuinfo").getBigDecimal("balc"));
        patientMdData.setInsutype(psnData.getJSONObject("insuinfo").getString("insutype"));
        patientMdData.setPsnType(psnData.getJSONObject("insuinfo").getString("psn_type"));
        patientMdData.setPsnInsuStas(psnData.getJSONObject("insuinfo").getString("psn_insu_stas"));
        patientMdData.setPsnInsuDate(psnData.getJSONObject("insuinfo").getDate("psn_insu_date"));
        patientMdData.setPausInsuDate(psnData.getJSONObject("insuinfo").getDate("paus_insu_date"));
        patientMdData.setCvlservFlag(psnData.getJSONObject("insuinfo").getString("cvlserv_flag"));
        patientMdData.setInsuplcAdmdvs(psnData.getJSONObject("insuinfo").getString("insuplc_admdvs"));
        patientMdData.setEmpName(psnData.getJSONObject("insuinfo").getString("emp_name"));
        //获取人员待遇信息
        JSONObject data1 = new JSONObject();
        data1.put("psn_no",psnData.getJSONObject("baseinfo").getString("psn_no"));
        data1.put("insutype",psnData.getJSONObject("insuinfo").getString("insutype"));
        data1.put("fixmedins_code", SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode"));
        data1.put("begntime",psnData.getJSONObject("insuinfo").getDate("psn_insu_date"));
        //Todo 非必填参数 待联调配置
        data1.put("endtime","");
        //病种编码
        data1.put("dise_codg","");
        //病种名称
        data1.put("dise_name","");
        //日间手术病种必填
        data1.put("oprn_oprt_code","");
        //手术操作名称
        data1.put("oprn_oprt_name","");
        //生育类别
        data1.put("matn_type","");
        data1.put("birtctrl_type","");
        data1.put("expContent","");
        //人员待遇信息医保结果
        JSONObject jsonObject1 = new JSONObject();
        jsonObject.put("data",data1);
        JSONObject psnTrtData = mdRequestUtil.getMedicareData("2001", jsonObject1);
        //模拟待遇信息
//        //JSONObject psnTrtData =  JSON.parseObject("{\n" +
//                "  \"trtinfo\": {\n" +
//                "    \"trt_chk_type\": \"TypeA\",\n" +
//                "    \"fund_pay_type\": \"TypeB\",\n" +
//                "    \"trt_enjymnt_flag\": \"Yes\",\n" +
//                "    \"trt_chk_rslt\": \"Success\",\n" +
//                "    \"expcontent\": \"Detailed explanation\",\n" +
//                "    \"begndate\": \"2024-09-01T00:00:00Z\"\n" +
//                "  }\n" +
//                "}\n");
        patientMdData.setTrtChkType(psnTrtData.getJSONObject("trtinfo").getString("trt_chk_type"));
        patientMdData.setFundPayType(psnTrtData.getJSONObject("trtinfo").getString("fund_pay_type"));
        patientMdData.setTrtEnjymntFlag(psnTrtData.getJSONObject("trtinfo").getString("trt_enjymnt_flag"));
        patientMdData.setTrtChkRslt(psnTrtData.getJSONObject("trtinfo").getString("trt_chk_rslt"));
        patientMdData.setTrtExpcontent(psnTrtData.getJSONObject("trtinfo").getString("expcontent"));
        patientMdData.setTrtBegndate(psnTrtData.getJSONObject("trtinfo").getDate("begndate"));
        //根据患者id插入或更新
        return   patientMdDataService.saveOrUpdate(patientMdData, new LambdaQueryWrapper<PatientMdData>()
                .eq(PatientMdData::getPatientId, patientMdData.getPatientId()));
    }




}
