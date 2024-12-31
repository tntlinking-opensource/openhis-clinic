package com.geeke.medicareutils.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.geeke.admin.entity.User;
import com.geeke.medicareutils.service.MdCommonService;
import com.geeke.medicareutils.util.MdRequestUtil;
import com.geeke.org.entity.ClinicOffice;
import com.geeke.outpatient.entity.MedicalRecord;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.entity.PatientMdData;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.MedicalRecordService;
import com.geeke.outpatient.service.PatientMdDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MdCommonServiceImpl implements MdCommonService {

     private final MdRequestUtil mdRequestUtil;

     private  final MedicalRecordService medicalRecordService;

     private final PatientMdDataService patientMdDataService;


    /**
     * 冲正数据交易
     * Description
     * 可用交易码：【2101】医生结算、【2102】药店结算、【2103】药店结算撤销、【2207】门诊结算、
     * 【2208】门诊结算撤销、【2304】住院结算、【2305】住院结算撤销、【2401】入院办理、
     * 【2304A】住院结算；
     * @param psnNo 人员编号
     * @param omsgid 原发送方报文id
     * @param oinfno 原交易编号
     */
    @Override
    public JSONObject reversalData(String psnNo,String omsgid,String oinfno) {
        JSONObject data =  new JSONObject();
        data.put("psn_no",psnNo);
        data.put("omsgid",omsgid);
        data.put("oinfno",oinfno);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        return mdRequestUtil.getMedicareData("2601",jsonObject);
    }

    @Override
    public JSONObject upMedicalRecord(Registration registration) {
        //获取人员病历信息
        List< MedicalRecord> record = medicalRecordService.getByOrder(registration.getId());
        MedicalRecord medicalRecord = record.get(0);
        //人员医保信息
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        //科室信息
        ClinicOffice clinicOffice = registration.getClinicOffice();
        //人员信息
        Patient patient = registration.getPatientId();
        //医生信息
        User doctor = registration.getDoctor();

        //入院信息构造
        JSONObject adminfo =  new JSONObject();
        adminfo.put("mdtrt_sn", registration.getId()); // 就医流水号
        adminfo.put("mdtrt_id", registration.getMdtrtId()); // 就诊 ID
        adminfo.put("psn_no", patientMdData.getPsnNo()); // 人员编号
        adminfo.put("mdtrtsn", ""); // 住院号
        adminfo.put("name", patientMdData.getPsnName()); // 姓名
        adminfo.put("gend", patientMdData.getGend()); // 性别
        adminfo.put("age", patientMdData.getAge()); // 年龄
        adminfo.put("adm_rec_no","" ); // 入院记录流水号
        adminfo.put("wardarea_name", ""); // 病区名称
        adminfo.put("dept_code", clinicOffice.getCode()); // 科室代码
        adminfo.put("dept_name", clinicOffice.getName()); // 科室名称
        adminfo.put("bedno", ""); // 病床号
        adminfo.put("adm_time", registration.getCreateDate()); // 入院时间
        adminfo.put("illhis_stte_name", patientMdData.getPsnName()); // 病史陈述者姓名 默认本人
        adminfo.put("illhis_stte_rl", ""); // 陈述者与患者关系代码
        if (Objects.nonNull(patient.getWithPatientNexus())) {
            //有监护人陈述
            adminfo.put("illhis_stte_name", patient.getGuardianName()); // 病史陈述者姓名 默认本人
            adminfo.put("illhis_stte_rl",patient.getWithPatientNexus() ); // 陈述者与患者关系代码
        }
        adminfo.put("stte_rele", ""); // 陈述内容是否可靠标识
        adminfo.put("chfcomp",medicalRecord.getPatientTell()); // 主诉
        adminfo.put("dise_now", medicalRecord.getNowHistory()); // 现病史
        adminfo.put("hlcon", ""); // 健康状况
        adminfo.put("dise_his", medicalRecord.getDiseaseHistory()); // 疾病史（含外伤）
        adminfo.put("ifet",registration.getInfectType()); // TODO 无字典 患者传染性标志
        adminfo.put("ifet_his", medicalRecord.getInfectiousHistory()); // 传染病史
        adminfo.put("prev_vcnt", ""); // 预防接种史
        adminfo.put("oprn_his", medicalRecord.getSurgeryHistory()); // 手术史
        adminfo.put("bld_his",medicalRecord.getTransfusionHistory()); // 输血史
        adminfo.put("algs_his", medicalRecord.getAllergyHistory()); // 过敏史
        adminfo.put("psn_his", medicalRecord.getPersonalHistory()); // 个人史
        adminfo.put("mrg_his", medicalRecord.getPregnancyHistory()); // 婚育史
        adminfo.put("mena_his", medicalRecord.getLunariaHistory()); // 月经史
        adminfo.put("fmhis", medicalRecord.getFamilyHistory()); // 家族史
        adminfo.put("physexm_tprt", registration.getTemperature()); // 体格检查--体温 （℃）
        adminfo.put("physexm_pule", registration.getPulse()); // 体格检查 -- 脉率
        adminfo.put("physexm_vent_frc", registration.getBreathe()); // 体格检查--呼吸频率
        //TODO   体格检查现系统为整体输入
        adminfo.put("physexm_systolic_pre", ""); // 体格检查 -- 收缩压 （mmHg）
        adminfo.put("physexm_dstl_pre", ""); // 体格检查 -- 舒张压 （mmHg）
        adminfo.put("physexm_height", ""); // 体格检查--身高 （cm）
        adminfo.put("physexm_wt", ""); // 体格检查--体重 （kg）
        adminfo.put("physexm_ordn_stas", ""); // 体格检查 -- 一般状 况 检查结果
        adminfo.put("physexm_skin_musl", ""); // 体格检查 -- 皮肤和黏膜检查结果
        adminfo.put("physexm_spef_lymph", ""); // 体格检查 -- 全身浅表淋巴结检查结果
        adminfo.put("physexm_head", ""); // 体格检查 -- 头部及其器官检查结果
        adminfo.put("physexm_neck", ""); // 体格检查 -- 颈部检查结果
        adminfo.put("physexm_chst", ""); // 体格检查 -- 胸部检查结果
        adminfo.put("physexm_abd", ""); // 体格检查 -- 腹部检查结果
        adminfo.put("physexm_finger_exam", ""); // 体格检查 -- 肛门指诊检查结果描述
        adminfo.put("physexm_genital_area", ""); // 体格检查 -- 外生殖器检查结果
        adminfo.put("physexm_spin", ""); // 体格检查 -- 脊柱检查结果
        adminfo.put("physexm_all_fors", ""); // 体格检查 -- 四肢检查结果
        adminfo.put("nersys", ""); // 体格检查 -- 神经系统检查结果
        adminfo.put("spcy_info", ""); // 专科情况
        adminfo.put("asst_exam_rslt", ""); // 辅助检查结果
        adminfo.put("tcm4d_rslt", ""); // 中医“四诊”观察结果描述
        adminfo.put("syddclft", ""); // 辨证分型代码
        adminfo.put("syddclft_name", ""); // 辩证分型名称
        adminfo.put("prnp_trt", ""); // 治则治法

        adminfo.put("rec_doc_code", doctor.getUserExt().getPracPsnCode()); // 接诊医生编号
        adminfo.put("rec_doc_name", doctor.getUserExt().getName()); // 接诊医生姓名
        adminfo.put("ipdr_code", ""); // 住院医师编号
        adminfo.put("ipdr_name", ""); // 住院医师姓名
        adminfo.put("chfdr_code", ""); // 主任医师编号
        adminfo.put("chfdr_name", ""); // 主任医师姓名
        adminfo.put("chfpdr_code", doctor.getUserExt().getPracPsnCode()); // 主诊医师代码
        adminfo.put("chfpdr_name", doctor.getUserExt().getName()); // 主诊医师姓名
        adminfo.put("main_symp", ""); // 主要症状
        adminfo.put("adm_rea", ""); // 入院原因
        adminfo.put("adm_way", ""); // 入院途径
        adminfo.put("apgr", ""); // 评分值(Apgar)
        adminfo.put("diet_info", ""); // 饮食情况
        adminfo.put("growth_deg", ""); // 发育程度
        adminfo.put("mtl_stas_norm", ""); // 精神状态正常标志
        adminfo.put("slep_info", ""); // 睡眠状况
        adminfo.put("sp_info", ""); // 特殊情况
        adminfo.put("mind_info", ""); // 心理状态
        adminfo.put("nurt", ""); // 营养状态
        adminfo.put("self_ablt", ""); // 自理能力
        adminfo.put("nurscare_obsv_item_name", ""); // 护理观察项目名称
        adminfo.put("nurscare_obsv_rslt", ""); // 护理观察结果
        adminfo.put("smoke", ""); // 吸烟标志
        adminfo.put("stop_smok_days", ""); // 停止吸烟天数
        adminfo.put("smok_info", ""); // 吸烟状况
        adminfo.put("smok_day", ""); // 日吸烟量（支）
        adminfo.put("drnk", ""); // 饮酒标志
        adminfo.put("drnk_frqu", ""); // 饮酒频率
        adminfo.put("drnk_day", ""); // 日饮酒量（mL）
        adminfo.put("eval_time", ""); // 评估日期时间
        adminfo.put("resp_nurs_code", ""); // 责任护士编码
        adminfo.put("resp_nurs_name", ""); // 责任护士姓名
        adminfo.put("vali_flag", ""); // 有效标志

        //诊断信息构造
        JSONObject diseinfo = new JSONObject();
        diseinfo.put("inout_diag_type", ""); // 出入院诊断类别
        diseinfo.put("maindiag_flag", ""); // 主诊断标志
        diseinfo.put("diag_seq", ""); // 诊断序列号
        diseinfo.put("diag_time", ""); // 诊断时间
        diseinfo.put("wm_diag_code", ""); // 西医诊断编码
        diseinfo.put("wm_diag_name", ""); // 西医诊断名称
        diseinfo.put("tcm_dise_code", ""); // 中医病名代码
        diseinfo.put("tcm_dise_name", ""); // 中医病名
        diseinfo.put("tcmsymp_code", ""); // 中医证候代码
        diseinfo.put("tcmsymp", ""); // 中医证候
        diseinfo.put("vali_flag", ""); // 有效标志
        //病程记录构造
        JSONObject coursrinfo = new JSONObject();
        coursrinfo.put("codse_rcd_id", ""); // 病程记录编号
        coursrinfo.put("dept_code", ""); // 科室代码
        coursrinfo.put("dept_name", ""); // 科室名称
        coursrinfo.put("wardarea_name", ""); // 病区名称
        coursrinfo.put("bedno", ""); // 病床号
        coursrinfo.put("rcd_time", ""); // 记录日期时间
        coursrinfo.put("chfcomp", ""); // 主诉
        coursrinfo.put("cas_ftur", ""); // 病例特点
        coursrinfo.put("tcm4d_rslt", ""); // 中医“四诊”观察结果
        coursrinfo.put("dise_evid", ""); // 诊断依据
        coursrinfo.put("prel_wm_diag_code", ""); // 初步诊断-西医诊断编码
        coursrinfo.put("prel_wm_dise_name", ""); // 初步诊断-西医诊断名称
        coursrinfo.put("prel_tcm_diag_code", ""); // 初步诊断-中医病名代码
        coursrinfo.put("prel_tcm_dise_name", ""); // 初步诊断-中医病名
        coursrinfo.put("prel_tcmsymp_code", ""); // 初步诊断-中医证候代码
        coursrinfo.put("prel_tcmsymp", ""); // 初步诊断-中医证候
        coursrinfo.put("finl_wm_diag_code", ""); // 鉴别诊断-西医诊断编码
        coursrinfo.put("finl_wm_diag_name", ""); // 鉴别诊断-西医诊断名称
        coursrinfo.put("finl_tcm_dise_code", ""); // 鉴别诊断-中医病名代码
        coursrinfo.put("finl_tcm_dise_name", ""); // 鉴别诊断-中医病名
        coursrinfo.put("finl_tcmsymp_code", ""); // 鉴别诊断-中医证候代码
        coursrinfo.put("finl_tcmsymp", ""); // 鉴别诊断-中医证候
        coursrinfo.put("dise_plan", ""); // 诊疗计划
        coursrinfo.put("prnp_trt", ""); // 治则治法
        coursrinfo.put("ipdr_code", ""); // 住院医师编号
        coursrinfo.put("ipdr_name", ""); // 住院医师姓名
        coursrinfo.put("prnt_doc_name", ""); // 上级医师姓名
        coursrinfo.put("vali_flag", ""); // 有效标志
        //手术记录、病情抢救、死亡记录、出院小结构造 TODO 诊所暂且预留
        JSONObject oprninfo = new JSONObject();
        JSONObject rescinfo = new JSONObject();
        JSONObject dieinfo = new JSONObject();
        JSONObject dscginfo  =  new JSONObject();


        JSONObject jsonObject = new JSONObject();
        //入院信息
        jsonObject.put("adminfo",adminfo);
        //诊断信息
        jsonObject.put("diseinfo",diseinfo);
        //病程记录
        jsonObject.put("coursrinfo",coursrinfo);

        return mdRequestUtil.getMedicareData("4701",jsonObject);
    }


}
