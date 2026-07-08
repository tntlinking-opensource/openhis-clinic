package com.geeke.medicareutils.service.impl;
import com.geeke.common.constants.BizConstants;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.geeke.admin.entity.User;
import com.geeke.admin.service.UserService;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.medicareutils.db.entity.DzcfD001Output;
import com.geeke.medicareutils.db.entity.DzcfD002Output;
import com.geeke.medicareutils.db.entity.DzcfD003Output;
import com.geeke.medicareutils.db.service.DzcfD001OutputService;
import com.geeke.medicareutils.db.service.DzcfD002OutputService;
import com.geeke.medicareutils.db.service.DzcfD003OutputService;
import com.geeke.medicareutils.service.MdMedicationOrderService;
import com.geeke.medicareutils.util.MdRequestUtil;
import com.geeke.outpatient.entity.*;

import com.geeke.outpatient.service.*;

import com.geeke.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.context.WebServerApplicationContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @Description 电子处方流转实现类
 * @Author Hzx
 * @Date 2024/10/30
 */
@Service
@RequiredArgsConstructor
public class MdMedicationOrderImpl implements MdMedicationOrderService {


    private  final MdRequestUtil mdRequestUtil;

    private  final WebServerApplicationContext webServerApplicationContext;
    private final static String PRESCRIPTION_URL_XML = "/api/ureport/pdf?_u=Newtouch:chronicDisease.ureport.xml&_t=0&recipelInfoId=";

    private final PresDrugService presDrugService;

    private  final RegistrationService registrationService;

    private  final RecipelDetailService recipelDetailService;

    private final PatientMdDataService patientMdDataService;

    private final XtZdService xtZdService;

    private final XtZyzhService xtZyzhService;

    private  final DzcfD001OutputService dzcfD001OutputService;

    private  final DzcfD002OutputService dzcfD002OutputService;

    private  final DzcfD003OutputService dzcfD003OutputService;

    private final UserService userService;

    private final MedicareConfigProperties  properties ;


