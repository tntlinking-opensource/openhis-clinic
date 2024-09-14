package com.geeke.outpatient.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.outpatient.dao.MedicalRecordDao;
import com.geeke.outpatient.dao.RecipelDetailDao;
import com.geeke.outpatient.dao.RegistrationDao;
import com.geeke.outpatient.entity.*;
import com.geeke.outpatient.service.*;
import com.geeke.stock.entity.Dispensing;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.Stuff;
import com.geeke.stock.service.DispensingService;
import com.geeke.stock.service.DrugService;
import com.geeke.stock.service.StuffService;
import com.geeke.toll.entity.TollInfo;
import com.geeke.toll.service.TollInfoService;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.service.CostItemService;
import com.geeke.utils.DateUtils;
import com.geeke.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.java2d.pipe.OutlineTextRenderer;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DataBean {

    private static final String MEDICAL_REMARK = "特别提示：按卫生部、国家中医药管理局卫医发[2002]24号文件规定：" +
            "为保证患者用药安全，药品一经发出，不得退换。";

    private static final String CHARGE_REMARK = "温馨提示：\n此凭条不具备报销功能，请妥善保管好你的缴费凭条，就诊，退费时请出示该凭条。" +
            "若有遗失，将造成退费不便和信息泄露。";

    private static final String REGISTRATION_REMARK = "温馨提示：\n此凭条不具备报销功能，请妥善保管好你的缴费凭条，就诊，退费时请出示该凭条。";

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedicalRecordDao medicalRecordDao;

    @Autowired
    private RecipelInfoService recipelInfoService;

    @Autowired
    private RecipelDetailDao recipelDetailDao;


    @Autowired
    private TollInfoService tollInfoService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private CostItemService costItemService;

    @Resource
    private MedicalRecordService medicalRecordService;
    @Resource
    private DispensingService dispensingService;
    /**
     * @return 组装处方其他信息
     */
    public List<RecipelTempletEvt> buildReport(String dsName, String datasetName, Map<String, Object> parameters) {
        //处方id
        String recipelInfoId = (String) parameters.get("recipelInfoId");
        //处方|发药 0|1
        String type = (String) parameters.get("type");
        RecipelInfo recipelInfo = recipelInfoService.get(recipelInfoId);
        List<Parameter> parameters2 = new ArrayList<>();
        parameters2.add(new Parameter("registration_id", "=", recipelInfo.getRegistration().getId()));
        PageRequest pageRequest = new PageRequest(parameters2, "");
        List<MedicalRecord> medicalRecords = medicalRecordDao.listAll(pageRequest);
        MedicalRecord medicalRecord = medicalRecords.get(0);
        //MedicalRecord medicalRecord = medicalRecordDao.get(recipelInfo.getMedicalRecord().getId());
        Registration registration = registrationService.get(medicalRecord.getRegistration().getId());
        Patient patient = patientService.get(registration.getPatientId().getId());
        List<TollInfo> byRecipeId = tollInfoService.getByRecipeId(recipelInfoId);
        TollInfo tollInfo = null;
        if (null != byRecipeId && !byRecipeId.isEmpty()) {
            tollInfo = byRecipeId.get(0);
        }
        List<RecipelTempletEvt> outList = new ArrayList<>();
        RecipelTempletEvt recipelTempletEvt = new RecipelTempletEvt();
        recipelTempletEvt.setClinicName(recipelInfo.getCompany().getName());
        recipelTempletEvt.setSmallType(recipelInfo.getSmallType().getName());
        recipelTempletEvt.setOffice("科室：" + registration.getClinicOffice().getName());
        recipelTempletEvt.setCode("处方号：" + recipelInfo.getCode());
        recipelTempletEvt.setName("姓名：" + patient.getName());
        recipelTempletEvt.setGender("性别：" + patient.getGender().getName());
        recipelTempletEvt.setTreatType(registration.getTreatType().getName());
        String billDate = DateUtils.formatDate(recipelInfo.getCreateDate(), "yyyy年MM月dd日");
        recipelTempletEvt.setBillDate("开单日期：" + billDate);
        recipelTempletEvt.setAge("年龄：" + patient.getAge() + "岁");
        recipelTempletEvt.setFeeType("费别：" + (null == tollInfo ? "" : tollInfo.getPaymentType().getName()));
        recipelTempletEvt.setDispenseDate("0".equals(type) ? "" : ("发药日期：" + format.format(recipelInfo.getDispensionDate())));
        recipelTempletEvt.setIdCard("身份证号：" + patient.getCard());
        recipelTempletEvt.setDiagnose("临床诊断：" + (StringUtils.isNotBlank(medicalRecord.getWesternDiagnose()) ? "西医诊断：" + medicalRecord.getWesternDiagnose() + ";" : "")
                + (StringUtils.isNotBlank(medicalRecord.getChinaDiagnose()) ? "中医诊断：" + medicalRecord.getChinaDiagnose() + ";" : ""));
        recipelTempletEvt.setDoctor("医师：" + registration.getDoctor().getName());
        recipelTempletEvt.setFee("药费：" + recipelInfo.getFee() + "元");
        recipelTempletEvt.setEntrust(StringUtils.isBlank(recipelInfo.getEntrust()) ? "" : "医嘱事项：" + recipelInfo.getEntrust());
        recipelTempletEvt.setBlack("················以下空白················");
        recipelTempletEvt.setApothecary("审配：");
        recipelTempletEvt.setDeploy("调配：");
        recipelTempletEvt.setMedicalRemark(MEDICAL_REMARK);
        outList.add(recipelTempletEvt);
        return outList;
    }

    /**
     * @return 组装西药
     */
    public List<RecipelDetailEvt> westMedicine(String dsName, String datasetName, Map<String, Object> parameters) {
        List<RecipelDetailEvt> outList = new ArrayList<>();
        //处方id
        String recipelInfoId = (String) parameters.get("recipelInfoId");
        List<RecipelDetail> detailList = recipelDetailDao.getByRecipelInfoId(recipelInfoId);
        BigDecimal count = new BigDecimal(0);
        if (null != detailList && !detailList.isEmpty()) {
            for (RecipelDetail recipelDetail : detailList) {
                if (1 == recipelDetail.getIsExtra()) {
                    //计算附加费用
                    count = count.add(recipelDetail.getAllFee());
                    continue;
                }
                if ("0".equals(recipelDetail.getStuffType())) {
                    Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    RecipelDetailEvt recipelDetailEvt = new RecipelDetailEvt();
                    recipelDetailEvt.setName(drug.getGoodsName());
                    recipelDetailEvt.setNorms("("+drug.getDosis() + drug.getDosisUnit().getName() + "*" + drug.getPreparation() + drug.getPreparationUnit().getName()+")");
                    recipelDetailEvt.setNum(recipelDetail.getTotal() + drug.getPreparationUnit().getName());
                    recipelDetailEvt.setPrice(recipelDetail.getAllFee() + "元");
                    String daysName = recipelDetail.getDays() != null && recipelDetail.getDays().getName() != null
                            ? recipelDetail.getDays().getName()
                            : "0";
                    recipelDetailEvt.setDays(daysName + "天");
                    recipelDetailEvt.setFrequency(recipelDetail.getFrequency().getRemarks() + "\t" + (daysName + "天"));
                    recipelDetailEvt.setUse("Sig："+recipelDetail.getWesternMedicineUse().getName());
                    recipelDetailEvt.setUseText("(" + recipelDetail.getSingleDosage() + drug.getDosisUnit().getName()+")/次");
                    outList.add(recipelDetailEvt);
                    //无赖之举
                    RecipelDetailEvt recipelDetailEvt1 = new RecipelDetailEvt();
                    recipelDetailEvt1.setName(recipelDetailEvt.getUse());
                    recipelDetailEvt1.setNorms(recipelDetailEvt.getUseText());
                    recipelDetailEvt1.setNum(recipelDetailEvt.getFrequency());
                    outList.add(recipelDetailEvt1);
                }
            }
        }
        if (1 == count.compareTo(new BigDecimal(0))) {
            //组装附加费
            RecipelDetailEvt recipelDetailEvt = new RecipelDetailEvt();
            recipelDetailEvt.setUseText("");
            recipelDetailEvt.setStuffPrice( count + "元");
            outList.add(recipelDetailEvt);
        }
        return outList;
    }

    /**
     * @return 组装输液处方
     */
    public List<InfuseEvt> infuse(String dsName, String datasetName, Map<String, Object> parameters) {
        List<InfuseEvt> outList = new ArrayList<>();
        //处方id
        String recipelInfoId = (String) parameters.get("recipelInfoId");
        List<RecipelDetail> detailList = recipelDetailDao.getInfuseByRecipelInfoId(recipelInfoId);
        InfuseEvt useMethod = new InfuseEvt();
        Integer num = 1;
        BigDecimal count = new BigDecimal(0);
        if (null != detailList && !detailList.isEmpty()) {
            for (RecipelDetail recipelDetail : detailList) {
                if (1 == recipelDetail.getIsExtra()) {
                    //计算附加费用
                    count = count.add(recipelDetail.getAllFee());
                    continue;
                }
                if ("2".equals(recipelDetail.getStuffType())) {
                    Integer infuseGroup = recipelDetail.getInfuseGroup();
                    Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    if (infuseGroup > num) {
                        //拼接用法
                        InfuseEvt use = new InfuseEvt();
                        BeanUtils.copyProperties(useMethod, use);
                        outList.add(use);
                        num = infuseGroup;
                    }
                    InfuseEvt infuseEvt = new InfuseEvt();
                    infuseEvt.setName("("+infuseGroup+") "+drug.getGoodsName());
                    infuseEvt.setSkinTest(recipelDetail.getSkinTest().getName());
                    infuseEvt.setSingleDosage(recipelDetail.getSingleDosage() + drug.getPreparationUnit().getName());
                    String daysName = recipelDetail.getDays() != null && recipelDetail.getDays().getName() != null
                            ? recipelDetail.getDays().getName()
                            : "0";
                    infuseEvt.setDays("x"+daysName);
                    infuseEvt.setTotalNum("共"+recipelDetail.getTotal() + drug.getPreparationUnit().getName());
                    outList.add(infuseEvt);
                    useMethod.setName("Sig：" + recipelDetail.getInfuseUse().getName());
                    useMethod.setSkinTest(recipelDetail.getFrequency().getRemarks());
                    useMethod.setSingleDosage(recipelDetail.getDrippingSpeed() + "滴/分钟");
                }
            }
            outList.add(useMethod);
        }
        if (1 == count.compareTo(new BigDecimal(0))) {
            //组装附加费
            InfuseEvt infuseEvt = new InfuseEvt();
            infuseEvt.setStuffPriceName("材料费");
            infuseEvt.setStuffPrice(count + "元");
            outList.add(infuseEvt);
        }
        return outList;
    }

    /**
     * @return 组装中药
     */
    public List<RecipelDetailEvt> chineseMedicine(String dsName, String datasetName, Map<String, Object> parameters) {
        List<RecipelDetailEvt> outList = new ArrayList<>();
        //处方id
        String recipelInfoId = (String) parameters.get("recipelInfoId");
        RecipelInfo recipelInfo = recipelInfoService.get(recipelInfoId);
        List<RecipelDetail> detailList = recipelDetailDao.getByRecipelInfoId(recipelInfoId);
        List<String> chineseMedicalNames = new ArrayList<>();
        RecipelDetailEvt recipelDetailEvt = new RecipelDetailEvt();
        BigDecimal count = new BigDecimal(0);
        if (null != detailList && !detailList.isEmpty()) {
            for (RecipelDetail recipelDetail : detailList) {
                if (1 == recipelDetail.getIsExtra()) {
                    //计算附加费用
                    count = count.add(recipelDetail.getAllFee());
                    continue;
                }
                if ("1".equals(recipelDetail.getStuffType())) {
                    Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    chineseMedicalNames.add(drug.getGoodsName() + " " + recipelDetail.getTotal() + drug.getDosisUnit().getName());
                }
            }
        }
        RecipelDetailEvt reDetailEvt = new RecipelDetailEvt();
        reDetailEvt.setRemarks(StringUtils.isBlank(recipelInfo.getEntrust()) ? "" : "医嘱事项： " + recipelInfo.getEntrust());
        reDetailEvt.setNum("共" + recipelInfo.getDosage()+"付");
        reDetailEvt.setUse("用法：" + recipelInfo.getRecipelUse().getName());
        reDetailEvt.setFrequency(recipelInfo.getFrequency().getName()
                +" "+recipelInfo.getTakeFrequency().getName());
        reDetailEvt.setNorms("一次" + recipelInfo.getSingleDosage() + "ml");
        if (1 == count.compareTo(new BigDecimal(0))) {
            //组装附加费
            reDetailEvt.setName("材料费：" + count + "元");
        }
        outList.add(reDetailEvt);

        return outList;
    }

    /**
     * @return 组装中药成分
     */
    public List<EachChineseMedicalEvt> eachChineseMedical(String dsName, String datasetName, Map<String, Object> parameters) {
        List<EachChineseMedicalEvt> outList = new ArrayList<>();
        //处方id
        String recipelInfoId = (String) parameters.get("recipelInfoId");
        List<RecipelDetail> detailList = recipelDetailDao.getByRecipelInfoId(recipelInfoId);
        List<String> chineseMedicalNames = new ArrayList<>();
        if (null != detailList && !detailList.isEmpty()) {
            for (RecipelDetail recipelDetail : detailList) {
                if ("1".equals(recipelDetail.getStuffType())) {
                    Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    DecimalFormat decimalFormat = new DecimalFormat("0.##");
                    chineseMedicalNames.add(drug.getGoodsName() + " " + decimalFormat.format(recipelDetail.getSingleDosage()) + drug.getDosisUnit().getName());
                }
            }
        }
        if (!chineseMedicalNames.isEmpty()) {
            List<List<String>> lists = fixedGrouping(chineseMedicalNames, 4);
            for (List<String> list : lists) {
                EachChineseMedicalEvt eachChineseMedicalEvt = new EachChineseMedicalEvt();
                for (int i = 0; i < list.size(); i++) {
                    if (0 == i) {
                        eachChineseMedicalEvt.setNameOne(list.get(i));
                    } else if (1 == i) {
                        eachChineseMedicalEvt.setNameTwo(list.get(i));
                    } else if (2 == i) {
                        eachChineseMedicalEvt.setNameThree(list.get(i));
                    } else if (3 == i) {
                        eachChineseMedicalEvt.setNameFour(list.get(i));
                    }
                }
                outList.add(eachChineseMedicalEvt);
            }
        }
        return outList;
    }

    /**
     * @return 组装诊疗项目
     */
    public List<RecipelDetailEvt> costItem(String dsName, String datasetName, Map<String, Object> parameters) {
        List<RecipelDetailEvt> outList = new ArrayList<>();
        //处方id
        String recipelInfoId = (String) parameters.get("recipelInfoId");
//        RecipelInfo recipelInfo = recipelInfoService.get(recipelInfoId);
        List<RecipelDetail> detailList = recipelDetailDao.getByRecipelInfoId(recipelInfoId);
        BigDecimal count = new BigDecimal(0);
        if (null != detailList && !detailList.isEmpty()) {
            for (RecipelDetail recipelDetail : detailList) {
                if (1 == recipelDetail.getIsExtra()) {
                    //计算附加费用
                    count = count.add(recipelDetail.getAllFee());
                    continue;
                }
                if ("3".equals(recipelDetail.getStuffType())) {
                    CostItem costItem = costItemService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    RecipelDetailEvt recipelDetailEvt = new RecipelDetailEvt();
                    recipelDetailEvt.setName(costItem.getItemName());
                    recipelDetailEvt.setNum(recipelDetail.getTotal() + "");
                    recipelDetailEvt.setUseText(costItem.getUnit().getName());
                    recipelDetailEvt.setPrice(new DecimalFormat("0.##").format(costItem.getSalePrice()) + "元");
                    recipelDetailEvt.setRemarks(costItem.getRemarks());
                    outList.add(recipelDetailEvt);
                }
            }
        }
        if (1 == count.compareTo(new BigDecimal(0))) {
            //组装附加费
            RecipelDetailEvt recipelDetailEvt = new RecipelDetailEvt();
            recipelDetailEvt.setUseText("");
            recipelDetailEvt.setStuffPrice(count + "元");
            outList.add(recipelDetailEvt);
        }
        return outList;
    }

    /**
     * @return 组装收费
     */
    public List<ChargeTicketEvt> charge(String dsName, String datasetName, Map<String, Object> parameters) {
        List<ChargeTicketEvt> outList = new ArrayList<>();
        //处方id
        String recipelInfoId = (String) parameters.get("recipelInfoId");
        RecipelInfo recipelInfo = recipelInfoService.get(recipelInfoId);
        //MedicalRecord medicalRecord = medicalRecordDao.get(recipelInfo.getMedicalRecord().getId());
        //Registration registration = registrationService.get(medicalRecord.getRegistration().getId());
        Registration registration = registrationService.get(recipelInfo.getRegistration().getId());
        Patient patient = patientService.get(registration.getPatientId().getId());
        List<TollInfo> byRecipeId = tollInfoService.getByRecipeId(recipelInfoId);
        TollInfo tollInfo = null;
        if (null != byRecipeId && !byRecipeId.isEmpty()) {
            tollInfo = byRecipeId.get(0);
        }

        ChargeTicketEvt chargeTicketEvt = new ChargeTicketEvt();
        chargeTicketEvt.setClinicName(recipelInfo.getCompany().getName());
        chargeTicketEvt.setName("姓名：" + patient.getName());
        chargeTicketEvt.setGender("性别：" + patient.getGender().getName());
        chargeTicketEvt.setAge("年龄：" + patient.getAge());
        chargeTicketEvt.setTip(CHARGE_REMARK);
        if (null != tollInfo) {
            chargeTicketEvt.setCode("收费单号：" + tollInfo.getId());
            chargeTicketEvt.setType("收费类别：" + tollInfo.getPaymentType().getName());
            chargeTicketEvt.setProject("收费项目：" + recipelInfo.getRecipelType().getName());
            String createBy = tollInfo.getCreateBy();
            if (createBy.contains("(")) {
                createBy = createBy.substring(0,createBy.indexOf("("));
            }
            chargeTicketEvt.setPeople("收费人：" + createBy);
            chargeTicketEvt.setDate("就诊日期：" + format.format(recipelInfo.getCreateDate()));
            chargeTicketEvt.setOffice("就诊科室：" + registration.getClinicOffice().getName());
            chargeTicketEvt.setFee("收费金额：" + recipelInfo.getFee());
            String feeType = "";
            if ("payType_0".equals(tollInfo.getPaymentType().getValue()))
                feeType = "现金支付：" + recipelInfo.getFee();
            else if ("payType_1".equals(tollInfo.getPaymentType().getValue()))
                feeType = "支付宝支付：" + recipelInfo.getFee();
            else if ("payType_2".equals(tollInfo.getPaymentType().getValue()))
                feeType = "微信支付：" + recipelInfo.getFee();
            else if ("payType_3".equals(tollInfo.getPaymentType().getValue()))
                feeType = "银行卡支付：" + recipelInfo.getFee();
            else if ("payType_4".equals(tollInfo.getPaymentType().getValue()))
                feeType = "医保支付：" + recipelInfo.getFee();
            chargeTicketEvt.setFeeType(feeType);
        }
        outList.add(chargeTicketEvt);
        return outList;
    }

    /**
     * 普通/慢病处方
     * @param dsName
     * @param datasetName
     * @param parameters
     * @return
     */
    public List<ChronicDiseaseEvt> chronicDisease(String dsName, String datasetName, Map<String, Object> parameters){
        List<ChronicDiseaseEvt> list = ListUtil.list(false);
        //处方id
        String recipelInfoId = (String) parameters.get("recipelInfoId");
        RecipelInfo recipelInfo = recipelInfoService.get(recipelInfoId);
        List<RecipelDetail> detailList = recipelDetailDao.getByRecipelInfoId(recipelInfoId);
        ReceptionEvt receptionEvt = medicalRecordService.allNewQuery(recipelInfo.getRegistration().getId(), null);
        if (ObjectUtil.isNotNull(receptionEvt)) {
            Patient patient = receptionEvt.getPatient();
            Registration registration = receptionEvt.getRegistration();
            MedicalRecord medicalRecord = receptionEvt.getMedicalRecord();
            ChronicDiseaseEvt evt = new ChronicDiseaseEvt(patient,registration,medicalRecord,recipelInfo,detailList);
            List<TollInfo> tollInfos = tollInfoService.getByRecipeId(recipelInfo.getId());
            if (CollectionUtil.isNotEmpty(tollInfos)) {
                TollInfo tollInfo = tollInfos.get(0);
                String name = tollInfo.getCreateBy().substring(0, tollInfo.getCreateBy().indexOf("("));
                evt.setTollCollector("收费员："+name);
                evt.setDeploy("审核/调配："+name);
            }
            Parameter parameter = new Parameter("registration_id", "=", registration.getId());
            Parameter parameter1 = new Parameter("recipel_info_id", "=", recipelInfoId);
            List<Dispensing> dispensings = dispensingService.listAll(ListUtil.of(parameter, parameter1), null);
            if (CollectionUtil.isNotEmpty(dispensings)) {
                String name = dispensings.get(0).getCreateBy()
                        .substring(0, dispensings.get(0).getCreateBy().indexOf("("));
                evt.setDispensing("核对/发药：" + name);
            }
            list.add(evt);
        }
        return list;
    }

    /**
     * @return 组装挂号
     */
    public List<ChargeTicketEvt> registration(String dsName, String datasetName, Map<String, Object> parameters) {
        List<ChargeTicketEvt> outList = new ArrayList<>();
        //处方id
        String registrationId = (String) parameters.get("registrationId");
        Registration registration = registrationService.get(registrationId);
        Patient patient = patientService.get(registration.getPatientId().getId());
        ChargeTicketEvt chargeTicketEvt = new ChargeTicketEvt();
        chargeTicketEvt.setClinicName(registration.getCompany().getName() + "挂号凭证(客户联)");
        chargeTicketEvt.setName("姓名：" + patient.getName());
        chargeTicketEvt.setGender("性别：" + patient.getGender().getName());
        chargeTicketEvt.setAge("年龄：" + patient.getAge());
        chargeTicketEvt.setDate("挂号时间：" + format.format(registration.getCreateDate()));
        String createBy = registration.getCreateBy();
        if (createBy.contains("(")) {
            createBy = createBy.substring(0,createBy.indexOf("("));
        }
        chargeTicketEvt.setPeople("收费人：" + createBy);
        chargeTicketEvt.setOffice("挂号科室：" + registration.getClinicOffice().getName());
        chargeTicketEvt.setProject("挂号医生：" + registration.getDoctor().getName());
        chargeTicketEvt.setFee("挂号金额：" + registration.getRegistrationFee() + "元");
        String feeType = "";
        if ("payType_0".equals(registration.getPayType().getValue()))
            feeType = "现金支付：" + registration.getRegistrationFee() + "元";
        else if ("payType_1".equals(registration.getPayType().getValue()))
            feeType = "支付宝支付：" + registration.getRegistrationFee() + "元";
        else if ("payType_2".equals(registration.getPayType().getValue()))
            feeType = "微信支付：" + registration.getRegistrationFee() + "元";
        else if ("payType_3".equals(registration.getPayType().getValue()))
            feeType = "银行卡支付：" + registration.getRegistrationFee() + "元";
        else if ("payType_4".equals(registration.getPayType().getValue()))
            feeType = "医保支付：" + registration.getRegistrationFee() + "元";
        chargeTicketEvt.setFeeType(feeType);
        chargeTicketEvt.setTip(REGISTRATION_REMARK);
        Date zeroTimeOfDate = DateUtils.getZeroTimeOfDate(registration.getCreateDate());
        String todayStart = DateUtils.formatDateTime(zeroTimeOfDate);
        Date endTimeOfDate = DateUtils.getEndTimeOfDate(registration.getCreateDate());
        String todayEnd = DateUtils.formatDateTime(endTimeOfDate);
        List<Parameter> regParameters = new ArrayList<>();
        regParameters.add(new Parameter("a.doctor_id", "=",registration.getDoctor().getId()));
        regParameters.add(new Parameter("a.create_date", ">=",todayStart));
        regParameters.add(new Parameter("a.create_date", "<=",todayEnd));
        List<Registration> registrations = registrationService.listAll(regParameters, "a.create_date asc");
        String todayNum = "0";
        if (CollectionUtils.isNotEmpty(registrations)) {
            String patientId = registration.getPatientId().getId();
            for (int i = 0; i < registrations.size(); i++) {
                Registration docRegistration = registrations.get(i);
                if (patientId.equals(docRegistration.getPatientId().getId())) {
                    todayNum = (i + 1) + "";
                }
            }
        }
        chargeTicketEvt.setNum("今日序号："+todayNum);
        outList.add(chargeTicketEvt);
        return outList;
    }

    /**
     * 将一组数据固定分组，每组n个元素
     *
     * @param source 要分组的数据源
     * @param n      每组n个元素
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> fixedGrouping(List<T> source, int n) {
        if (null == source || source.size() == 0 || n <= 0)
            return null;
        List<List<T>> result = new ArrayList<List<T>>();
        int remainder = source.size() % n;//余数
        int size = (source.size() / n);//商 不算余数 要分多少组。有余数的话下面有单独处理余数数据的
        for (int i = 0; i < size; i++) {//循环要分多少组
            List<T> subset = null;
            subset = source.subList(i * n, (i + 1) * n);//截取list
            result.add(subset);
        }
        if (remainder > 0) {//有余数的情况下把余数得到的数据再添加到list里面
            List<T> subset = null;
            subset = source.subList(size * n, size * n + remainder);
            result.add(subset);
        }
        return result;
    }

}
