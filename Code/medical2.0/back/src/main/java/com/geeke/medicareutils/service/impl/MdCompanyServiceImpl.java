package com.geeke.medicareutils.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.medicareutils.domain.reqpo.RequestData;
import com.geeke.medicareutils.domain.respo.MdCompanyData;
import com.geeke.medicareutils.domain.respo.MdStaffData;
import com.geeke.medicareutils.service.MdCompanyService;
import com.geeke.medicareutils.util.MdRequestUtil;
import com.geeke.org.entity.ClinicOffice;
import com.geeke.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科室实现类
 */
@Service
@RequiredArgsConstructor
public class MdCompanyServiceImpl implements MdCompanyService{

    private final MdRequestUtil mdRequestUtil;

    private final StringRedisTemplate stringRedisTemplate;


    @Override
    public JSONObject getOrgCompanyInfo(String infoNo,String name,String fixmedinsCode) {
        JSONObject data = new JSONObject();
        //定点医疗机构
        data.put("fixmdins_type","1");
        data.put("fixmdins_name",name);
        data.put("fixmedins_code",fixmedinsCode);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
      return   mdRequestUtil.getMedicareData("1201",jsonObject);
    }

    @Override
    public JSONObject upCompanyData(ClinicOffice clinicOffice,String infoNo) {
        JSONObject data = new JSONObject();
        data.put("hosp_dept_codg",clinicOffice.getCode());
        data.put("hosp_dept_name",clinicOffice.getName());
        data.put("caty",clinicOffice.getCategory());
        data.put("begntime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(clinicOffice.getCreateDate()));
        data.put("endtime","");
        data.put("itro",clinicOffice.getItro());
        data.put("dept_resper_name",clinicOffice.getDeptResperName());
        data.put("dept_resper_tel",clinicOffice.getDeptResperTel());
        data.put("dept_estbdat",clinicOffice.getDeptEstbdat());
        data.put("dept_med_serv_scp","");
        data.put("aprv_bed_cnt",clinicOffice.getAprvBedCnt());
        data.put("hi_crtf_bed_cnt","");
        data.put("poolarea_no",clinicOffice.getPoolareaNo());
        data.put("tecn_psncnt",clinicOffice.getTecnPsncnt());
        data.put("phar_psncnt",clinicOffice.getPharPsncnt());
        data.put("dr_psncnt",clinicOffice.getDrPsncnt());
        data.put("nurs_psncnt",clinicOffice.getNursPsncnt());
        data.put("memo","");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
       return mdRequestUtil.getMedicareData(infoNo,jsonObject);
    }

    @Override
    public JSONObject upListCompanyData(List<ClinicOffice> clinicOffice, String infoNo) {
            JSONObject jsonObject = new JSONObject();

        return mdRequestUtil.getMedicareData(infoNo,jsonObject);
    }


    @Override
    public JSONObject delCompanyData(ClinicOffice clinicOffice) {
        JSONObject data = new JSONObject();
        data.put("hosp_dept_codg",clinicOffice.getCode());
        data.put("hosp_dept_name",clinicOffice.getName());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return mdRequestUtil.getMedicareData("3403",jsonObject);
    }

    @Override
    public MdCompanyData getCompanyInfo() {
        RequestData requestData = new RequestData();
        //获取定点医疗机构代码与名称
        String FixmedinsCode = SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode");
        String FixmedinsName = SessionUtils.getUserJson().getJSONObject("company").getString("name");
        requestData.setInfno("5101");
        //发送方id
        String MsgId =  FixmedinsCode + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+ "1234";
        requestData.setMsgid(MsgId);
        //就医医保区划
        requestData.setMdtrtarea_admvs("value4");
        //参保地医保区划
        requestData.setInsuplc_admdvs("value3");
        //接收方系统代码
        requestData.setRecer_sys_code("value5");
        //设备编号
        requestData.setDev_no("value6");
        //设备安全信息
        requestData.setDev_safe_info("value7");
        //医保接入安全码数字签名信息
        requestData.setCainfo("value8");
        //签名类型  建议SM2、SM3
        requestData.setSigntype("value9");
        //接口版本号
        requestData.setInfver("value9");
        //1-经办人；2-自助终端；3-移动终端
        requestData.setOpter_type("value10");
        //传入经办人/终端编号
        requestData.setOpter("value11");
        //经办人姓名
        requestData.setOpter_name("value12");
        //接口交易时间
        requestData.setInf_time("value13");
        //设置定点医疗机构代码与名称
        requestData.setFixmedins_code(FixmedinsCode);
        requestData.setFixmedins_name(FixmedinsName);
        //签到接口
        //从redis中获取签到流水号
        requestData.setSign_no(stringRedisTemplate.opsForValue().get("sign_no"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", JSON.toJSON(requestData));
        return JSONObject.parseObject(mdRequestUtil.getMedicareData("5101",jsonObject).getJSONObject("feedetail").toJSONString(), MdCompanyData.class);
    }

    @Override
    public MdStaffData getStaffInfo(User user) {
        JSONObject data = new JSONObject();
        //执业人员分类
        data.put("prac_psn_type", user.getUserExt());
        //证件类型
        data.put("psn_cert_type", user.getUserExt());
        //证件号码
        data.put("certno", user.getUserExt());
        //人员名称
        data.put("prac_psn_name", user.getUserExt());
        //执业人员代码
        data.put("prac_psn_code", user.getUserExt());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return JSONObject.parseObject(mdRequestUtil.getMedicareData("5102",jsonObject).getString("feedetail"), MdStaffData.class);
    }


}