    /**【Ld7801】电子处方上传预核验
     * @return
     */
    @Override
    public JSONObject validateElectronicPrescriptionUpload_Ld7801(RecipelInfoReview recipelInfoReview) {
        JSONObject data = new JSONObject(true);
        RecipelInfo recipelInfo = recipelInfoReview.getRecipelInfo();
        Registration registration = recipelInfo.getRegistration();
        MedicalRecord medicalRecord = recipelInfoReview.getMedicalRecord();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        User doctor = userService.get(registration.getDoctor().getId());
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        List<RecipelDetail> recipelDetail = recipelDetailService.getRecipelDetail(recipelInfo.getId());
        data.put("mdtrtCertType", registration.getCardType());  // 就诊凭证类型，值可能为 01、02 或 03
        // 就诊凭证号
        data.put("mdtrtCertNo", registration.getCard());  // 就诊凭证号，若凭证类型为03则为社会保障卡卡号
        // 卡识别码（电子凭证令牌）
        data.put("cardSn", "");  // TODO 必填，电子凭证令牌，长度32字符
        // 电子凭证线下场景用身份核验 TODO
        data.put("ecToken", "");  // 必填，线上场景互联网医院问诊时使用
        // 流水号
        data.put("authNo", "");  // 必填，用于身份核验，类型为流水号，100字符
        // 业务类型代码
        data.put("bizTypeCode", "01");  // 必填，01为定点医疗机构就诊，02为互联网医院问诊
        // 参保地编号，异地参保人必传
        data.put("insuPlcNo", patientMdData.getInsuplcAdmdvs());  // 必填，参保地编号，6字符
        // 定点医疗机构处方编号
        data.put("hospRxno", recipelInfo.getCode());  // 必填，定点医疗机构的处方编号，最大40字符
        // 续方的原处方编号
        data.put("initRxno", "");  // 非必填，续方的原处方编号，最大40字符
        // 处方类别代码 TODO 预留具体诊所具体更改
        if(BizConstants.RECIPEL_TYPE_WESTERN.equals(recipelInfo.getRecipelType().getValue())){
            //西药
            data.put("rxTypeCode", "1");
            // 药品类目数（剂数）
            data.put("rxDrugCnt",recipelDetail.size() );  // 必填，非中药时为处方药品类目数量，中药时为药品总剂数
        } else if (BizConstants.RECIPEL_TYPE_CHINESE.equals(recipelInfo.getRecipelType().getValue())) {
            //中药
            data.put("rxTypeCode", "2");
            // 药品类目数（剂数）
            data.put("rxDrugCnt", recipelInfo.getDosage());  // 必填，非中药时为处方药品类目数量，中药时为药品总剂数
        }
        // 开方时间
        data.put("prscTime", recipelInfo.getCreateDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(outputFormatter));  // 必填，开方时间，日期时间格式：yyyy-MM-dd HH:mm:ss
        // 处方整剂用法编号
        data.put("rxUsedWayCodg", "");  // 必填，参考药物使用途径代码（drug_medc_way_code）
        // 处方整剂用法名称
        data.put("rxUsedWayName", "");  // 必填，处方整剂用法的名称
        // 处方整剂频次编号
        data.put("rxFrquCodg", "");  // 必填，参考使用频次（used_frqu）
        // 处方整剂频次名称
        data.put("rxFrquName", "");  // 必填，处方整剂频次名称
        // 处方整剂剂量单位
        data.put("rxDosunt", "");  // 必填，处方整剂的剂量单位
        // 处方整剂单次剂量数
        data.put("rxDoscnt", "");  // 必填，处方整剂单次剂量数
        // 处方整剂医嘱说明
        data.put("rxDrordDscr", "");  // 必填，处方整剂的医嘱说明
        // 处方有效天数
        data.put("valiDays", "2");  // 必填，处方的有效天数
        // 处方有效截止时间
        data.put("valiEndTime", "");  // 必填，计算得到的有效截止时间（开方时间+有效天数）
        // 复用（多次）使用标志
        data.put("reptFlag", "");  // 必填，0为否，1为是，表示是否可复用
        // 最大使用次数
        data.put("maxReptCnt", "");  // 必填，处方的最大使用次数
        // 已使用次数
        data.put("reptdCnt", "");  // 必填，已使用次数
        // 使用最小间隔（天数）
        data.put("minInrvDays", "");  // 必填，表示使用处方的最小时间间隔（天数）
        // 续方标志
        data.put("rxCotnFlag", "");  // 必填，0为否，1为是，表示是否为续方
        // 长期处方标志
        data.put("longRxFlag","");  // 必填，0为否，1为是，表示是否为长期处方

        //处方明细详情
        JSONArray rxdrugdetail = new JSONArray();
        recipelDetail.forEach(item -> {
            //获取电子处方药品信息
             PresDrug presDrug = presDrugService.getById(item.getDrugStuffId().getDrugStuffId());
             item.setPresDrug(presDrug);
             JSONObject rxdrug = new JSONObject(true);
            // 医疗目录编码
            rxdrug.put("medListCodg", presDrug.getMedlistcodg());  // 必填，医保药品编码
            // 定点医药机构目录编号
            rxdrug.put("fixmedinsHilistId", "1");  // 必填，院内药品编码
            // 医疗机构制剂标志
            rxdrug.put("hospPrepFlag", "");  // 必填，0-否、1-是，默认否
            if(BizConstants.RECIPEL_TYPE_WESTERN.equals(recipelInfo.getRecipelType().getValue())){
                //西药
                rxdrug.put("rxItemTypeCode", "11");
                rxdrug.put("medcWayCodg",item.getWesternMedicineUse() );  // 必填，参考药物使用途径代码
                //中成药
            } else if (BizConstants.RECIPEL_TYPE_CHINESE.equals(recipelInfo.getRecipelType().getValue())) {
                //中药
                rxdrug.put("rxItemTypeCode", "13");  // 必填，参考处方项目分类代码
                rxdrug.put("tcmdrugTypeCode", "9");  //TODO 对接使用 必填，参考处方项目分类代码，中药处方必填
                rxdrug.put("medcWayCodg",item.getChineseMedicineUse() );  // 必填，参考药物使用途径代码
            }
            // 处方项目分类名称
            rxdrug.put("rxItemTypeName", "");  // 必填，处方项目分类名称
            // 中药类别名称
            rxdrug.put("tcmdrugTypeName", "");  // 必填，中药类别名称
            // 草药脚注
            rxdrug.put("tcmherbFoote", "");  // 草药脚注
            // 药物类型代码
            rxdrug.put("mednTypeCode", "");  // 必填，参考药物类型代码
            // 药物类型名称
            rxdrug.put("mednTypeName", presDrug.getDosformname());  // 药物类型名称
            // 主要用药标志
            rxdrug.put("mainMedcFlag", "");  // 必填，0-否、1-是
            // 加急标志
            rxdrug.put("urgtFlag", "");  // 必填，0-否、1-是
            // 基本药物标志
            rxdrug.put("basMednFlag", "");  // 必填，0-否、1-是
            // 是否进口药品
            rxdrug.put("impDrugFlag", "");  // 必填，0-否、1-是
            // 药品商品名
            rxdrug.put("drugProdname","");  // 药品商品名
            // 药品通用名
            rxdrug.put("drugGenname", item.getPresDrug().getGenname());  // 必填，药品通用名
            // 药品剂型
            rxdrug.put("drugDosform", item.getPresDrug().getDosformname());  // 必填，药品剂型
            // 药品规格
            rxdrug.put("drugSpec", item.getPresDrug().getSpecname() +'*'+item.getPresDrug().getMinpaccnt()+item.getPresDrug().getMinprepunt()+'/'+item.getPresDrug().getMinpacunt());  // 必填，药品规格
            // 生厂厂家
            rxdrug.put("prdrName", item.getPresDrug().getPrdrname());  // 生厂厂家
            rxdrug.put("medcWayCodg", item.getWesternMedicineUse().getValue());  // 用药途径描述
            // 用药途径描述
            rxdrug.put("medcWayDscr", item.getWesternMedicineUse().getName());  // 用药途径描述
            // 用药开始时间
            rxdrug.put("medcBegntime", item.getCreateDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime().format(outputFormatter));  // 必填，yyyy-MM-dd HH:mm:ss 格式
            // 用药结束时间
           LocalDateTime startTime = LocalDateTime.parse( item.getCreateDate().toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDateTime().format(outputFormatter), outputFormatter);
            LocalDateTime endTime = startTime.plusDays(Long.parseLong(item.getDays().getName()));
            rxdrug.put("medcEndtime", endTime.toString());  // 必填，yyyy-MM-dd HH:mm:ss 格式
            // 用药天数
            rxdrug.put("medcDays", item.getDays().getName());  // 必填，用药天数
            // 药品单价
            rxdrug.put("drugPric", "");  // 药品单价，按发药单位计价
            // 药品总金额
            rxdrug.put("drugSumamt", "");  // 药品总金额
            // 药品发药总量
            rxdrug.put("drugCnt", item.getTotal());  // 必填，药品发药总量
            // 药品发药单位
            rxdrug.put("drugDosunt",item.getPresDrug().getMinpaccnt() );  // 必填，发药单位
            // 用药总量
            rxdrug.put("drugTotlcnt", "");  // 必填，用药总量
            // 用药总量单位
            rxdrug.put("drugTotlcntEmp", "");  // 必填，用药总量单位
            // 单次用量
            rxdrug.put("sinDoscnt", item.getSingleDosage());  // 必填，单次用量
            // 单次剂量单位
            rxdrug.put("sinDosunt",item.getPresDrug().getMinprepunt());  // 必填，单次剂量单位
            // 使用频次编码
            rxdrug.put("usedFrquCodg", item.getFrequency().getValue());  // 必填，参考使用频次
            // 使用频次名称
            rxdrug.put("usedFrquName", item.getFrequency().getName());  // 必填，使用频次名称
            // 医院审批标志
            rxdrug.put("hospApprFlag", "1");  // 必填，医院审批标志
            rxdrugdetail.add(rxdrug);

        });
        //就诊信息
        JSONObject mdtrtinfo = new JSONObject(true);
        //诊断信息
        JSONObject diseinfo  = new JSONObject(true);
        String FixmedinsCode = SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode");
        String FixmedinsName = SessionUtils.getUserJson().getJSONObject("company").getString("name");
        mdtrtinfo.put("fixmedinsName",FixmedinsName);        // 定点医疗机构名称 (字符型 200)
        mdtrtinfo.put("fixmedinsCode", FixmedinsCode);        // 定点医疗机构编号 (字符型 20)
        mdtrtinfo.put("mdtrtId", registration.getMdtrtId());              // 医保就诊ID (字符型 30)
        mdtrtinfo.put("medType", registration.getMedType());              // 医疗类别 (字符型 6)
        mdtrtinfo.put("iptOtpNo", registration.getIptOtpNo());             // 住院/门诊号 (字符型 30)
        mdtrtinfo.put("otpIptFlag", "1");           // 门诊住院标识 (字符型 3)
        mdtrtinfo.put("psnNo", patientMdData.getPsnNo());                // 医保人员编号 (字符型 30)
        mdtrtinfo.put("patnName", patientMdData.getPsnName());             // 患者姓名 (字符型 40)
        mdtrtinfo.put("psnCertType", registration.getCardType());          // 人员证件类型 (字符型 6)
        mdtrtinfo.put("certno", patientMdData.getCertno());               // 证件号码 (字符型 50)
        mdtrtinfo.put("patnAge", patientMdData.getAge());             // 年龄 (数值型 4,1)
        mdtrtinfo.put("patnHgt", "");             // 患者身高 (数值型 6,2)
        mdtrtinfo.put("patnWt", "");              // 患者体重 (数值型 6,2)
        mdtrtinfo.put("gend", patientMdData.getGend());                 // 性别 (字符型 6)
        mdtrtinfo.put("gesoVal", "");               // 妊娠(孕周) (数值型 2)
        mdtrtinfo.put("nwbFlag", "");              // 新生儿标志 (字符型 3)
        mdtrtinfo.put("nwbAge", "");               // 新生儿日、月龄 (字符型 20)
        mdtrtinfo.put("suckPrdFlag", "");           // 哺乳期标志 (数值型 3)
        mdtrtinfo.put("algsHis", "");              // 过敏史 (字符型 1000)
        mdtrtinfo.put("prscDeptName",registration.getClinicOffice().getName() );         // 开方科室名称 (字符型 50)
        mdtrtinfo.put("prscDeptCode", registration.getClinicOffice().getCode());         // 开方科室编号 (字符型 30)
        mdtrtinfo.put("drCode", doctor.getUserExt().getPracPsnCode());               // 开方医保医师代码 (字符型 20)
        mdtrtinfo.put("prscDrName", doctor.getName());           // 开方医师姓名 (字符型 50)
        mdtrtinfo.put("prscDrCertType", "");       // 开方医师证件类型 (字符型 6)
        mdtrtinfo.put("prscDrCertno", "");         // 开方医师证件号码 (字符型 50)
        // 医生职称编码 (字符型 20)
        mdtrtinfo.put("drProfttlCodg", doctor.getUserExt().getPost());
        switch (doctor.getUserExt().getPost()){
            case  "231":
                mdtrtinfo.put("drProfttlName", "主任医师");
                break;
            case  "232":
                mdtrtinfo.put("drProfttlName", "副主任医师");
                break;
            case  "233":
                mdtrtinfo.put("drProfttlName", "主诊医师");
                break;
            case  "234":
                mdtrtinfo.put("drProfttlName", "医师");
                break;
            case  "235":
                mdtrtinfo.put("drProfttlName", "医士");
                break;
            default:   mdtrtinfo.put("drProfttlName", "");
        }
        mdtrtinfo.put("drDeptCode",doctor.getUserExt().getOfficeCode());           // 医生科室编码 (字符型 30)
        mdtrtinfo.put("drDeptName", doctor.getUserExt().getOffice());           // 医生科室名称 (字符型 50)
        mdtrtinfo.put("mdtrtTime", registration.getCreateDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(outputFormatter));            // 就诊时间 (日期时间型 yyyy-MM-dd HH:mm:ss)
        mdtrtinfo.put("diseCodg", "");             // 病种编码 (字符型 30)
        mdtrtinfo.put("diseName", "");             // 病种名称 (字符型 500)
        mdtrtinfo.put("spDiseFlag", "");           // 特殊病种标志 (字符型 3)
        XtZd xtZd = xtZdService.getById(medicalRecord.getDiseaseId());
        if(BizConstants.RECIPEL_TYPE_WESTERN.equals(recipelInfo.getRecipelType().getValue())){
            //西药
            diseinfo.put("diagType", "1");        // 诊断类别 (诊断类型，字符型，最大长度3)
            diseinfo.put("tcmDiseCode", "");     // 中医病名代码 (字符型，最大长度9)
            diseinfo.put("tcmDiseName", "");     // 中医病名 (字符型，最大长度100)
            diseinfo.put("tcmsympCode", "");     // 中医症候代码 (字符型，最大长度9)
            diseinfo.put("tcmsymp", "");         // 中医症候 (字符型，最大长度100)

        } else if (BizConstants.RECIPEL_TYPE_CHINESE.equals(recipelInfo.getRecipelType().getValue())) {
            //中药
            XtZyzh xtZyzh = xtZyzhService.getById(medicalRecord.getSyndromeId());
            diseinfo.put("diagType", "2");        // 诊断类别 (诊断类型，字符型，最大长度3)
            diseinfo.put("tcmDiseCode", xtZd.getIcd10());     // 中医病名代码 (字符型，最大长度9)
            diseinfo.put("tcmDiseName", xtZd.getZdmc());     // 中医病名 (字符型，最大长度100)
            diseinfo.put("tcmsympCode", xtZyzh.getZhcode());     // 中医症候代码 (字符型，最大长度9)
            diseinfo.put("tcmsymp",xtZyzh.getZhmc() );         // 中医症候 (字符型，最大长度100)
        }
        mdtrtinfo.put("diseCondDscr", "");         // 疾病病情描述 (字符型 2000)
        mdtrtinfo.put("hiFeesetlType", "");        // 医保费用结算类型 (字符型 6)
        mdtrtinfo.put("hiFeesetlName", "");        // 医保费用类别名称 (字符型 20)
        mdtrtinfo.put("rgstFee", "");             // 挂号费 (数值型 16,2)
        mdtrtinfo.put("medfeeSumamt", "");        // 医疗费总额 (数值型 16,2)
        mdtrtinfo.put("fstdiagFlag", "");          // 是否初诊 (字符型 3)
        mdtrtinfo.put("extras", "");               // 扩展数据 (对象型)
      //诊断节点
        diseinfo.put("maindiagFlag", "1");    // 主诊断标志 (0-否，1-是，字符型，最大长度3)
        diseinfo.put("diagSrtNo", "1");       // 诊断排序号 (数值型，最大长度2)
        diseinfo.put("diagCode", xtZd.getIcd10());        // 诊断代码 (使用国家医保诊断代码，字符型，最大长度30)
        diseinfo.put("diagName", xtZd.getZdmc());        // 诊断名称 (字符型，最大长度100)
        diseinfo.put("diagDept", registration.getClinicOffice().getName());        // 诊断科室 (字符型，最大长度50)
        diseinfo.put("diagDrNo", doctor.getUserExt().getPracPsnCode());        // 诊断医生编码 (国家医保医师代码，字符型，最大长度30)
        diseinfo.put("diagDrName", doctor.getName());      // 诊断医生姓名 (字符型，最大长度50)
        diseinfo.put("diagTime", medicalRecord.getCreateDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(outputFormatter));        // 诊断时间 (日期时间型，格式：yyyy-MM-dd HH:mm:ss)
        data.put("rxdrugdetail",rxdrugdetail);
        data.put("mdtrtinfo",mdtrtinfo);
        data.put("diseinfo",diseinfo);
        JSONObject jsonObject= new JSONObject(true);
        jsonObject.put("data",data);

        JSONObject ld7801 = JSONObject.parseObject("{\"rxtracecode\":12345678901234567890,\"hirxno\":987654321098765432109876543210}");

        //JSONObject ld7801 = mdRequestUtil.dzcfUpload("Ld7801", jsonObject);
        //日志落地表
        DzcfD001Output dzcfD001Output = new DzcfD001Output();
        dzcfD001Output.setMzh(registration.getId());
        dzcfD001Output.setCfh(recipelInfo.getId());
        dzcfD001Output.setOrganizeid(SessionUtils.getUserJson().getJSONObject("company").getString("id"));
        dzcfD001Output.setInputcontent(jsonObject.getString("data"));
        dzcfD001Output.setRxtracecode(ld7801.getString("rxtracecode"));
        dzcfD001Output.setHirxno(ld7801.getString("hirxno"));
        dzcfD001Output.setCzydm(SessionUtils.getUserJson().getString("loginName"));
        dzcfD001Output.setCzrq(LocalDateTime.now());
        dzcfD001Output.setZt(1);
        dzcfD001OutputService.save(dzcfD001Output);
        return ld7801 ;
    }

