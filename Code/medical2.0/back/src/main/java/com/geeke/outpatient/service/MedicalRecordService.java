package com.geeke.outpatient.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.cure.entity.InspectionCheck;
import com.geeke.cure.entity.InspectionCheckInfo;
import com.geeke.cure.service.InspectionCheckDetailService;
import com.geeke.cure.service.InspectionCheckInfoService;
import com.geeke.cure.service.InspectionCheckService;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.dao.MedicalRecordDao;
import com.geeke.outpatient.entity.*;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.InstantPatient;
import com.geeke.stock.entity.InventoryVerification;
import com.geeke.stock.entity.Stuff;
import com.geeke.stock.service.DrugService;
import com.geeke.stock.service.InventoryVerificationService;
import com.geeke.stock.service.MedicinalStorageControlService;
import com.geeke.stock.service.StuffService;
import com.geeke.sys.entity.DictItem;
import com.geeke.sys.service.SysFileService;
import com.geeke.toll.service.TollInfoService;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.service.CostItemService;
import com.geeke.treatment.service.impl.CostItemPackageService;
import com.geeke.utils.IdGen;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SerialNoUtils;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 病历填写Service
 *
 * @author txl
 * @version 2022-06-13
 */

@Service("medicalRecordService")
@Transactional(readOnly = true)
public class MedicalRecordService extends CrudService<MedicalRecordDao, MedicalRecord> {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RecipelInfoService recipelInfoService;

    @Autowired
    private RecipelDetailService recipelDetailService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private CostItemService costItemService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private MedicalRecordDao medicalRecordDao;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private TollInfoService tollInfoService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private SerialNoUtils serialNoUtils;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Autowired
    private InspectionCheckService inspectionCheckService;

    @Autowired
    private InspectionCheckInfoService inspectionCheckInfoService;

    @Autowired
    private InspectionCheckDetailService inspectionCheckDetailService;

    @Autowired
    private CostItemPackageService costItemPackageService;

    @Autowired
    private InventoryVerificationService inventoryVerificationService;

    @Autowired
    private RemoteDiagnosisTreatmentService remoteDiagnosisTreatmentService;

    @Transactional(readOnly = false)
    public MedicalRecord save(MedicalRecord medicalRecord,
                              MultipartFile[] fileIdUploads,
                              String[] deleteIds) throws java.io.IOException {
        if (StringUtils.isBlank(medicalRecord.getFileId())) {
            medicalRecord.setFileId(IdGen.uuid());
        }
        // 保存附件
        sysFileService.changeAndSaveSysFileList(fileIdUploads, medicalRecord.getFileId());
        // 根据附件ID删除附件信息
        if (null != deleteIds && deleteIds.length > 0) {
            sysFileService.delete(deleteIds);
        }
        return save(medicalRecord);
    }

    /**
     挂号id查询病例信息
     */
    public List<MedicalRecord> getByOrder(String registration) {
        return medicalRecordDao.getByOrder(registration);
    }

    @Transactional(readOnly = false)
    public Registration allSave(@RequestBody MedicalRecipelEvt medicalRecipelEvt, MultipartFile[] fileIdUploads, String strDeleteIds) {
        //保存病历
        // String[] deleteIds = JSONObject.parseObject(strDeleteIds, String[].class);
        // try {
        //     String id = this.save(medicalRecipelEvt.getMedicalRecord(),
        //             fileIdUploads,
        //             deleteIds
        //     ).getId();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        return saveAllRecordInfo(medicalRecipelEvt);
        //添加待收费的收费单  待确认
//        tollInfoService.addTollInfo(registration.getPatientId(), registration.getCompany(), recipelInfos,medicalRecordSave);
    }

    private Registration saveAllRecordInfo(MedicalRecipelEvt medicalRecipelEvt) {
        Registration bindedRegistration;
        MedicalRecord medicalRecord = medicalRecipelEvt.getMedicalRecord();
        List<RecipelStastics> recipelStasticsList = new ArrayList<>();
        //是快速
        if (InstantPatient.INSTANT.getCode() == medicalRecipelEvt.getIsInstant()) {
            //一键存入用户信息
            Patient patient = medicalRecipelEvt.getPatient();
            Patient savedPatient = patientService.save(patient);

            //一键挂号
            Registration registration = new Registration();
            registration.setRegistrationFee(BigDecimal.ZERO);
            registration.setCompany(SessionUtils.getLoginTenant());
            registration.setDoctor(SessionUtils.getUser());
            DictItem dictItem = new DictItem();
            dictItem.setValue("treatType_0");
            registration.setTreatType(dictItem);
            DictItem dictItemTo = new DictItem();
            dictItemTo.setValue("infectType_0");
            registration.setInfectType(dictItemTo);
            registration.setPatientId(savedPatient);
            bindedRegistration = registrationService.save(registration);

            //新创建的挂号单绑定上
            medicalRecord.setRegistration(bindedRegistration);
        } else {
            bindedRegistration = registrationService.get(medicalRecipelEvt.getMedicalRecord().getRegistration().getId());
        }
        //保存病例信息
        medicalRecord.setDiagnoseDate(new Date());
        MedicalRecord medicalRecordSave = this.save(medicalRecord);
        //获取处方信息
        List<RecipelInfoEvt> recipelInfoEvtList = medicalRecipelEvt.getRecipelInfoEvtList();
        //用于保存收费
        List<RecipelInfo> recipelInfos = new ArrayList<>();
        if (null != recipelInfoEvtList && !recipelInfoEvtList.isEmpty()) {
            for (RecipelInfoEvt recipelInfoEvt : recipelInfoEvtList) {
                //保存处方信息
                RecipelInfo recipelInfo = recipelInfoEvt.getRecipelInfo();
                //recipelInfo.setMedicalRecord(medicalRecordSave);
                recipelInfo.setRegistration(bindedRegistration);
                //完成就诊的时候都没有付款
                recipelInfo.setIsPay("0");
                //TODO
//                recipelInfo.setSeq(0);
//                DictItem dictItem = new DictItem();
//                dictItem.setValue("smallType_0");
//                recipelInfo.setSmallType(dictItem);
//                recipelInfo.setIsFollowUp(0);

                //设置编码
                String recipelNo = serialNoUtils.generateSerialNo("");
                recipelInfo.setCode(recipelNo);
                RecipelInfo recipelInfoSave = recipelInfoService.save(recipelInfo);
                List<RecipelDetail> recipelDetailEvtList = recipelInfoEvt.getRecipelDetailEvtList();
                System.out.println(recipelDetailEvtList);
                if (null != recipelDetailEvtList && !recipelDetailEvtList.isEmpty()) {
                    //遍历处方
                    for (RecipelDetail recipelDetail : recipelDetailEvtList) {
                        //保存处方详情
                        recipelDetail.setRecipelInfo(recipelInfoSave);
                        recipelDetail.setMinTotal(recipelDetail.getTotal());
                        //TODO
//                        recipelDetail.setIsUnpackSell(0);
//                        recipelDetail.setStuffType("0");

                        recipelDetailService.save(recipelDetail);
                        //搜集处方信息
                        String value = recipelDetail.getStuffType();
                        RecipelStastics recipelStastics = RecipelStastics.builder().id(recipelDetail.getDrugStuffId().getDrugStuffId())
                                .stuffType(value)
                                .occupy(recipelDetail.getMinTotal() == null ? 0 : recipelDetail.getMinTotal()).build();
                        recipelStasticsList.add(recipelStastics);
                    }
                }
                recipelInfos.add(recipelInfoSave);
            }
        }

        //修改挂号状态为已就诊
        DictItem dictItem = new DictItem();
        dictItem.setValue("registrationStatus_1");
        bindedRegistration.setStatus(dictItem);
        bindedRegistration.setReceptionEndDate(new Date());
        bindedRegistration.setTreatType(medicalRecipelEvt.getMedicalRecord().getRegistration().getTreatType());
        bindedRegistration.setInfectType(medicalRecipelEvt.getMedicalRecord().getRegistration().getInfectType());
        registrationService.save(bindedRegistration);

        //校验是否足够
        //isEnough(recipelStasticsList);
        return bindedRegistration;
    }

