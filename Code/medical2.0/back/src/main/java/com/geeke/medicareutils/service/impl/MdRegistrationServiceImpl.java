package com.geeke.medicareutils.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.geeke.admin.entity.User;
import com.geeke.medicareutils.domain.respo.MdFeeDetail;
import com.geeke.medicareutils.domain.respo.MdPsnDiseData;
import com.geeke.medicareutils.domain.respo.MdPsnVisitData;
import com.geeke.medicareutils.service.MdRegistrationService;
import com.geeke.medicareutils.util.MdRequestUtil;
import com.geeke.org.entity.ClinicOffice;
import com.geeke.outpatient.entity.MedicalRecord;
import com.geeke.outpatient.entity.PatientMdData;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.MedicalRecordService;
import com.geeke.outpatient.service.PatientMdDataService;
import com.geeke.toll.service.TollInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MdRegistrationServiceImpl implements MdRegistrationService {

    private final MdRequestUtil mdRequestUtil;

    private final PatientMdDataService patientMdDataService;
    @Lazy
    @Resource
    private   TollInfoService tollInfoService;
    @Lazy
    @Resource
    private   MedicalRecordService medicalRecordService;



    /**
     * 门诊挂号  2201
     * @param registration
     * @return
     */
    @Override
    public JSONObject getRegistrationInfo(Registration registration) {
        Map<String,Object> data = new HashMap<>();
        //获取患者医保信息
        PatientMdData patientMdData = patientMdDataService.getOne( new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId,registration.getPatientId().getId()));
        //挂号医生信息
        User doctor = registration.getDoctor();
        //挂号科室信息
        ClinicOffice clinicOffice = registration.getClinicOffice();
        //请求信息构造
        data.put("psn_no",patientMdData.getPsnNo());
        //
        data.put("insutype",patientMdData.getInsutype());
        data.put("begntime",registration.getCreateDate());
        //就诊凭证类型
        data.put("mdtrt_cert_type",registration.getCardType());
        // 就诊凭证编号
        data.put("mdtrt_cert_no",registration.getMdtrtCertNo());
        //门诊唯一流水号
        data.put("ipt_otp_no",registration.getId());
        //医师编码
        data.put("atddr_no",doctor.getUserExt().getCreditNum());
        //医师姓名
        data.put("dr_name",doctor.getName());
        data.put("dept_code",clinicOffice.getCode());
        data.put("dept_name",clinicOffice.getName());
        data.put("caty",clinicOffice.getCategory());
        data.put("expContent","");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", JSON.toJSON(data));
        return mdRequestUtil.getMedicareData("2201",jsonObject);
    }

    @Override
    public JSONObject revokeRegistrationInfo(Registration registration) {
        //获取患者医保信息
        PatientMdData patientMdData = patientMdDataService.getOne( new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId,registration.getPatientId().getId()));
        Map<String,Object> data = new HashMap<>();
        data.put("psn_no",patientMdData.getPsnNo());
        data.put("mdtrt_id",registration.getMdtrtId());
        data.put("ipt_otp_no",registration.getId());
        data.put("expContent","");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", JSON.toJSON(data));
        return mdRequestUtil.getMedicareData("2202",jsonObject);
    }

    /**
     * 门诊就诊信息上传
     * @param registration
     * @return
     */
    @Override
    public JSONObject upRegistrationInfo(Registration registration) {
        //单行就诊信息
        JSONObject data = new JSONObject();
        //诊断多行数据
        JSONArray jsonArray = new JSONArray();
        //患者医保信息
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        //患者科室信息
        ClinicOffice clinicOffice = registration.getClinicOffice();
        //患者医生信息
        User doctor = registration.getDoctor();
        //人员病历信息
        List<MedicalRecord> record = medicalRecordService.getByOrder(registration.getId());
        MedicalRecord medicalRecord = record.get(0);
        //就诊参数
        data.put("mdtrt_id", registration.getMdtrtId());
        data.put("psn_no", patientMdData.getPsnNo());
        data.put("med_type", registration.getMedType().getValue());
        data.put("begntime", registration.getCreateDate());
        //主要病情描述 TODO
        data.put("main_cond_dscr", "");
        //病种编码    TODO
        data.put("dise_code", "");
        //病种名称 TODO
        data.put("dise_name", "");
        data.put("birctrl_type", "");
        data.put("bitctrl_matn_date", "");
        data.put("matn_type", "");
        data.put("geso_val", "");
        data.put("expContent", "");

        //诊断参数
        JSONObject array = new JSONObject();
        //诊断类别 1西医诊断 2中医主病诊断 3中医主证诊断 4手术操作 5.安病种付费病种 TODO 只允许一个诊断
        if (Objects.nonNull(medicalRecord.getWesternDiagnose())) {
            //西医
            array.put("diag_type", "1");
            array.put("diag_name", medicalRecord.getWesternDiagnose());

        }
        if (Objects.nonNull(medicalRecord.getChinaDiagnose())) {
            //中医
            array.put("diag_type", "2");
            array.put("diag_name", medicalRecord.getChinaDiagnose());
        }
        array.put("diag_srt_no", "0");
        array.put("diag_dept", clinicOffice.getName());
        array.put("dise_dor_no", doctor.getUserExt().getPracPsnCode());
        array.put("dise_dor_name", doctor.getUserExt().getName());
        array.put("diag_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(medicalRecord.getCreateDate()));
        //TODO 默认有效 1, 0无效
        array.put("vali_flag", "1");
        jsonArray.add(array);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mdtrtinfo", data);
        jsonObject.put("diseinfo", jsonArray);
        return mdRequestUtil.getMedicareData("2203", jsonObject);
    }

    /**
     * @param registration
     * @return  门诊信息上传A 2203A
     */
    @Override
    public JSONObject upRegistrationInfoList(Registration registration) {
        // 创建外层 JSONObject
        JSONObject jsonObject = new JSONObject();

        //患者医保信息
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        //患者科室信息
        ClinicOffice clinicOffice = registration.getClinicOffice();
        //患者医生信息
        User doctor = registration.getDoctor();
        //人员病历信息
        List<MedicalRecord> record = medicalRecordService.getByOrder(registration.getId());
        MedicalRecord medicalRecord = record.get(0);

        // 创建 mdtrtinfo 对象
        JSONObject mdtrtinfo = new JSONObject();
        mdtrtinfo.put("mdtrt_id", registration.getMdtrtId());
        mdtrtinfo.put("psn_no", patientMdData.getPsnNo());
        mdtrtinfo.put("med_type", registration.getMedType());
        mdtrtinfo.put("begntime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        mdtrtinfo.put("main_cond_dscr", "");
        mdtrtinfo.put("dise_codg", "");
        mdtrtinfo.put("dise_name", "");
        mdtrtinfo.put("birctrl_type", "");
        mdtrtinfo.put("birctrl_matn_date", "");
        mdtrtinfo.put("matn_type", "");
        mdtrtinfo.put("geso_val", 0);
        mdtrtinfo.put("expContent", "");

        // 将 mdtrtinfo 添加到主对象
        jsonObject.put("mdtrtinfo", mdtrtinfo);

        // 创建 diseinfo 数组
        JSONArray diseinfoArray = new JSONArray();
        JSONObject diseinfo = new JSONObject();
        diseinfo.put("diag_type", "1");
        diseinfo.put("diag_srt_no", "1");
        diseinfo.put("diag_code", medicalRecord.getDiagnosisCode()); //诊断代码
        diseinfo.put("diag_name", medicalRecord.getDiagnose());
        diseinfo.put("diag_dept", clinicOffice.getName());
        diseinfo.put("dise_dor_no", doctor.getUserExt().getPracPsnCode());
        diseinfo.put("dise_dor_name", doctor.getName());
        diseinfo.put("diag_time", medicalRecord.getCreateDate());
        diseinfo.put("vali_flag", "1");

        // 将 diseinfo 对象添加到数组
        diseinfoArray.add(diseinfo);

        // 将 diseinfoArray 添加到主对象
        jsonObject.put("diseinfo", diseinfoArray);
        return mdRequestUtil.getMedicareData("2203A", jsonObject);
    }

    /**
     * 门诊费用明细信息上传  2204
     * @param
     * @return
     */
    @Override
    public MdFeeDetail upRegistrationMoneyInfo(JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("feedetail",jsonArray);
        return JSONObject.parseObject(mdRequestUtil.getMedicareData("2204", jsonObject).getString("result"), MdFeeDetail.class);
    }

    /**
     * 门诊明细上传撤销 2205
     *
     * @param mdtrtId
     * @param chrgBchno
     * @param psnNo
     * @param expContent
     * @return
     */
    @Override
    public JSONObject revokeRegistrationMoneyInfo(String mdtrtId, String chrgBchno, String psnNo, String expContent) {
        JSONObject data = new JSONObject();
        data.put("mdtrt_id", mdtrtId);
        data.put("chrg_bchno", chrgBchno);//000则取消全部
        data.put("psn_no", psnNo);
        data.put("expContent", expContent);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return mdRequestUtil.getMedicareData("2205", jsonObject);
    }

    /**
     * 门诊预结算 2206
     *
     * @param registration 挂号信息
     * @param mdFeeDetail  收费信息
     * @return
     */
    @Override
    public JSONObject processOutpatientPreSettlement(Registration registration, MdFeeDetail mdFeeDetail,String chrgBchno,String acctUsedFlag) {
        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        data.put("psn_no", patientMdData.getPsnNo());
        //就诊凭证类型 TODO
        data.put("mdtrt_cert_type","");
        //“01”时填写电子凭证 令牌，为“02”时填写身份证号，为“03”时填写社会保障卡卡号
        data.put("mdtrt_cert_no","");
        //医疗类别
        data.put("med_type", registration.getMedType().getValue());
        data.put("medfee_sumamt", mdFeeDetail.getDetItemFeeSumamt());
        data.put("psn_setlway","01");//01 按项目结算  20 按定额结算
        data.put("mdtrt_id",registration.getMdtrtId());
        data.put("chrg_bchno",chrgBchno);
        //个人账号使用标志 0否1是
        data.put("acc_used_flag",acctUsedFlag);
        //险种类型
        data.put("insutype",patientMdData.getInsutype());
        data.put("expContent","");
        jsonObject.put("data",data);
        return mdRequestUtil.getMedicareData("2206",jsonObject);
    }

    /**
     * 门诊结算
     *
     * @param registration
     * @param mdFeeDetail
     * @param chrgBchno
     * @param acctUsedFlag
     */
    @Override
    public JSONObject executeOutpatientPreSettlement(Registration registration, MdFeeDetail mdFeeDetail, String chrgBchno, String acctUsedFlag) {
        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        data.put("psn_no", patientMdData.getPsnNo());
        //就诊凭证类型 TODO
        data.put("mdtrt_cert_type","");
        //“01”时填写电子凭证 令牌，为“02”时填写身份证号，为“03”时填写社会保障卡卡号
        data.put("mdtrt_cert_no","");
        //医疗类别
        data.put("med_type", registration.getMedType().getValue());

        data.put("medfee_sumamt", mdFeeDetail.getDetItemFeeSumamt());
        data.put("psn_setlway","01");
        data.put("mdtrt_id",registration.getMdtrtId());
        data.put("chrg_bchno",chrgBchno);
        //个人账号使用标志 0否1是
        data.put("acc_used_flag",acctUsedFlag);
        //险种类型
        data.put("insutype",patientMdData.getInsutype());
        //TODO 发票号
        data.put("invono","");
        data.put("fulamt_ownpay_amt", mdFeeDetail.getFulamtOwnpayAmt());
        data.put("overlmt_selfpay", mdFeeDetail.getOverlmtAmt());
        data.put("preselfpay_amt", mdFeeDetail.getPreselfpayAmt());
        data.put("inscp_scp_amt", mdFeeDetail.getInscpScpAmt());
        data.put("expContent","");
        jsonObject.put("data",data);
        return mdRequestUtil.getMedicareData("2207",jsonObject);
    }

    /**
     * 门诊结算撤销
     *
     * @return
     */
    @Override
    public JSONObject revokeOutpatientSettlement(String setlId,String psnNo,String mdtrtId ) {
             JSONObject data = new JSONObject();
             JSONObject jsonObject = new JSONObject();
             data.put("setl_id",setlId);
             data.put("psn_no",psnNo);
             data.put("mdtrt_id",mdtrtId);
             jsonObject.put("data",data);
        return mdRequestUtil.getMedicareData("2208",jsonObject);
    }


    /**
     * 人员就诊信息
     * @param registration
     * @param begntime
     * @param endtime
     * @return
     */
    @Override
    public MdPsnVisitData getPsnVisitData(Registration registration, LocalDateTime begntime, LocalDateTime endtime) {
        JSONObject data = new JSONObject();
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        data.put("psn_no",patientMdData.getPsnNo());
        data.put("begntime",begntime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        data.put("endtime",endtime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        data.put("med_type",registration.getMedType().getValue());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", JSON.toJSON(data));
        return   JSONObject.parseObject(mdRequestUtil.getMedicareData("5201",jsonObject).getString("mdtrtinfo"),MdPsnVisitData.class) ;
    }

    @Override
    public MdPsnDiseData getPsnDiseData(Registration registration) {
        JSONObject data = new JSONObject();
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        data.put("psn_no",patientMdData.getPsnNo());
        data.put("mdtrt_id",registration.getMdtrtId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", JSON.toJSON(data));
        return  JSONObject.parseObject(mdRequestUtil.getMedicareData("5202",jsonObject).getString("diseinfo"),MdPsnDiseData.class);
    }

    /**
     * 结算信息查询
     *
     * @param psnNo
     * @param setlId
     * @param mdtrtId
     * @return
     */
    @Override
    public JSONObject getSettlementInfo(String psnNo, String setlId, String mdtrtId) {
        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        data.put("setl_id",setlId);
        data.put("psn_no",psnNo);
        data.put("mdtrt_id",mdtrtId);
        jsonObject.put("data",data);
        return mdRequestUtil.getMedicareData("5203",jsonObject);

    }

    /**
     * 费用明细查询
     *
     * @param psnNo
     * @param setlId
     * @param mdtrtId
     * @return
     */
    @Override
    public JSONObject getExpenseDetails(String psnNo, String setlId, String mdtrtId) {
        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        data.put("setl_id",setlId);
        data.put("psn_no",psnNo);
        data.put("mdtrt_id",mdtrtId);
        jsonObject.put("data",data);
        return mdRequestUtil.getMedicareData("5204",jsonObject);
    }

    /**
     * 门急诊诊疗记录
     *
     * @param registration 挂号信息
     * @return
     */
    @Override
    public JSONObject getEmergencyOutpatientRecords(Registration registration) {
        JSONObject rgstinfo = new JSONObject();
        JSONObject caseinfo = new JSONObject();
        JSONObject diseinfo = new JSONObject();
        JSONObject rxinfo = new JSONObject();
        //挂号
        rgstinfo.put("mdtrt_sn", "");               // 就医流水号
        rgstinfo.put("mdtrt_id", "");               // 就诊 ID
        rgstinfo.put("psn_no", "");                 // 人员编号
        rgstinfo.put("rgst_type_code", "");         // 挂号类别代码
        rgstinfo.put("rgst_way_code", "");          // 挂号方式代码
        rgstinfo.put("rgst_serv_fee", "");          // 挂号费/医事服务费
        rgstinfo.put("ordr_way_code", "");          // 预约途径代码
        rgstinfo.put("retnr_flag", "");             // 退号标志
        rgstinfo.put("fstdiag_flag", "");           // 初诊标志
        rgstinfo.put("mdtrt_flag", "");             // 就诊标志
        rgstinfo.put("rgst_retnr_time", "");        // 挂号/退号时间
        rgstinfo.put("medfee_paymtd_code", "");    // 医疗费用支付方式代码
        rgstinfo.put("vali_flag", "");             // 有效标志
        //病历信息
        caseinfo.put("mdtrt_date", "");             // 就诊日期
        caseinfo.put("chfcomp", "");                 // 主诉
        caseinfo.put("attk_date_time", "");         // 发病日期时间
        caseinfo.put("mdtrt_rea", "");               // 就诊原因
        caseinfo.put("illhis", "");                  // 病史
        caseinfo.put("algs", "");                    // 过敏史
        caseinfo.put("aise_code", "");              // 过敏源代码
        caseinfo.put("phex", "");                    // 查体
        caseinfo.put("disa_info_code", "");         // 残疾情况代码
        caseinfo.put("symp_name", "");              // 症状名称
        caseinfo.put("symp_code", "");              // 症状代码
        caseinfo.put("dspo_opnn", "");               // 处置意见
        caseinfo.put("dept_code", "");               // 科室代码
        caseinfo.put("dept_name", "");               // 科室名称
        caseinfo.put("vali_flag", "");               // 有效标志
        //诊断
        diseinfo.put("tcm_diag_flag", "");        // 中医诊断标志
        diseinfo.put("maindiag_flag", "");        // 主诊断标志
        diseinfo.put("diag_code", "");            // 诊断代码
        diseinfo.put("diag_name", "");            // 诊断名称
        diseinfo.put("tcm_dise_code", "");        // 中医病名代码
        diseinfo.put("tcm_dise_name", "");        // 中医病名名称
        diseinfo.put("tcmsymp_code", "");         // 中医证候代码
        diseinfo.put("tcmsymp", "");               // 中医证候
        diseinfo.put("vali_flag", "");           // 有效标志
        //处方
        rxinfo.put("rxno", "");                          // 处方号
        rxinfo.put("rx_prsc_time","");          // 处方开方时间
        rxinfo.put("rx_type_code", "");                  // 处方类别代码
        rxinfo.put("rx_item_type_code", "");             // 处方项目分类代码
        rxinfo.put("rx_item_type_name", "");             // 处方项目分类名称
        rxinfo.put("rx_detl_id", "");                    // 处方明细代码
        rxinfo.put("rx_detl_name", "");                  // 处方明细名称
        rxinfo.put("tcmdrug_type_name", "");             // 中药类别名称
        rxinfo.put("tcmdrug_type_code", "");             // 中药类别代码
        rxinfo.put("tcmherb_foote", "");                 // 草药脚注
        rxinfo.put("medn_type_code", "");                // 药物类型代码
        rxinfo.put("medn_type_name", "");                // 药物类型
        rxinfo.put("drug_dosform", "");                  // 药品剂型代码
        rxinfo.put("drug_dosform_name", "");             // 药品剂型名称
        rxinfo.put("drug_spec", "");                      // 药品规格
        rxinfo.put("drug_used_frqu", "");                // 药物使用-频率
        rxinfo.put("drug_used_idose", 0.0);              // 药物使用-总剂量
        rxinfo.put("drug_used_sdose", 0);                // 药物使用-次剂量
        rxinfo.put("drug_used_dosunt", "");              // 药物使用-剂量单位
        rxinfo.put("drug_used_way_code", "");            // 药物使用-途径代码
        rxinfo.put("drug_medc_way", "");                 // 药物使用-途径
        rxinfo.put("skintst_dicm", "");                  // 皮试判别
        rxinfo.put("medc_begntime", "");         // 用药开始时间
        rxinfo.put("medc_endtime", "");          // 用药停止时间
        rxinfo.put("medc_days", 0);                       // 用药天数
        rxinfo.put("main_medc_flag", "");                // 主要用药标志
        rxinfo.put("urgt_flag", "");                      // 加急标志
        rxinfo.put("unif_purc_drug", "");                // 统一采购药品
        rxinfo.put("drug_purc_code", "");                // 药品采购代码
        rxinfo.put("drug_mgt_plaf_code", "");            // 药品管理平台代码
        rxinfo.put("bas_medn_flag", "");                 // 基本药物标志
        rxinfo.put("vali_flag", "");                     // 有效标志
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rgstinfo", rgstinfo);
        jsonObject.put("caseinfo", caseinfo);
        jsonObject.put("diseinfo", diseinfo);
        jsonObject.put("rxinfo", rxinfo);

        return mdRequestUtil.getMedicareData("4301", jsonObject);
    }

    /**
     * 明细审核事前分析服务
     *
     * @param registration
     * @return
     */
    @Override
    public JSONObject analyzeDetailReviewPreCheck(Registration registration) {
        //患者医保信息
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        //患者科室信息
        ClinicOffice clinicOffice = registration.getClinicOffice();
        //患者医生信息
        User doctor = registration.getDoctor();
        //人员病历信息
        List<MedicalRecord> record = medicalRecordService.getByOrder(registration.getId());
        MedicalRecord medicalRecord = record.get(0);

        // 创建 data JSON 对象
        JSONObject data = new JSONObject();

        JSONArray patientDtos = new JSONArray();
        JSONObject patientDto = new JSONObject();

        patientDto.put("patn_id", "");
        patientDto.put("patn_name", "");
        patientDto.put("gend", "");
        patientDto.put("brdy", "");
        patientDto.put("poolarea", "");
        patientDto.put("curr_mdtrt_id", "");

        JSONArray encounterDtos = new JSONArray();
        JSONObject encounterDto = new JSONObject();

        encounterDto.put("mdtrt_id", "");
        encounterDto.put("medins_id", "");
        encounterDto.put("medins_name", "");
        encounterDto.put("medins_admdvs", "");
        encounterDto.put("medins_type", "");
        encounterDto.put("medins_lv", "");
        encounterDto.put("wardarea_codg", "");
        encounterDto.put("wardno", "");
        encounterDto.put("bedno", "");
        encounterDto.put("adm_date", "");
        encounterDto.put("dscg_date", "");
        encounterDto.put("dscg_main_dise_codg", "");
        encounterDto.put("dscg_main_dise_name", "");

        JSONArray diagnoseDtos = new JSONArray();
        JSONObject diagnoseDto = new JSONObject();

        diagnoseDto.put("dise_id", "");
        diagnoseDto.put("inout_dise_type", "");
        diagnoseDto.put("maindise_flag", "");
        diagnoseDto.put("dias_srt_no", "");
        diagnoseDto.put("dise_codg", "");
        diagnoseDto.put("dise_name", "");
        diagnoseDto.put("dise_date", "");
        diagnoseDtos.add(diagnoseDto);
        encounterDto.put("fsi_diagnose_dtos", diagnoseDtos);

        encounterDto.put("dr_codg", "");
        encounterDto.put("adm_dept_codg", "");
        encounterDto.put("adm_dept_name", "");
        encounterDto.put("dscg_dept_codg", "");
        encounterDto.put("dscg_dept_name", "");
        encounterDto.put("med_mdtrt_type", "");
        encounterDto.put("med_type", "");

        JSONArray orderDtos = new JSONArray();
        JSONObject orderDto = new JSONObject();

        orderDto.put("rx_id", "");
        orderDto.put("rxno", "");
        orderDto.put("grpno", "");
        orderDto.put("long_drord_flag", "");
        orderDto.put("hilist_type", "");
        orderDto.put("chrg_type", "");
        orderDto.put("drord_bhvr", "");
        orderDto.put("hilist_code", "");
        orderDto.put("hilist_name", "");
        orderDto.put("hilist_dosform", "");
        orderDto.put("hilist_lv", "");
        orderDto.put("hilist_pric", "");
        orderDto.put("lv1_hosp_item_pric", "");
        orderDto.put("lv2_hosp_item_pric", "");
        orderDto.put("lv3_hosp_item_pric", "");
        orderDto.put("hilist_memo", "");
        orderDto.put("hosplist_code", "");
        orderDto.put("hosplist_name", "");
        orderDto.put("hosplist_dosform", "");
        orderDto.put("cnt", "");
        orderDto.put("pric", "");
        orderDto.put("sumamt", "");
        orderDto.put("ownpay_amt", "");
        orderDto.put("selfpay_amt", "");
        orderDto.put("spec", "");
        orderDto.put("spec_unt", "");
        orderDto.put("drord_begn_date", "");
        orderDto.put("drord_stop_date", "");
        orderDto.put("drord_dept_codg", "");
        orderDto.put("drord_dept_name", "");
        orderDto.put("drord_dr_codg", "");
        orderDto.put("drord_dr_name", "");
        orderDto.put("drord_dr_profttl", "");
        orderDto.put("curr_drord_flag", "");
        orderDtos.add(orderDto);

        encounterDto.put("fsi_order_dtos", orderDtos);
        encounterDto.put("matn_stas", "");
        encounterDto.put("medfee_sumamt", "");
        encounterDto.put("ownpay_amt", "");
        encounterDto.put("selfpay_amt", "");
        encounterDto.put("acct_payamt", "");
        encounterDto.put("ma_amt", "");
        encounterDto.put("hifp_payamt", "");
        encounterDto.put("setl_totlnum", "");
        encounterDto.put("insutype", "");
        encounterDto.put("reim_flag", "");
        encounterDto.put("out_setl_flag", "");
        encounterDto.put("fsi_operation_dtos", "");

        encounterDtos.add(encounterDto);
        patientDto.put("fsi_encounter_dtos", encounterDtos);

        patientDtos.add(patientDto);
        data.put("patient_dtos", patientDtos);
        data.put("rule_ids", null);
        data.put("trig_scen", "");
        data.put("syscode", null);
        JSONObject  jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return mdRequestUtil.getMedicareData("3301", jsonObject);
    }

    /**
     * 明细审核事中分析服务
     *
     * @param registration 3102
     * @return
     */
    @Override
    public JSONObject analyzeDetailReviewDuringProcess(Registration registration) {
        JSONObject jsonObject = new JSONObject();
        JSONObject patientDto = new JSONObject();
        //患者医保信息
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        //患者科室信息
        ClinicOffice clinicOffice = registration.getClinicOffice();
        //患者医生信息
        User doctor = registration.getDoctor();
        //人员病历信息
        List<MedicalRecord> record = medicalRecordService.getByOrder(registration.getId());
        MedicalRecord medicalRecord = record.get(0);

        patientDto.put("patn_id", "");
        patientDto.put("patn_name", "");
        patientDto.put("gend", "");
        patientDto.put("brdy", "");
        patientDto.put("poolarea", "");
        patientDto.put("curr_mdtrt_id", "");

        // 创建就诊信息 JSON 对象
        JSONObject encounterDto = new JSONObject();
        encounterDto.put("mdtrt_id", "");
        encounterDto.put("medins_id", "");
        encounterDto.put("medins_name", "");
        encounterDto.put("medins_admdvs", "");
        encounterDto.put("medins_type", "");
        encounterDto.put("medins_lv", "");
        encounterDto.put("wardarea_codg", "");
        encounterDto.put("wardno", "");
        encounterDto.put("bedno", "");
        encounterDto.put("adm_date", "");
        encounterDto.put("dscg_date", "");
        encounterDto.put("dscg_main_dise_codg", "");
        encounterDto.put("dscg_main_dise_name", "");

        // 创建诊断信息 JSON 数组
        JSONArray diagnoseDtos = new JSONArray();
        JSONObject diagnoseDto = new JSONObject();
        diagnoseDto.put("dise_id", "");
        diagnoseDto.put("inout_dise_type", "");
        diagnoseDto.put("maindise_flag", "");
        diagnoseDto.put("dias_srt_no", "");
        diagnoseDto.put("dise_codg", "");
        diagnoseDto.put("dise_name", "");
        diagnoseDto.put("dise_date", "");
        diagnoseDtos.add(diagnoseDto);
        encounterDto.put("fsi_diagnose_dtos", diagnoseDtos);

        encounterDto.put("dr_codg", "");
        encounterDto.put("adm_dept_codg", "");
        encounterDto.put("adm_dept_name", "");
        encounterDto.put("dscg_dept_codg", "");
        encounterDto.put("dscg_dept_name", "");
        encounterDto.put("med_mdtrt_type", "");
        encounterDto.put("med_type", "");

        // 创建医嘱信息 JSON 数组
        JSONArray orderDtos = new JSONArray();
        JSONObject orderDto = new JSONObject();
        orderDto.put("rx_id", "");
        orderDto.put("rxno", "");
        orderDto.put("grpno", "");
        orderDto.put("long_drord_flag", "");
        orderDto.put("hilist_type", "");
        orderDto.put("chrg_type", "");
        orderDto.put("drord_bhvr", "");
        orderDto.put("hilist_code", "");
        orderDto.put("hilist_name", "");
        orderDto.put("hilist_dosform", "");
        orderDto.put("hilist_lv", "");
        orderDto.put("hilist_pric", "");
        orderDto.put("lv1_hosp_item_pric", "");
        orderDto.put("lv2_hosp_item_pric", "");
        orderDto.put("lv3_hosp_item_pric", "");
        orderDto.put("hilist_memo", "");
        orderDto.put("hosplist_code", "");
        orderDto.put("hosplist_name", "");
        orderDto.put("hosplist_dosform", "");
        orderDto.put("cnt", "");
        orderDto.put("pric", "");
        orderDto.put("sumamt", "");
        orderDto.put("ownpay_amt", "");
        orderDto.put("selfpay_amt", "");
        orderDto.put("spec", "");
        orderDto.put("spec_unt", "");
        orderDto.put("drord_begn_date", "");
        orderDto.put("drord_stop_date", "");
        orderDto.put("drord_dept_codg", "");
        orderDto.put("drord_dept_name", "");
        orderDto.put("drord_dr_codg", "");
        orderDto.put("drord_dr_name", "");
        orderDto.put("drord_dr_profttl", "");
        orderDto.put("curr_drord_flag", "");
        orderDtos.add(orderDto);
        encounterDto.put("fsi_order_dtos", orderDtos);

        encounterDto.put("matn_stas", "");
        encounterDto.put("medfee_sumamt", "");
        encounterDto.put("ownpay_amt", "");
        encounterDto.put("selfpay_amt", "");
        encounterDto.put("acct_payamt", "");
        encounterDto.put("ma_amt", "");
        encounterDto.put("hifp_payamt", "");
        encounterDto.put("setl_totlnum", "");
        encounterDto.put("insutype", "");
        encounterDto.put("reim_flag", "");
        encounterDto.put("out_setl_flag", "");
        encounterDto.put("fsi_operation_dtos", "");

        // 将就诊信息添加到患者信息中
        JSONArray encounterDtos = new JSONArray();
        encounterDtos.add(encounterDto);
        patientDto.put("fsi_encounter_dtos", encounterDtos);
        patientDto.put("fsi_his_data_dto", "");

        // 将患者信息添加到 data 对象中
        JSONObject data = new JSONObject();
        JSONArray patientDtos = new JSONArray();
        patientDtos.add(patientDto);
        data.put("patient_dtos", patientDtos);
        data.put("rule_ids", "");
        data.put("trig_scen", "");
        data.put("syscode", "");
        jsonObject.put("data", data);
         return mdRequestUtil.getMedicareData("3102", jsonObject);
    }

    /**
     * 医药机构费用结算对总账
     *
     * @return
     */
    @Override
    public JSONObject matchPharmacyCostsWithGeneralLedger() {
        return null;
    }


    /**
     * 医药机构费用结算对明细账
     *
     * @return
     */
    @Override
    public JSONObject matchPharmacyCostsWithDetailAccounts() {
        return null;
    }
}