    /**
     * @return 【Ld7802】电子处方医保电子签名
     */
    @Override
    @Transactional
    public JSONObject signElectronicPrescriptionWithInsurance_Ld7802(RecipelInfoReview recipelInfoReview, String ysId) {
        RecipelInfo recipelInfo = recipelInfoReview.getRecipelInfo();
        Registration registration = recipelInfo.getRegistration();
        MedicalRecord medicalRecord = recipelInfoReview.getMedicalRecord();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        User doctor = userService.get(registration.getDoctor().getId());
        User ysDoctor = userService.get(ysId);
        String FixmedinsCode = SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode");
        String FixmedinsName = SessionUtils.getUserJson().getJSONObject("company").getString("name");
        //根据处方id获得拼接处方PDF路径
        JSONObject data = new JSONObject();
        String url = "http://localhost:" +properties.getWebPort() +PRESCRIPTION_URL_XML+ recipelInfoReview.getRecipelInfo().getId();
        //文件base64
         byte[] bytes = HttpUtil.downloadBytes(url);
        String pdfInfo = Base64.getEncoder().encodeToString(bytes);
        data.put("originalRxFile", pdfInfo);
        //电子处方上传参数1-20JsonString的Base64字符值
        JSONObject top20  = new JSONObject();
         DzcfD001Output one = dzcfD001OutputService.getOne(new LambdaQueryWrapper<DzcfD001Output>().eq(DzcfD001Output::getCfh, recipelInfo.getId()).eq(DzcfD001Output::getOrganizeid, SessionUtils.getLoginTenantId()));
        top20.put("rxTraceCode",one.getRxtracecode() );               // 处方追溯码，字符型
        top20.put("hiRxno", one.getHirxno());                    // 医保处方编号，字符型
        top20.put("mdtrtId", registration.getMdtrtId());                   // 医保就诊ID，字符型
        top20.put("patnName", patientMdData.getPsnName());                  // 患者姓名，字符型
        top20.put("psnCertType", patientMdData.getPsnCertType());               // 人员证件类型，字符型
        top20.put("certno", patientMdData.getCertno());                    // 证件号码，字符型
        top20.put("fixmedinsName", FixmedinsName );             // 定点医疗机构名称
        top20.put("fixmedinsCode",  FixmedinsCode);             // 定点医疗机构编号
        top20.put("drCode", doctor.getUserExt().getPracPsnCode());                    // 开方医保医师代码
        top20.put("prscDrName", doctor.getName());                // 开方医师姓名
        top20.put("pharDeptName", ysDoctor.getDepartment().getCode() );              // 审方药师科室名称
        top20.put("pharDeptCode", ysDoctor.getUserExt().getOfficeCode());              // 审方药师科室编号
        top20.put("hosp_dept_codg", registration.getClinicOffice().getCode());            // 医院科室编码
        top20.put("pharProfttlCodg", ysDoctor.getUserExt().getPost());
        // 审方药师职称编码
        switch (ysDoctor.getUserExt().getPost()){
            case  "1":
                top20.put("drProfttlName", "执业药师");
                break;
            case  "2":
                top20.put("drProfttlName", "卫生技术职称");
                break;
            case  "2.1":
                top20.put("drProfttlName", "主任医师");
                break;
            case  "2.2":
                top20.put("drProfttlName", "副主任医师");
                break;
            case  "2.3":
                top20.put("drProfttlName", "主管药师");
                break;
            case  "2.4":
                top20.put("drProfttlName", "药师");
                break;
            case  "2.5":
                top20.put("drProfttlName", "医士");
                break;
            default:
                top20.put("drProfttlName", "");
        }
        top20.put("pharCode", ysDoctor.getUserExt().getPost());                  // 审方医保药师代码
        top20.put("pharCertType", "");              // 审方药师证件类型
        top20.put("pharCertno", "");                // 审方药师证件号码
        top20.put("pharName", ysDoctor.getName());                  // 审方药师姓名
        top20.put("pharPracCertNo", ysDoctor.getUserExt().getPracPsnCode());            // 审方药师执业资格证号
        top20.put("pharChkTime", recipelInfoReview.getCreateDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(outputFormatter));               // 审方药师审方时间，日期时间型，默认为空字符串
        String top20base64 = Base64.getEncoder().encodeToString(top20.toString().getBytes());
        data.put("originalValue", top20base64);
        //医疗机构代码
        data.put("fixmedinsCode", SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        DzcfD002Output dzcfD002Output = new DzcfD002Output();
//        JSONObject ld7802 = JSON.parseObject("{\n" +
//                "  \"rxFile\": \"UEsDBBQAAAAIAI6gO5bPUc3nqs2SOmeKb4hVV3zRGkA5uAY9S5sPtm2EzKItUEz0X0MNRVBeVJtSBRZ6gdlwsOdQFk8NdhNBZeaSHIQFBvjoHaVBpP1fZ0PlHKhqP0RZ8wQZlV2J+3H0BEPDNkweXvTxF3akbbYN3pCRgBqa6xfqujlRaxDXnGG8J2UdhPuwmPOG1G9IhpkI+xP2rZz3IEp5VyzKSCxM6Wcvqqzt1p7s4zo5bXx/J0iq1lVXkmG7e6Nho2VHJGG0SZnGYoS0=\", \n" +
//                "  \"signDigest\": \"8ac8e8327f2b38be283cd9032f2074f43b87c9a50637a589ec7c8039c36e69b9\", \n" +
//                "  \"signCertSn\": \"1234567890ABCDE1234567890ABCDE1234567890ABCDE1234567890ABCDE12345\", \n" +
//                "  \"signCertDn\": \"C=CN,O=NHSA,ST=15,L=00,L=00,OU=02,T=91150500MA0NFP6N44,CN=通辽博德中蒙西医结合医院\"\n" +
//                "}");
        JSONObject ld7802 =   mdRequestUtil.dzcfUpload("Ld7802", jsonObject);
        dzcfD002Output.setMzh(registration.getId());
        dzcfD002Output.setCfh(recipelInfoReview.getRecipelInfo().getId());
        dzcfD002Output.setOrganizeid(SessionUtils.getLoginTenantId());
        dzcfD002Output.setOriginalvalue(top20base64);
        dzcfD002Output.setInputcontent(jsonObject.getString("data"));
        dzcfD002Output.setOriginalrxfile(pdfInfo);
        dzcfD002Output.setRxfile(ld7802.getString("rxFile"));
        dzcfD002Output.setSigndigest(ld7802.getString("signDigest"));
        dzcfD002Output.setSigncertsn(ld7802.getString("signCertSn"));
        dzcfD002Output.setSigncertdn(ld7802.getString("signCertDn"));
        dzcfD002Output.setCzydm(SessionUtils.getUserJson().getString("id"));
        dzcfD002Output.setCzrq(new Date());
        dzcfD002Output.setZt(1);
        dzcfD002OutputService.save(dzcfD002Output);

        return ld7802;
    }

    /**【Ld7101】电子处方上传
     * @return
     */
    @Override
    public JSONObject uploadElectronicPrescription_Ld7101(RecipelInfoReview recipelInfoReview,String ysId) {
        String FixmedinsCode = SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode");
        String FixmedinsName = SessionUtils.getUserJson().getJSONObject("company").getString("name");
        RecipelInfo recipelInfo = recipelInfoReview.getRecipelInfo();
        Registration registration = recipelInfo.getRegistration();
        DzcfD001Output output001 = dzcfD001OutputService.getOne(new LambdaQueryWrapper<DzcfD001Output>().eq(DzcfD001Output::getMzh, recipelInfo.getRegistration().getId()).eq(DzcfD001Output::getOrganizeid, SessionUtils.getLoginTenantId()));
        DzcfD002Output output002 = dzcfD002OutputService.getOne(new LambdaQueryWrapper<DzcfD002Output>().eq(DzcfD002Output::getMzh, recipelInfo.getRegistration().getId()).eq(DzcfD002Output::getOrganizeid, SessionUtils.getLoginTenantId()));
        MedicalRecord medicalRecord = recipelInfoReview.getMedicalRecord();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        User doctor = userService.get(registration.getDoctor().getId());
        User ysDoctor = userService.get(ysId);
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        JSONObject data = new JSONObject(true);
        data.put("rxTraceCode",output001.getRxtracecode());
        data.put("hiRxno",output001.getHirxno());
        data.put("mdtrtId",registration.getMdtrtId());
        data.put("patnName", patientMdData.getPsnName());
        data.put("psnCertType", registration.getCardType());          // 人员证件类型 (字符型 6)
        data.put("certno", patientMdData.getCertno());
        data.put("fixmedinsName", FixmedinsName );             // 定点医疗机构名称
        data.put("fixmedinsCode",  FixmedinsCode);
        data.put("drCode",doctor.getUserExt().getPracPsnCode());
        data.put("prscDrName", doctor.getName());
        data.put("pharDeptName", ysDoctor.getDepartment().getCode() );              // 审方药师科室名称
        data.put("pharDeptCode", ysDoctor.getUserExt().getOfficeCode());
        switch (ysDoctor.getUserExt().getPost()){
            case  "1":
                data.put("drProfttlName", "执业药师");
                break;
            case  "2":
                data.put("drProfttlName", "卫生技术职称");
                break;
            case  "2.1":
                data.put("drProfttlName", "主任医师");
                break;
            case  "2.2":
                data.put("drProfttlName", "副主任医师");
                break;
            case  "2.3":
                data.put("drProfttlName", "主管药师");
                break;
            case  "2.4":
                data.put("drProfttlName", "药师");
                break;
            case  "2.5":
                data.put("drProfttlName", "医士");
                break;
            default:
                data.put("drProfttlName", "");
        }
        data.put("pharCode", ysDoctor.getUserExt().getPost());                  // 审方医保药师代码
        data.put("pharCertType", "");              // 审方药师证件类型
        data.put("pharCertno", "");                // 审方药师证件号码
        data.put("pharName", ysDoctor.getName());                  // 审方药师姓名
        data.put("pharPracCertNo", ysDoctor.getUserExt().getPracPsnCode());            // 审方药师执业资格证号
        data.put("pharChkTime", recipelInfoReview.getCreateDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(outputFormatter));
         data.put("rxFile",output002.getRxfile());
         data.put("signDIgest", output002.getSigndigest());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        DzcfD003Output dzcfD003Output = new DzcfD003Output();
        dzcfD003Output.setMzh(registration.getId());
        dzcfD003Output.setCfh(recipelInfo.getId());
        dzcfD003Output.setOrganizeid(SessionUtils.getLoginTenantId());
        dzcfD003Output.setInputcontent(jsonObject.getString("data"));
        dzcfD003Output.setRxtracecode(output001.getRxtracecode());
        dzcfD003Output.setHirxno(output001.getHirxno());
        JSONObject ld7101 =   mdRequestUtil.dzcfUpload("Ld7101",jsonObject);
        dzcfD003Output.setRxtracecode(ld7101.getString("rxtracecode"));
        dzcfD003Output.setRxstasname(ld7101.getString("rxstasname"));
        dzcfD003OutputService.save(dzcfD003Output);
        return ld7101;
    }

    /**电子处方撤销
     * @return
     */
    @Override
    public JSONObject cancelElectronicPrescription_Ld7104(RecipelInfoReview recipelInfoReview, String undoRea) {
           JSONObject data = new JSONObject();
           DzcfD001Output one001 = dzcfD001OutputService.getOne(new LambdaQueryWrapper<DzcfD001Output>().eq(DzcfD001Output::getCfh, recipelInfoReview.getRecipelInfo().getId()).eq(DzcfD001Output::getOrganizeid, SessionUtils.getLoginTenantId()));
           String fixmedinsCode = SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode");
           RecipelInfo recipelInfo = recipelInfoReview.getRecipelInfo();
           User doctor = userService.get(recipelInfo.getRegistration().getDoctor().getId());
           data.put("hiRxno", one001.getHirxno());
           data.put("fixmedinsCode", fixmedinsCode);
           data.put("drCode",doctor.getUserExt().getPracPsnCode());
           data.put("drName",doctor.getName());
           data.put("undoRea", undoRea);
           data.put("undoTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
           JSONObject jsonObject = new JSONObject();
           jsonObject.put("data", data);
           JSONObject ld7104 = mdRequestUtil.dzcfUpload("Ld7104", jsonObject);
           DzcfD003Output dzcfD003Output = dzcfD003OutputService.getOne(new LambdaQueryWrapper<DzcfD003Output>().eq(DzcfD003Output::getCfh, recipelInfo.getId()));
           dzcfD003Output.setCxyy(undoRea);
           dzcfD003Output.setCxsj(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
           dzcfD003Output.setRxstascodg(ld7104.getString("rxstascodg"));
           dzcfD003Output.setRxstasname(ld7104.getString("rxstasname"));
           dzcfD003OutputService.saveOrUpdate(dzcfD003Output);
        return ld7104;
    }

    /**
     * @return
     */
    @Override
    public JSONObject queryElectronicPrescriptionInfo_Ld7202() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public JSONObject feedbackPrescriptionReviewResult_Ld7102() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public JSONObject queryElectronicPrescriptionReviewResult_Ld7805(RecipelInfoReview recipelInfoReview) {
        JSONObject data  = new JSONObject(true);
        Registration registration = recipelInfoReview.getRecipelInfo().getRegistration();
        DzcfD001Output one001 = dzcfD001OutputService.getOne(new LambdaQueryWrapper<DzcfD001Output>().eq(DzcfD001Output::getCfh, recipelInfoReview.getRecipelInfo().getId()).eq(DzcfD001Output::getOrganizeid, SessionUtils.getLoginTenantId()));
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        String fixmedinsCode = SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode");
        data.put("hiRxno",one001.getHirxno());
        data.put("fixmedinsCode",fixmedinsCode);
        data.put("mdtrtId",registration.getMdtrtId());
        data.put("psnName",patientMdData.getPsnName());
        data.put("psnCertType",patientMdData.getPsnCertType());
        data.put("psnCertno",patientMdData.getCertno());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        return mdRequestUtil.dzcfUpload("【Ld7804",jsonObject);
    }

    /**
     * @return 取药查询
     */
    @Override
    public JSONObject queryPrescriptionDispensingResult_Ld7804(RecipelInfoReview recipelInfoReview) {
        JSONObject data  = new JSONObject(true);
        Registration registration = recipelInfoReview.getRecipelInfo().getRegistration();
        DzcfD001Output one001 = dzcfD001OutputService.getOne(new LambdaQueryWrapper<DzcfD001Output>().eq(DzcfD001Output::getCfh, recipelInfoReview.getRecipelInfo().getId()).eq(DzcfD001Output::getOrganizeid, SessionUtils.getLoginTenantId()));
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        String fixmedinsCode = SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode");
        data.put("hiRxno",one001.getHirxno());
        data.put("fixmedinsCode",fixmedinsCode);
        data.put("mdtrtId",registration.getMdtrtId());
        data.put("psnName",patientMdData.getPsnName());
        data.put("psnCertType",patientMdData.getPsnCertType());
        data.put("psnCertno",patientMdData.getCertno());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        return mdRequestUtil.dzcfUpload("【Ld7804",jsonObject);
    }

    /**
     * @return 电子处方药品信息查询后保存
     */
    @Override
    public Boolean queryPrescriptionDrugDirectory_Ld7806() {
        JSONObject data = new JSONObject();
        data.put("fixmedinsCode", SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode"));
        data.put("medListCodg","");
        data.put("medListCodgs","");
        data.put("begntime","");
        data.put("endtime", "");
        //查出所有后存入本地数据库
        data.put("pageSize", Long.MAX_VALUE);
        data.put("pageNum", "1");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        return presDrugService
                .saveBatch(JSONArray.parseArray(mdRequestUtil.dzcfUpload("Ld7806",jsonObject).getString("list"), PresDrug.class));
    }



}