    private void isEnough(List<RecipelStastics> recipelStasticsList) {
        List<RecipelStastics> occupied = recipelDetailService.getDetailStasticsForOccupy(30);
        Map<String, List<RecipelStastics>> collect = recipelStasticsList.stream()
                .collect(Collectors.groupingBy(RecipelStastics::getId));
        List<RecipelStastics> need = new ArrayList<>();
        collect.forEach((k, v) -> {
            int all = v.stream().mapToInt(RecipelStastics::getOccupy).sum();
            RecipelStastics one = RecipelStastics.builder().id(k).occupy(all).stuffType(v.get(0).getStuffType()).build();
            need.add(one);
        });
        for (RecipelStastics now : need) {
            String nowId = now.getId();
            for (RecipelStastics oc : occupied) {
                int inventory;
                String ocId = oc.getId();
                int total = oc.getOccupy() + now.getOccupy();
                //校验数量是否合法
                if (ocId.equals(nowId)) {
                    switch (oc.getStuffType()) {
                        //药品
                        case "0":
                        case "1":
                        case "2":
                            Drug drug = drugService.get(ocId);
                            if (null == drug) {
                                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "当前物料不在数据表中。"));
                            }
                            inventory = drug.getInventory();
                            if (inventory < total) {
                                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "当前物料库存不足，无法办理。"));
                            }
                            break;
                        //材料
                        case "4":
                            Stuff stuff = stuffService.get(ocId);
                            if (null == stuff) {
                                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "当前物料不在数据表中。"));
                            }
                            inventory = stuff.getInventory();
                            if (inventory < total) {
                                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "当前物料库存不足，无法办理。"));
                            }
                            break;
                        //诊疗项目
                        default:
                            break;
                    }
                }
            }
        }
    }

    public MedicalRecipelEvt allQuery(@RequestBody Registration registration, String chargeStatus) {
        MedicalRecipelEvt outEvt = new MedicalRecipelEvt();
        List<RecipelInfoEvt> recipelInfoEvtList = new ArrayList<>();
        //查询病历信息
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("company_id", "=", registration.getCompany().getId()));
        parameters.add(new Parameter("doctor_id", "=", registration.getDoctor().getId()));
        parameters.add(new Parameter("registration_id", "=", registration.getId()));
        List<MedicalRecord> medicalRecords = super.listAll(parameters, "");
        MedicalRecord medicalRecord = medicalRecords.get(0);
        outEvt.setMedicalRecord(medicalRecord);
        //查询处方信息
        parameters.clear();
        parameters.add(new Parameter("company_id", "=", registration.getCompany().getId()));
        parameters.add(new Parameter("registration_id", "=", registration.getId()));
        if (!StringUtils.isNullOrEmpty(chargeStatus)) {
            parameters.add(new Parameter("charge_status", "=", chargeStatus));
        }
        List<RecipelInfo> recipelInfos = recipelInfoService.listAll(parameters, "");
        if (!CollectionUtils.isEmpty(recipelInfos)) {
            for (RecipelInfo recipelInfo : recipelInfos) {
                //设置处方信息
                RecipelInfoEvt recipelInfoEvt = new RecipelInfoEvt();
                recipelInfoEvt.setRecipelInfo(recipelInfo);
                //设置处方详情x
                //查询处方详情
                parameters.clear();
                parameters.add(new Parameter("company_id", "=", registration.getCompany().getId()));
                parameters.add(new Parameter("recipel_info_id", "=", recipelInfo.getId()));
                List<RecipelDetail> recipelDetails = recipelDetailService.listAll(parameters, "");
                if (!CollectionUtils.isEmpty(recipelDetails)) {
                    for (RecipelDetail recipelDetail : recipelDetails) {
                        recipelDetail.setDrugStuffId(this.getDrugStuffEvt(recipelDetail));
                    }
                }
                recipelInfoEvt.setRecipelDetailEvtList(recipelDetails);
                recipelInfoEvtList.add(recipelInfoEvt);
            }
        }

        outEvt.setRecipelInfoEvtList(recipelInfoEvtList);
        logger.info("打印出参:{}", JSONObject.toJSON(outEvt));
        return outEvt;
    }

    public RecipelInfoEvt queryByInfoId(String reciptInfoId) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.clear();
        parameters.add(new Parameter("company_id", "=", SessionUtils.getLoginTenantId()));
        parameters.add(new Parameter("recipel_info_id", "=", reciptInfoId));
        List<RecipelDetail> recipelDetails = recipelDetailService.listAll(parameters, "");
        RecipelInfo recipelInfo = recipelInfoService.get(reciptInfoId);
        //设置处方信息
        RecipelInfoEvt recipelInfoEvt = new RecipelInfoEvt();
        recipelInfoEvt.setRecipelInfo(recipelInfo);
        if (null != recipelDetails && !recipelDetails.isEmpty()) {
            for (RecipelDetail recipelDetail : recipelDetails) {

                //查询药品信息
                DrugStuffEvt drugStuffEvt = new DrugStuffEvt();
                if ("recipelType_0".equals(recipelInfo.getRecipelType().getValue()) || "recipelType_1".equals(recipelInfo.getRecipelType().getValue()) || "recipelType_2".equals(recipelInfo.getRecipelType().getValue())) {
                    //中药、西药、输液处方查询药品信息
                    Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    drugStuffEvt.setDrugStuffId(drug.getId());
                    drugStuffEvt.setName(drug.getGoodsName());
                    drugStuffEvt.setPrice(drug.getPrice());
                    drugStuffEvt.setDosisUnit(drug.getDosisUnit());
                    drugStuffEvt.setPack(drug.getPack());
                    drugStuffEvt.setDrug(drug);
                } else if ("recipelType_3".equals(recipelInfo.getRecipelType().getValue())) {
                    //查询诊疗项目信息
                    CostItem costItem = costItemService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    drugStuffEvt.setDrugStuffId(costItem.getId());
                    drugStuffEvt.setName(costItem.getItemName());
                    drugStuffEvt.setPrice(costItem.getSalePrice());
                    drugStuffEvt.setDosisUnit(costItem.getUnit());
                    drugStuffEvt.setCostItem(costItem);
                } else if ("recipelType_4".equals(recipelInfo.getRecipelType().getValue())) {
                    //查询材料信息&药品信息为其他的信息
                    Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    if (null == drug) {
                        Stuff stuff = stuffService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                        drugStuffEvt.setDrugStuffId(stuff.getId());
                        drugStuffEvt.setName(stuff.getName());
                        drugStuffEvt.setPrice(stuff.getPriceOutSell());
                        drugStuffEvt.setDosisUnit(stuff.getPackUnit());
                        drugStuffEvt.setPack(stuff.getPackUnit());
                        drugStuffEvt.setStuff(stuff);
                    } else {
                        drugStuffEvt.setDrugStuffId(drug.getId());
                        drugStuffEvt.setName(drug.getGoodsName());
                        drugStuffEvt.setPrice(drug.getPrice());
                        drugStuffEvt.setDosisUnit(drug.getDosisUnit());
                        drugStuffEvt.setPack(drug.getPack());
                        drugStuffEvt.setDrug(drug);
                    }
                } else if ("recipelType_5".equals(recipelInfo.getRecipelType().getValue())) {
                    //零售处方
                    String retailType = recipelDetail.getRetailType();
                    if ("0".equals(retailType) || "1".equals(retailType) || "2".equals(retailType)) {
                        //中药、西药、输液处方查询药品信息
                        Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                        drugStuffEvt.setDrugStuffId(drug.getId());
                        drugStuffEvt.setName(drug.getGoodsName());
                        drugStuffEvt.setPrice(drug.getPrice());
                        drugStuffEvt.setDosisUnit(drug.getDosisUnit());
                        drugStuffEvt.setPack(drug.getPack());
                        drugStuffEvt.setDrug(drug);
                    } else if ("3".equals(retailType)) {
                        //查询诊疗项目信息
                        CostItem costItem = costItemService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                        drugStuffEvt.setDrugStuffId(costItem.getId());
                        drugStuffEvt.setName(costItem.getItemName());
                        drugStuffEvt.setPrice(costItem.getSalePrice());
                        drugStuffEvt.setDosisUnit(costItem.getUnit());
                        drugStuffEvt.setCostItem(costItem);
                    } else if ("4".equals(retailType)) {
                        //查询材料信息&药品信息为其他的信息
                        Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                        if (null == drug) {
                            Stuff stuff = stuffService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                            drugStuffEvt.setDrugStuffId(stuff.getId());
                            drugStuffEvt.setName(stuff.getName());
                            drugStuffEvt.setPrice(stuff.getPriceOutSell());
                            drugStuffEvt.setDosisUnit(stuff.getPackUnit());
                            drugStuffEvt.setPack(stuff.getPackUnit());
                            drugStuffEvt.setStuff(stuff);
                        } else {
                            drugStuffEvt.setDrugStuffId(drug.getId());
                            drugStuffEvt.setName(drug.getGoodsName());
                            drugStuffEvt.setPrice(drug.getPrice());
                            drugStuffEvt.setDosisUnit(drug.getDosisUnit());
                            drugStuffEvt.setPack(drug.getPack());
                            drugStuffEvt.setDrug(drug);
                        }
                    }
                }
                recipelDetail.setDrugStuffId(drugStuffEvt);
            }
        }
        recipelInfoEvt.setRecipelDetailEvtList(recipelDetails);
        logger.info("打印出参:{}", JSONObject.toJSON(recipelInfoEvt));
        return recipelInfoEvt;
    }

    public List<RecipelInfoEvt> getRecipelInfoEvts(List<RecipelInfo> recipelInfos) {
        List<Parameter> parameters = new ArrayList<>();
        List<RecipelInfoEvt> recipelInfoEvtList = new ArrayList<>();
        if (null != recipelInfos && !recipelInfos.isEmpty()) {
            for (RecipelInfo recipelInfo : recipelInfos) {
                //设置处方信息
                RecipelInfoEvt recipelInfoEvt = new RecipelInfoEvt();
                recipelInfoEvt.setRecipelInfo(recipelInfo);
                //设置处方详情
                //查询处方详情
                parameters.clear();
                parameters.add(new Parameter("company_id", "=", "998324809623052308"));
                parameters.add(new Parameter("recipel_info_id", "=", recipelInfo.getId()));
                List<RecipelDetail> recipelDetails = recipelDetailService.listAll(parameters, "");
                if (null != recipelDetails && !recipelDetails.isEmpty()) {
                    for (RecipelDetail recipelDetail : recipelDetails) {
                        //查询药品信息
                        DrugStuffEvt drugStuffEvt = new DrugStuffEvt();
                        if ("recipelType_0".equals(recipelInfo.getRecipelType().getValue()) || "recipelType_1".equals(recipelInfo.getRecipelType().getValue()) || "recipelType_2".equals(recipelInfo.getRecipelType().getValue())) {
                            //中药、西药、输液处方查询药品信息
                            Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                            drugStuffEvt.setDrugStuffId(drug.getId());
                            drugStuffEvt.setName(drug.getGoodsName());
                            drugStuffEvt.setPrice(drug.getPrice());
                            drugStuffEvt.setDosisUnit(drug.getDosisUnit());
                            drugStuffEvt.setPack(drug.getPack());
                            drugStuffEvt.setDrug(drug);
                        } else if ("recipelType_3".equals(recipelInfo.getRecipelType().getValue())) {
                            //查询诊疗项目信息
                            CostItem costItem = costItemService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                            drugStuffEvt.setDrugStuffId(costItem.getId());
                            drugStuffEvt.setName(costItem.getItemName());
                            drugStuffEvt.setPrice(costItem.getSalePrice());
                            drugStuffEvt.setDosisUnit(costItem.getUnit());
                            drugStuffEvt.setCostItem(costItem);
                        } else if ("recipelType_4".equals(recipelInfo.getRecipelType().getValue())) {
                            //查询材料信息&药品信息为其他的信息
                            Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                            if (null == drug) {
                                Stuff stuff = stuffService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                                drugStuffEvt.setDrugStuffId(stuff.getId());
                                drugStuffEvt.setName(stuff.getName());
                                drugStuffEvt.setPrice(stuff.getPriceOutSell());
                                drugStuffEvt.setDosisUnit(stuff.getPackUnit());
                                drugStuffEvt.setPack(stuff.getPackUnit());
                                drugStuffEvt.setStuff(stuff);
                            } else {
                                drugStuffEvt.setDrugStuffId(drug.getId());
                                drugStuffEvt.setName(drug.getGoodsName());
                                drugStuffEvt.setPrice(drug.getPrice());
                                drugStuffEvt.setDosisUnit(drug.getDosisUnit());
                                drugStuffEvt.setPack(drug.getPack());
                                drugStuffEvt.setDrug(drug);
                            }
                        }
                        recipelDetail.setDrugStuffId(drugStuffEvt);
                    }
                }
                recipelInfoEvt.setRecipelDetailEvtList(recipelDetails);
                recipelInfoEvtList.add(recipelInfoEvt);
            }
        }
        return recipelInfoEvtList;
    }

    public List<MedicalRecord> listMedicalRecordByChargeStatus(String chargeStatus) {
        return this.dao.listMedicalRecordByChargeStatus(chargeStatus, SessionUtils.getLoginTenantId());
    }

    @Transactional(readOnly = false)
    public String allNewSave(ReceptionEvt receptionEvt, MultipartFile[] fileIdUploads, String[] delFileIds,MultipartFile[] medicalFiles) throws Exception {

        //盘点时不能进行完成接诊的操作
        Company company = SessionUtils.getUser().getCompany();
        List<InventoryVerification> inventoryVerifications = inventoryVerificationService.getByCompanyId(company.getId());
        if(!CollectionUtils.isEmpty(inventoryVerifications)){
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "正在执行盘点操作,无法进行接诊"));
        }
        //普通接诊
        if (InstantPatient.NORMAL.getCode() == receptionEvt.getType())
        {
            if (!org.springframework.util.StringUtils.hasText(receptionEvt.getId()))
            {
                throw new RuntimeException("登记接诊信息不能为空.");
            }
            Registration registration = this.registrationService.get(receptionEvt.getId());
            if (Objects.isNull(registration))
            {
                throw new RuntimeException("登记接诊信息不存在.");
            }
            if (!(Objects.equals(registration.getStatus().getValue(), "registrationStatus_0") || Objects.equals(registration.getStatus().getValue(), "registrationStatus_1")))
            {
                throw new RuntimeException("登记接诊信息不是[待接诊]或[已完成接诊]状态.");
            }
            if (!org.springframework.util.StringUtils.hasText(receptionEvt.getMedicalRecord().getFileId()))
            {
                receptionEvt.getMedicalRecord().setFileId(IdGen.uuid());
            }
            if (!ArrayUtils.isEmpty(fileIdUploads))
            {
                //保存上传附件信息
                this.sysFileService.changeAndSaveSysFileList(fileIdUploads, receptionEvt.getMedicalRecord().getFileId());
            }
            if (!ArrayUtils.isEmpty(delFileIds))
            {
                //删除需要作废的附件信息
                this.sysFileService.delete(delFileIds);
            }
            MedicalRecord medicalRecord = receptionEvt.getMedicalRecord();
            medicalRecord.setRegistration(registration);
            //保存更新病例填写信息
            medicalRecord = this.save(medicalRecord);
            //保存病历的附件
            if (CollUtil.isNotEmpty(receptionEvt.getDeleteFileIdList())) {
                this.sysFileService.delete(receptionEvt.getDeleteFileIdList().toArray(new String[]{}));
            }
            if (ArrayUtils.isNotEmpty(medicalFiles)) {
                this.sysFileService.changeAndSaveSysFileList(medicalFiles, receptionEvt.getRegistration().getId());
            }
            //保存处方信息
            this.saveRecipelInfoEvt(receptionEvt.getRecipelInfoEvtList(), receptionEvt.getId());

            //更新保存登记信息
            if (Objects.equals(registration.getStatus().getValue(), "registrationStatus_0"))
            {
                DictItem statusDictItem = new DictItem();
                statusDictItem.setValue("registrationStatus_1");
                registration.setStatus(statusDictItem);
                registration.setReceptionEndDate(new Date());
            }
            registration.setTreatType(receptionEvt.getRegistration().getTreatType());
            registration.setInfectType(receptionEvt.getRegistration().getInfectType());
            registration.setBreathe(receptionEvt.getPatient().getBreathe());
            registration.setTemperature(receptionEvt.getPatient().getTemperature());
            registration.setPulse(receptionEvt.getPatient().getPulse());
            registration.setBloodPressure(receptionEvt.getPatient().getBloodPressure());
            registration.setMorbidityTime(receptionEvt.getRegistration().getMorbidityTime());
            receptionEvt.setRegistration(this.registrationService.save(registration));
            receptionEvt.setId(receptionEvt.getRegistration().getId());
        }
        else if (InstantPatient.INSTANT.getCode() == receptionEvt.getType() || InstantPatient.RETAIL.getCode() == receptionEvt.getType())
        {
            if (!org.springframework.util.StringUtils.hasText(receptionEvt.getId()))
            {
                receptionEvt.setId(IdGen.uuid());
            }
            Registration registration = this.registrationService.get(receptionEvt.getId());
            if (Objects.isNull(registration))
            {
                if (Objects.isNull(receptionEvt.getPatient()))
                {
                    throw new RuntimeException("[患者基础信息]不能为空.");
                }
                //处理基础信息（即患者信息）
                if (!org.springframework.util.StringUtils.hasText(receptionEvt.getPatient().getId()) || Objects.isNull(this.patientService.get(receptionEvt.getPatient().getId())))
                {
                    receptionEvt.getPatient().setId(null);
                    receptionEvt.setPatient(this.patientService.saveSuper(receptionEvt.getPatient()));
                }
                receptionEvt.setPatient(this.patientService.get(receptionEvt.getPatient().getId()));

                //Registration registrationEntity = new Registration();
                Registration registrationEntity = receptionEvt.getRegistration();
                registrationEntity.setId(null);
                registrationEntity.setCompany(SessionUtils.getLoginTenant());
                //TODO:科室处理?  取用户信息里面用户配置的科室(目前无法直接获取，因为用户里面是存的科室名称，而不是科室ID，无法形成映射关系)
                registrationEntity.setClinicOffice(null);
                registrationEntity.setDoctor(SessionUtils.getUser());
                registrationEntity.setPatientId(receptionEvt.getPatient());
                if (InstantPatient.INSTANT.getCode() == receptionEvt.getType()) {
                    registrationEntity.setTreatType(receptionEvt.getRegistration().getTreatType());
                    registrationEntity.setInfectType(receptionEvt.getRegistration().getInfectType());
                    DictItem sourceDictItem = new DictItem();
                    sourceDictItem.setValue("registrationSource_2");
                    registrationEntity.setSource(sourceDictItem);
                    registrationEntity.setReceptionStartDate(new Date());
                    registrationEntity.setReceptionEndDate(new Date());
                    registrationEntity.setPulse(receptionEvt.getRegistration().getPulse());
                    registrationEntity.setBreathe(receptionEvt.getRegistration().getBreathe());
                    registrationEntity.setTemperature(receptionEvt.getRegistration().getTemperature());
                    registrationEntity.setFreeRegistrationFee("1");
                    registrationEntity.setRegistrationFee(BigDecimal.ZERO);
                    DictItem statusDictItem = new DictItem();
                    statusDictItem.setValue("registrationStatus_1");
                    registrationEntity.setStatus(statusDictItem);
                }
                else if (InstantPatient.RETAIL.getCode() == receptionEvt.getType()) {
                    //TODO: 治疗类型   默认初诊
                    DictItem dictItem = new DictItem();
                    dictItem.setValue("treatType_0");
                    registrationEntity.setTreatType(dictItem);
                    DictItem dictItemTo = new DictItem();
                    dictItemTo.setValue("infectType_0");
                    registrationEntity.setInfectType(dictItemTo);
                    DictItem sourceDictItem = new DictItem();
                    sourceDictItem.setValue("registrationSource_3");
                    registrationEntity.setSource(sourceDictItem);
                    registrationEntity.setReceptionStartDate(new Date());
                    registrationEntity.setReceptionEndDate(new Date());
                    registrationEntity.setPulse(receptionEvt.getRegistration().getPulse());
                    registrationEntity.setBreathe(receptionEvt.getRegistration().getBreathe());
                    registrationEntity.setTemperature(receptionEvt.getRegistration().getTemperature());
                    registrationEntity.setFreeRegistrationFee("1");
                    registrationEntity.setRegistrationFee(BigDecimal.ZERO);
                }

                receptionEvt.setRegistration(this.registrationService.save(registrationEntity));
            }

            if (InstantPatient.INSTANT.getCode() == receptionEvt.getType()) {
                registration.setTreatType(receptionEvt.getRegistration().getTreatType());
                registration.setInfectType(receptionEvt.getRegistration().getInfectType());
                receptionEvt.setRegistration(this.registrationService.save(registration));

                if (!org.springframework.util.StringUtils.hasText(receptionEvt.getMedicalRecord().getFileId()))
                {
                    receptionEvt.getMedicalRecord().setFileId(IdGen.uuid());
                }
                if (!ArrayUtils.isEmpty(fileIdUploads))
                {
                    //保存上传附件信息
                    this.sysFileService.changeAndSaveSysFileList(fileIdUploads, receptionEvt.getMedicalRecord().getFileId());
                }
                if (!ArrayUtils.isEmpty(delFileIds))
                {
                    //删除需要作废的附件信息
                    this.sysFileService.delete(delFileIds);
                }
                MedicalRecord medicalRecord = receptionEvt.getMedicalRecord();
                medicalRecord.setRegistration(registration);
                //保存更新病例填写信息
                this.save(medicalRecord);
            }

            //保存处方信息
            this.saveRecipelInfoEvt(receptionEvt.getRecipelInfoEvtList(), receptionEvt.getId());
        }

        return receptionEvt.getId();
    }

    @Transactional(readOnly = false)
    public void saveRecipelInfoEvt(List<RecipelInfoEvt> recipelInfoEvtList, String registrationId) {
        /*
        * 处方信息为空，直接不用保存
        * */
        if (CollectionUtils.isEmpty(recipelInfoEvtList)) {
            return;
            //throw new RuntimeException("处方信息不能为空.");
        }

        Registration registration = this.registrationService.get(registrationId);
        for (int i = 0; i < recipelInfoEvtList.size(); i++) {
            RecipelInfoEvt recipelInfoEvt = recipelInfoEvtList.get(i);
            RecipelInfo recipelInfo = recipelInfoEvt.getRecipelInfo();
            if (ObjectUtil.isNull(recipelInfo.getChronicDisease()) || !StrUtil.equals("recipelType_0",recipelInfo.getRecipelType().getValue())) {
                recipelInfo.setChronicDisease(Boolean.FALSE);
            }
            // 设置应付价格默认为0
            for (RecipelDetail recipelDetail:recipelInfoEvt.getRecipelDetailEvtList()){
                recipelDetail.setActualPayment(BigDecimal.valueOf(0.00));
            }
            List<RecipelDetail> recipelDetailEvtList = recipelInfoEvt.getRecipelDetailEvtList();
            if (CollectionUtils.isEmpty(recipelDetailEvtList))
            {
                throw new RuntimeException("[" + recipelInfo.getName() + "]明细不能为空.");
            }
            if (org.springframework.util.StringUtils.hasText(recipelInfo.getId())) {
                RecipelInfo queryRecipelInfo = this.recipelInfoService.get(recipelInfo.getId());
                if (Objects.isNull(queryRecipelInfo)) {
                    recipelInfo.setId(null);
                }
                //作废处方编辑保存不处理
                if (Objects.nonNull(queryRecipelInfo) && Objects.equals(queryRecipelInfo.getStatus(), -1))
                {
                    continue;
                }
                //接诊完成，且收费完成或退费的不处理
                if (Objects.nonNull(queryRecipelInfo) && Objects.equals(queryRecipelInfo.getStatus(), 1)
                        && (Objects.equals(queryRecipelInfo.getChargeStatus(), 1) || Objects.equals(queryRecipelInfo.getChargeStatus(), -1))) {
                    continue;
                }
            }

            if (!org.springframework.util.StringUtils.hasText(recipelInfo.getId()))
            {
                String recipelNo = serialNoUtils.generateSerialNo(org.apache.commons.lang3.StringUtils.EMPTY);
                recipelInfo.setCode(recipelNo);
            }

            recipelInfo.setRegistration(registration);
            recipelInfo.setSeq(i+1);
            recipelInfo.setStatus(1);
            if (Objects.equals(registration.getSource().getValue(), "registrationSource_3")) {
                recipelInfo.setChargeStatus(1);
                recipelInfo.setIsPay("1");  //?是否付款是否前台传
            }
            else
            {
                recipelInfo.setChargeStatus(0);
                recipelInfo.setIsPay("0");
            }

            //保存处方主表
            RecipelInfo recipelInfoEntity = this.recipelInfoService.save(recipelInfo);

            List<RecipelDetail> recipelDetails = this.recipelDetailService.getByRecipelInfoId(recipelInfoEntity.getId());
            if (!CollectionUtils.isEmpty(recipelDetails)) {
                this.recipelDetailService.batchDelete(recipelDetails);
            }
            for (int k = 0; k < recipelDetailEvtList.size(); k++) {
                RecipelDetail recipelDetail = recipelDetailEvtList.get(k);
                recipelDetail.setId(null);
                recipelDetail.setRecipelInfo(recipelInfoEntity);
                recipelDetail.setMinTotal(recipelDetail.getTotal());
                if(recipelDetail.getInfuseGroup()!=null){
                    recipelDetail.setStuffType("2");
                }
                this.recipelDetailService.save(recipelDetail);
            }

            //TODO:动态库存操作
            this.medicinalStorageControlService.preOccupyStock(recipelInfoEntity);
        }
    }

    @Transactional(readOnly = false)
    public void saveRecipelInfoEvtTo(List<RecipelInfoEvt> recipelInfoEvtList,String id) {

        // 获取远程诊诊疗记录
        RemoteDiagnosisTreatment entity = remoteDiagnosisTreatmentService.diagnosisById(id);
        /*
         * 处方信息为空，直接不用保存
         * */
        if (CollectionUtils.isEmpty(recipelInfoEvtList)) {
            //return;
           throw new RuntimeException("处方信息不能为空.");
        }
        String registrationId = entity.getRegistrationId();
        // 获取登记信息
        Registration registration = this.registrationService.get(registrationId);

        if (Objects.equals(registration.getStatus().getValue(), "registrationStatus_0"))
        {
            DictItem statusDictItem = new DictItem();
            statusDictItem.setValue("registrationStatus_1");
            registration.setStatus(statusDictItem);
            registration.setReceptionEndDate(new Date());
        }
        // 更新登记信息
        this.registrationService.save(registration);

        for (int i = 0; i < recipelInfoEvtList.size(); i++) {
            // 获取处方信息和详情
            RecipelInfoEvt recipelInfoEvt = recipelInfoEvtList.get(i);
            // 获取处方信息
            RecipelInfo recipelInfo = recipelInfoEvt.getRecipelInfo();
            recipelInfo.setCompany(entity.getCompany());
            // 设置应付价格默认为0
            for (RecipelDetail recipelDetail:recipelInfoEvt.getRecipelDetailEvtList()){
                recipelDetail.setActualPayment(BigDecimal.valueOf(0.00));
            }
            // 获取处方详情
            List<RecipelDetail> recipelDetailEvtList = recipelInfoEvt.getRecipelDetailEvtList();
            if (CollectionUtils.isEmpty(recipelDetailEvtList))
            {
                throw new RuntimeException("[" + recipelInfo.getName() + "]明细不能为空.");
            }
            if (org.springframework.util.StringUtils.hasText(recipelInfo.getId())) {
                RecipelInfo queryRecipelInfo = this.recipelInfoService.get(recipelInfo.getId());
                if (Objects.isNull(queryRecipelInfo)) {
                    recipelInfo.setId(null);
                }
                //作废处方编辑保存不处理
                if (Objects.nonNull(queryRecipelInfo) && Objects.equals(queryRecipelInfo.getStatus(), -1))
                {
                    continue;
                }
                //接诊完成，且收费完成或退费的不处理
                if (Objects.nonNull(queryRecipelInfo) && Objects.equals(queryRecipelInfo.getStatus(), 1)
                        && (Objects.equals(queryRecipelInfo.getChargeStatus(), 1) || Objects.equals(queryRecipelInfo.getChargeStatus(), -1))) {
                    continue;
                }
            }

            if (!org.springframework.util.StringUtils.hasText(recipelInfo.getId()))
            {
                String recipelNo = serialNoUtils.generateSerialNo(org.apache.commons.lang3.StringUtils.EMPTY);
                recipelInfo.setCode(recipelNo);
            }

            recipelInfo.setRegistration(registration);
            recipelInfo.setSeq(i+1);
            recipelInfo.setStatus(1);
            if (Objects.equals(registration.getSource().getValue(), "registrationSource_3")) {
                recipelInfo.setChargeStatus(1);
                recipelInfo.setIsPay("1");  //?是否付款是否前台传
            }
            else
            {
                recipelInfo.setChargeStatus(0);
                recipelInfo.setIsPay("0");
            }

            recipelInfo.setIsDispension("1");
            recipelInfo.setIsPay("0");
            recipelInfo.setDispensionStatus(0);
            if (registration.getTreatType().getValue() == "treatType_0"){
                recipelInfo.setIsFollowUp(0);
            }else {
                recipelInfo.setIsFollowUp(1);
            }

            //保存处方主表
            RecipelInfo recipelInfoEntity = this.recipelInfoService.save(recipelInfo);

            List<RecipelDetail> recipelDetails = this.recipelDetailService.getByRecipelInfoId(recipelInfoEntity.getId());
            if (!CollectionUtils.isEmpty(recipelDetails)) {
                this.recipelDetailService.batchDelete(recipelDetails);
            }
            for (int k = 0; k < recipelDetailEvtList.size(); k++) {
                RecipelDetail recipelDetail = recipelDetailEvtList.get(k);
                // 查询药品信息
                final Drug drug1 = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                // 添加药品信息
                if (null == drug1){
                    final Drug drug = new Drug();
                    final DictItem dictItem = new DictItem();
                    dictItem.setValue(recipelDetail.getStuffType().equals("2") ? "medicalType_0" : "medicalType_2");
                    drug.setId(recipelDetail.getDrugStuffId().getDrugStuffId());
                    drug.setGoodsName(recipelDetail.getDrugStuffId().getName());
                    drug.setPrice(recipelDetail.getDrugStuffId().getPrice());
                    drug.setRetailPrice(recipelDetail.getDrugStuffId().getRetailPrice());
                    drug.setDosisUnit(recipelDetail.getDrugStuffId().getDosisUnit());
                    drug.setPreparationUnit(recipelDetail.getDrugStuffId().getPreparationUnit());
                    drug.setPack(recipelDetail.getDrugStuffId().getPack());
                    drug.setIsUnpackSell(recipelDetail.getDrugStuffId().getIsUnpackSell());
                    drug.setType(dictItem);
                    drug.setStatus("1");
                    drug.setCreateDate(new Date());
                    drug.setUpdateDate(new Date());
                    drug.setCreateBy("远程医院");
                    drug.setUpdateBy("远程医院");
                    drugService.addDurg(drug);
                }

                recipelDetail.setCompany(entity.getCompany());
                recipelDetail.setId(null);
                recipelDetail.setSeq(k);
                recipelDetail.setRecipelInfo(recipelInfoEntity);
                recipelDetail.setMinTotal(recipelDetail.getTotal());
                recipelDetail.setIsUnpackSell(1);
                recipelDetail.setIsExtra(0);
                if(recipelDetail.getInfuseGroup()!=null){
                    recipelDetail.setStuffType("2");
                }
                this.recipelDetailService.save(recipelDetail);
            }
        }
    }

    public ReceptionEvt allNewQuery(String registrationId, String[] recipelInfoIds)
    {
        ReceptionEvt receptionEvt = new ReceptionEvt();
        if (org.springframework.util.StringUtils.hasText(registrationId))
        {
            Registration registration = this.registrationService.get(registrationId);
            if (Objects.isNull(registration) || !org.springframework.util.StringUtils.hasText(registration.getId()))
            {
                return receptionEvt;
            }
            if (Objects.nonNull(registration.getPatientId()) && org.springframework.util.StringUtils.hasText(registration.getPatientId().getId()))
            {
                receptionEvt.setPatient(this.patientService.get(registration.getPatientId().getId()));
            }

            //接诊类型:0-普通接诊  1-快速接诊  2-零售收费
            if (Objects.equals(registration.getSource().getValue(), "registrationSource_2"))
            {
                receptionEvt.setType(1);
            }
            else if (Objects.equals(registration.getSource().getValue(), "registrationSource_3"))
            {
                receptionEvt.setType(2);
            }
            else {
                receptionEvt.setType(0);
            }

            receptionEvt.setId(registration.getId());
            receptionEvt.setRegistration(registration);

            List<Parameter> parameters = new ArrayList<>();
            parameters.add(new Parameter("registration_id", "=", registration.getId()));
            //查询病历信息
            List<MedicalRecord> medicalRecordList = super.listAll(parameters, org.apache.commons.lang3.StringUtils.EMPTY);
            if (!CollectionUtils.isEmpty(medicalRecordList))
            {
                receptionEvt.setMedicalRecord(medicalRecordList.get(0));
                receptionEvt.getMedicalRecord().setRegistration(registration);
            }

            List<RecipelInfoEvt> recipelInfoEvtList = new ArrayList<>();
            List<RecipelInfo> recipelInfoList = this.recipelInfoService.listAll(parameters, "a.seq asc");
            if (!CollectionUtils.isEmpty(recipelInfoList))
            {
                for (int i = 0; i < recipelInfoList.size(); i++) {
                    RecipelInfoEvt recipelInfoEvt = new RecipelInfoEvt();
                    RecipelInfo recipelInfo = recipelInfoList.get(i);
                    recipelInfo.setRegistration(registration);


                    parameters.clear();
                    parameters.add(new Parameter("company_id", "=", registration.getCompany().getId()));
                    parameters.add(new Parameter("recipel_info_id", "=", recipelInfo.getId()));
                    List<RecipelDetail> recipelDetailList = this.recipelDetailService.listAll(parameters, org.apache.commons.lang3.StringUtils.EMPTY);
                    if (!CollectionUtils.isEmpty(recipelDetailList))
                    {
                        for (RecipelDetail recipelDetail:recipelDetailList)
                        {
                            recipelDetail.setDrugStuffId(this.getDrugStuffEvt(recipelDetail));

                        }
                    }

                    recipelInfoEvt.setRecipelInfo(recipelInfo);
                    recipelInfoEvt.setRecipelDetailEvtList(recipelDetailList);
                    recipelInfoEvtList.add(recipelInfoEvt);
                }
            }

            receptionEvt.setRecipelInfoEvtList(recipelInfoEvtList);
        }

        return receptionEvt;
    }

    public DrugStuffEvt getDrugStuffEvt(RecipelDetail recipelDetail) {
        DrugStuffEvt drugStuffEvt = new DrugStuffEvt();
        String stuffType = recipelDetail.getStuffType();        // 物料类型:0-西药 1-中药  2-输液 3-诊疗项目  4-材料
        if ("0".equals(stuffType) || "1".equals(stuffType) || "2".equals(stuffType)) {
            //中药、西药、输液处方查询药品信息
            Drug drug = this.drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
            drugStuffEvt.setDrugStuffId(drug.getId());
            drugStuffEvt.setName(drug.getGoodsName());
            drugStuffEvt.setPrice(drug.getPrice());
            drugStuffEvt.setRetailPrice(drug.getRetailPrice());
            drugStuffEvt.setIsUnpackSell(drug.getIsUnpackSell());
            drugStuffEvt.setDosisUnit(drug.getDosisUnit());
            drugStuffEvt.setPreparationUnit(drug.getPreparationUnit());
            drugStuffEvt.setPack(drug.getPack());
            drugStuffEvt.setDrug(drug);
        } else if ("3".equals(stuffType)) {
            //查询诊疗项目信息
            CostItem costItem = this.costItemService.get(recipelDetail.getDrugStuffId().getDrugStuffId());


            //根据处方详情id获取其检验检查结果
            List<InspectionCheck> inspectionChecks = inspectionCheckService.getByRecipelDetail(recipelDetail.getId());
            if(!CollectionUtils.isEmpty(inspectionChecks) && "1".equals(inspectionChecks.get(0).getStatus())){
                InspectionCheckInfo byInspecId = inspectionCheckInfoService.getByInspecId(inspectionChecks.get(0).getId());

                drugStuffEvt.setInspectionCheckInfo(byInspecId);
            }

            drugStuffEvt.setDrugStuffId(costItem.getId());
            drugStuffEvt.setName(costItem.getItemName());
            drugStuffEvt.setPrice(costItem.getSalePrice());

            drugStuffEvt.setRetailPrice(null);
            drugStuffEvt.setIsUnpackSell("0");
            drugStuffEvt.setDosisUnit(costItem.getUnit());
            drugStuffEvt.setPack(costItem.getUnit());
            drugStuffEvt.setCostItem(costItem);




        } else if ("4".equals(stuffType)) {
            Stuff stuff = this.stuffService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
            if(stuff!=null){
            drugStuffEvt.setDrugStuffId(stuff.getId());
            drugStuffEvt.setName(stuff.getName());
            drugStuffEvt.setPrice(stuff.getPriceOutSell());
            drugStuffEvt.setRetailPrice(stuff.getRetailPrice());
            drugStuffEvt.setIsUnpackSell(stuff.getIsUnpackSell());
            drugStuffEvt.setDosisUnit(stuff.getMinUnit());
            drugStuffEvt.setPreparationUnit(stuff.getMinUnit());
            drugStuffEvt.setPack(stuff.getPackUnit());
            drugStuffEvt.setStuff(stuff);
            }
        }

        return drugStuffEvt;
    }
    @Transactional(readOnly = false)
    public List<RecipelInfoDTO> getByRegistrationId(List<Parameter> parameters,int limit,int offset,String order) {
        PageRequest pageRequest = new PageRequest(offset, limit,  parameters, order);
        List<RecipelInfoDTO> recipelInfos=recipelInfoService.getHistoryRecipel(pageRequest);
        for (RecipelInfoDTO recipelInfoDTO:
        recipelInfos) {
            List<RecipelDetail> byRecipelInfoId = recipelDetailService.getByRecipelInfoId(recipelInfoDTO.getId());
            for (RecipelDetail recipelDetail:
                    byRecipelInfoId) {
                {
                    recipelDetail.setDrugStuffId(medicalRecordService.getDrugStuffEvt(recipelDetail));
                }
            }
            recipelInfoDTO.setRecipelDetail(byRecipelInfoId);

            //获取患者信息
            Patient patient = patientService.getPatientByregistrationId(recipelInfoDTO.getRegistration().getId());
            recipelInfoDTO.setPatient(patient);

            //获取病例信息
            MedicalRecord byRegistrationId = this.dao.getByRegistrationId(recipelInfoDTO.getRegistration().getId());
            recipelInfoDTO.setMedicalRecord(byRegistrationId);
        }
        return recipelInfos;
    }

    public List<Recordpatvo> recordpatlist(String id){
        Company company = SessionUtils.getUser().getCompany();
        return medicalRecordDao.recordpatlist(company.getId(),id);
    }


}