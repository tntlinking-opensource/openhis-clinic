package com.geeke.outpatient.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.common.constants.BizConstants;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.data.SearchParamsBuilder;
import com.geeke.common.service.CrudService;
import com.geeke.common.service.ServiceException;
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
import com.geeke.stock.entity.MedicinalStockControl;
import com.geeke.stock.dao.MedicinalStockControlDao;
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
import com.geeke.utils.SerialNoUtils;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
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

    @Lazy
    @Autowired
    private RegistrationService registrationService;

    @Lazy
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

    @Lazy
    @Autowired
    private TollInfoService tollInfoService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private SerialNoUtils serialNoUtils;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Autowired
    private MedicinalStockControlDao medicinalStockControlDao;

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

    @Autowired
    private PresDrugService presDrugService;

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
    public Registration allSave(MedicalRecipelEvt medicalRecipelEvt, MultipartFile[] fileIdUploads, String strDeleteIds) {
        //保存病历
        return saveAllRecordInfo(medicalRecipelEvt);
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
            dictItem.setValue(BizConstants.TREAT_TYPE_NORMAL);
            registration.setTreatType(dictItem);
            DictItem dictItemTo = new DictItem();
            dictItemTo.setValue(BizConstants.INFECT_TYPE_NONE);
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
                //以下字段已由前端或默认值处理
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
                if (null != recipelDetailEvtList && !recipelDetailEvtList.isEmpty()) {
                    //遍历处方
                    for (RecipelDetail recipelDetail : recipelDetailEvtList) {
                        //保存处方详情
                        recipelDetail.setRecipelInfo(recipelInfoSave);
                        // 如果前端没有传minTotal（制剂单位总量），则用total作为默认值
                        if (recipelDetail.getMinTotal() == null || recipelDetail.getMinTotal() == 0) {
                            recipelDetail.setMinTotal(recipelDetail.getTotal());
                        }
                        //以下字段已由前端或默认值处理
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
        dictItem.setValue(BizConstants.REG_STATUS_VISITED);
        bindedRegistration.setStatus(dictItem);
        bindedRegistration.setReceptionEndDate(new Date());
        bindedRegistration.setTreatType(medicalRecipelEvt.getMedicalRecord().getRegistration().getTreatType());
        bindedRegistration.setInfectType(medicalRecipelEvt.getMedicalRecord().getRegistration().getInfectType());
        registrationService.save(bindedRegistration);

        //校验是否足够
        //isEnough(recipelStasticsList);
        return bindedRegistration;
    }

    private void isEnough(String companyId, List<RecipelStastics> recipelStasticsList) {
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
                String ocId = oc.getId();
                int total = oc.getOccupy() + now.getOccupy();
                if (ocId.equals(nowId)) {
                    // 从新库存系统读取可用库存（MedicinalStockControl.surplusStock）
                    List<MedicinalStockControl> stockList = medicinalStockControlDao.inventory(companyId, ocId);
                    if (stockList == null || stockList.isEmpty()) {
                        throw new ServiceException("当前物料不在数据表中。");
                    }
                    BigDecimal surplusStock = stockList.get(0).getSurplusStock();
                    if (surplusStock == null || surplusStock.compareTo(new BigDecimal(total)) < 0) {
                        throw new ServiceException("当前物料库存不足，无法办理。");
                    }
                }
            }
        }
    }

    public MedicalRecipelEvt allQuery(Registration registration, String chargeStatus) {
        MedicalRecipelEvt outEvt = new MedicalRecipelEvt();
        List<RecipelInfoEvt> recipelInfoEvtList = new ArrayList<>();
        //查询病历信息
        List<Parameter> parameters = SearchParamsBuilder.create()
                .eq("company_id", registration.getCompany().getId())
                .eq("doctor_id", registration.getDoctor().getId())
                .eq("registration_id", registration.getId())
                .build();
        List<MedicalRecord> medicalRecords = super.listAll(parameters, "");
        MedicalRecord medicalRecord = medicalRecords.get(0);
        outEvt.setMedicalRecord(medicalRecord);
        //查询处方信息
        SearchParamsBuilder recipelParams = SearchParamsBuilder.create()
                .eq("company_id", registration.getCompany().getId())
                .eq("registration_id", registration.getId());
        if (!StringUtils.isNullOrEmpty(chargeStatus)) {
            recipelParams.eq("charge_status", chargeStatus);
        }
        List<RecipelInfo> recipelInfos = recipelInfoService.listAll(recipelParams.build(), "");
        if (!CollectionUtils.isEmpty(recipelInfos)) {
            // 批量查询所有处方明细（优化 N+1 查询）
            List<String> recipelInfoIds = recipelInfos.stream()
                    .map(RecipelInfo::getId)
                    .collect(Collectors.toList());
            Map<String, List<RecipelDetail>> detailsMap = recipelDetailService.getByRecipelInfoIds(recipelInfoIds);

            for (RecipelInfo recipelInfo : recipelInfos) {
                //设置处方信息
                RecipelInfoEvt recipelInfoEvt = new RecipelInfoEvt();
                recipelInfoEvt.setRecipelInfo(recipelInfo);
                //从批量查询结果中获取处方详情
                List<RecipelDetail> recipelDetails = detailsMap.getOrDefault(recipelInfo.getId(), Collections.emptyList());
                if (!CollectionUtils.isEmpty(recipelDetails)) {
                    for (RecipelDetail recipelDetail : recipelDetails) {
                        recipelDetail.setRecipelInfo(recipelInfo);
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
        List<Parameter> parameters = SearchParamsBuilder.createWithTenant(SessionUtils.getLoginTenantId())
                .eq("recipel_info_id", reciptInfoId)
                .build();
        List<RecipelDetail> recipelDetails = recipelDetailService.listAll(parameters, "");
        RecipelInfo recipelInfo = recipelInfoService.get(reciptInfoId);
        //设置处方信息
        RecipelInfoEvt recipelInfoEvt = new RecipelInfoEvt();
        recipelInfoEvt.setRecipelInfo(recipelInfo);
        if (null != recipelDetails && !recipelDetails.isEmpty()) {
            for (RecipelDetail recipelDetail : recipelDetails) {

                //查询药品信息
                DrugStuffEvt drugStuffEvt = new DrugStuffEvt();
                if (BizConstants.RECIPEL_TYPE_WESTERN.equals(recipelInfo.getRecipelType().getValue()) || BizConstants.RECIPEL_TYPE_CHINESE.equals(recipelInfo.getRecipelType().getValue()) || BizConstants.RECIPEL_TYPE_INFUSION.equals(recipelInfo.getRecipelType().getValue())) {
                    //中药、西药、输液处方查询药品信息
                    Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    drugStuffEvt.setDrugStuffId(drug.getId());
                    drugStuffEvt.setName(drug.getGoodsName());
                    drugStuffEvt.setPrice(drug.getPrice());
                    drugStuffEvt.setDosisUnit(drug.getDosisUnit());
                    drugStuffEvt.setPack(drug.getPack());
                    drugStuffEvt.setDrug(drug);
                } else if (BizConstants.RECIPEL_TYPE_OTHER.equals(recipelInfo.getRecipelType().getValue())) {
                    //查询诊疗项目信息
                    CostItem costItem = costItemService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    drugStuffEvt.setDrugStuffId(costItem.getId());
                    drugStuffEvt.setName(costItem.getItemName());
                    drugStuffEvt.setPrice(costItem.getSalePrice());
                    drugStuffEvt.setDosisUnit(costItem.getUnit());
                    drugStuffEvt.setCostItem(costItem);
                } else if (BizConstants.RECIPEL_TYPE_PATENT.equals(recipelInfo.getRecipelType().getValue())) {
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
                } else if (BizConstants.RECIPEL_TYPE_EXTERNAL.equals(recipelInfo.getRecipelType().getValue())) {
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
        List<RecipelInfoEvt> recipelInfoEvtList = new ArrayList<>();
        if (null != recipelInfos && !recipelInfos.isEmpty()) {
            // 批量查询所有处方明细（优化 N+1 查询）
            List<String> recipelInfoIds = recipelInfos.stream()
                    .map(RecipelInfo::getId)
                    .collect(Collectors.toList());
            Map<String, List<RecipelDetail>> detailsMap = recipelDetailService.getByRecipelInfoIds(recipelInfoIds);

            for (RecipelInfo recipelInfo : recipelInfos) {
                RecipelInfoEvt recipelInfoEvt = new RecipelInfoEvt();
                recipelInfoEvt.setRecipelInfo(recipelInfo);
                // 从批量查询结果中获取处方详情
                List<RecipelDetail> recipelDetails = detailsMap.getOrDefault(recipelInfo.getId(), Collections.emptyList());
                if (!CollectionUtils.isEmpty(recipelDetails)) {
                    for (RecipelDetail recipelDetail : recipelDetails) {
                        recipelDetail.setDrugStuffId(this.getDrugStuffEvt(recipelDetail));
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
        if (!CollectionUtils.isEmpty(inventoryVerifications)) {
            throw new ServiceException("正在执行盘点操作,无法进行接诊");
        }
        //普通接诊
        if (InstantPatient.NORMAL.getCode() == receptionEvt.getType()) {
            if (!org.springframework.util.StringUtils.hasText(receptionEvt.getId())) {
                throw new ServiceException("登记接诊信息不能为空.");
            }
            Registration registration = this.registrationService.get(receptionEvt.getId());
            if (Objects.isNull(registration)) {
                throw new ServiceException("登记接诊信息不存在.");
            }
            if (!(Objects.equals(registration.getStatus().getValue(), BizConstants.REG_STATUS_PENDING) || Objects.equals(registration.getStatus().getValue(), BizConstants.REG_STATUS_VISITED))) {
                throw new ServiceException("登记接诊信息不是[待接诊]或[已完成接诊]状态.");
            }
            if (!org.springframework.util.StringUtils.hasText(receptionEvt.getMedicalRecord().getFileId())) {
                receptionEvt.getMedicalRecord().setFileId(IdGen.uuid());
            }
            if (!ArrayUtils.isEmpty(fileIdUploads)) {
                //保存上传附件信息
                this.sysFileService.changeAndSaveSysFileList(fileIdUploads, receptionEvt.getMedicalRecord().getFileId());
            }
            if (!ArrayUtils.isEmpty(delFileIds)) {
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
            if (Objects.equals(registration.getStatus().getValue(), BizConstants.REG_STATUS_PENDING)) {
                DictItem statusDictItem = new DictItem();
                statusDictItem.setValue(BizConstants.REG_STATUS_VISITED);
                registration.setStatus(statusDictItem);
                registration.setReceptionEndDate(new Date());
            }
            if(!receptionEvt.getRecipelInfoEvtList().isEmpty() && receptionEvt.getRecipelInfoEvtList().get(0).getRecipelInfo().getIsPre()!=null&&receptionEvt.getRecipelInfoEvtList().size() == 1 && receptionEvt.getRecipelInfoEvtList().get(0).getRecipelInfo().getIsPre()){
                //单号单电子处方设置号结束就诊流程
                registration.setIsPre(true);
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
        } else if (InstantPatient.INSTANT.getCode() == receptionEvt.getType() || InstantPatient.RETAIL.getCode() == receptionEvt.getType()) {
            if (!org.springframework.util.StringUtils.hasText(receptionEvt.getId())) {
                receptionEvt.setId(IdGen.uuid());
            }
            Registration registration = this.registrationService.get(receptionEvt.getId());
            if (Objects.isNull(registration)) {
                if (Objects.isNull(receptionEvt.getPatient())) {
                    throw new ServiceException("[患者基础信息]不能为空.");
                }
                //处理基础信息（即患者信息）
                if (!org.springframework.util.StringUtils.hasText(receptionEvt.getPatient().getId()) || Objects.isNull(this.patientService.get(receptionEvt.getPatient().getId()))) {
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
                    sourceDictItem.setValue(BizConstants.REG_SOURCE_QUICK);
                    registrationEntity.setSource(sourceDictItem);
                    registrationEntity.setReceptionStartDate(new Date());
                    registrationEntity.setReceptionEndDate(new Date());
                    registrationEntity.setPulse(receptionEvt.getRegistration().getPulse());
                    registrationEntity.setBreathe(receptionEvt.getRegistration().getBreathe());
                    registrationEntity.setTemperature(receptionEvt.getRegistration().getTemperature());
                    registrationEntity.setFreeRegistrationFee("1");
                    registrationEntity.setRegistrationFee(BigDecimal.ZERO);
                    DictItem statusDictItem = new DictItem();
                    statusDictItem.setValue(BizConstants.REG_STATUS_VISITED);
                    registrationEntity.setStatus(statusDictItem);
                } else if (InstantPatient.RETAIL.getCode() == receptionEvt.getType()) {
                    //TODO: 治疗类型   默认初诊
                    DictItem dictItem = new DictItem();
                    dictItem.setValue(BizConstants.TREAT_TYPE_NORMAL);
                    registrationEntity.setTreatType(dictItem);
                    DictItem dictItemTo = new DictItem();
                    dictItemTo.setValue(BizConstants.INFECT_TYPE_NONE);
                    registrationEntity.setInfectType(dictItemTo);
                    DictItem sourceDictItem = new DictItem();
                    sourceDictItem.setValue(BizConstants.REG_SOURCE_RETAIL);
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

                if (!org.springframework.util.StringUtils.hasText(receptionEvt.getMedicalRecord().getFileId())) {
                    receptionEvt.getMedicalRecord().setFileId(IdGen.uuid());
                }
                if (!ArrayUtils.isEmpty(fileIdUploads)) {
                    //保存上传附件信息
                    this.sysFileService.changeAndSaveSysFileList(fileIdUploads, receptionEvt.getMedicalRecord().getFileId());
                }
                if (!ArrayUtils.isEmpty(delFileIds)) {
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
            if (ObjectUtil.isNull(recipelInfo.getChronicDisease()) || !StrUtil.equals(BizConstants.RECIPEL_TYPE_WESTERN,recipelInfo.getRecipelType().getValue())) {
                recipelInfo.setChronicDisease(Boolean.FALSE);
            }
            if (ObjectUtil.isNull(recipelInfo.getChronicDisease()) || !StrUtil.equals(BizConstants.RECIPEL_TYPE_WESTERN,recipelInfo.getRecipelType().getValue())) {
                recipelInfo.setIsPre(Boolean.FALSE);
            }
            if( recipelInfo.getIsPre()!= null && recipelInfo.getIsPre()){
                //电子处方特殊处理
                List<RecipelDetail> recipelDetailEvtList = recipelInfoEvt.getRecipelDetailEvtList();
                if (CollectionUtils.isEmpty(recipelDetailEvtList)) {
                    throw new ServiceException("[" + recipelInfo.getName() + "]明细不能为空.");
                }
                if (org.springframework.util.StringUtils.hasText(recipelInfo.getId())) {
                    RecipelInfo queryRecipelInfo = this.recipelInfoService.get(recipelInfo.getId());
                    if (Objects.isNull(queryRecipelInfo)) {
                        recipelInfo.setId(null);
                    }
                    //作废处方编辑保存不处理
                    if (Objects.nonNull(queryRecipelInfo) && Objects.equals(queryRecipelInfo.getStatus(), -1)) {
                        continue;
                    }
                    //接诊完成，且收费完成或退费的不处理
                    if (Objects.nonNull(queryRecipelInfo) && Objects.equals(queryRecipelInfo.getStatus(), 1)
                            && (Objects.equals(queryRecipelInfo.getChargeStatus(), 1) || Objects.equals(queryRecipelInfo.getChargeStatus(), -1))) {
                        continue;
                    }
                }

                if (!org.springframework.util.StringUtils.hasText(recipelInfo.getId())) {
                    String recipelNo = serialNoUtils.generateSerialNo(org.apache.commons.lang3.StringUtils.EMPTY);
                    recipelInfo.setCode(recipelNo);
                }
                recipelInfo.setRegistration(registration);
                recipelInfo.setSeq(i + 1);
                recipelInfo.setStatus(1);
                if (Objects.equals(registration.getSource().getValue(), BizConstants.REG_SOURCE_RETAIL)) {
                    recipelInfo.setChargeStatus(1);
                    recipelInfo.setIsPay("1");  //?是否付款是否前台传
                } else {
                    recipelInfo.setChargeStatus(0);
                    recipelInfo.setIsPay("0");
                }
                //设置处方收费与发药状态 电子处方不走后续流程
                recipelInfo.setChargeStatus(2);
                recipelInfo.setIsPay("2");
                recipelInfo.setDispensionStatus(2);

                //保存处方主表
                RecipelInfo recipelInfoEntity = this.recipelInfoService.save(recipelInfo);
                //保存处方详情表
                List<RecipelDetail> recipelDetails = this.recipelDetailService.getByRecipelInfoId(recipelInfoEntity.getId());
                if (!CollectionUtils.isEmpty(recipelDetails)) {
                    this.recipelDetailService.batchDelete(recipelDetails);
                }
                for (RecipelDetail recipelDetail : recipelDetailEvtList) {
                    //应付实付都为0
                    recipelDetail.setActualPayment(BigDecimal.valueOf(0.00));
                    recipelDetail.setAllFee(BigDecimal.valueOf(0.00));
                    recipelDetail.setId(null);
                    recipelDetail.setRecipelInfo(recipelInfoEntity);
                    // 如果前端没有传minTotal（制剂单位总量），则用total作为默认值
                    if (recipelDetail.getMinTotal() == null || recipelDetail.getMinTotal() == 0) {
                        recipelDetail.setMinTotal(recipelDetail.getTotal());
                    }
                    if (recipelDetail.getInfuseGroup() != null) {
                        recipelDetail.setStuffType("2");
                    }
                    this.recipelDetailService.save(recipelDetail);
                }
            } else {
                // 设置应付价格默认为0
                for (RecipelDetail recipelDetail : recipelInfoEvt.getRecipelDetailEvtList()) {
                    recipelDetail.setActualPayment(BigDecimal.valueOf(0.00));
                }
                List<RecipelDetail> recipelDetailEvtList = recipelInfoEvt.getRecipelDetailEvtList();
                if (CollectionUtils.isEmpty(recipelDetailEvtList)) {
                    throw new ServiceException("[" + recipelInfo.getName() + "]明细不能为空.");
                }
                if (org.springframework.util.StringUtils.hasText(recipelInfo.getId())) {
                    RecipelInfo queryRecipelInfo = this.recipelInfoService.get(recipelInfo.getId());
                    if (Objects.isNull(queryRecipelInfo)) {
                        recipelInfo.setId(null);
                    }
                    //作废处方编辑保存不处理
                    if (Objects.nonNull(queryRecipelInfo) && Objects.equals(queryRecipelInfo.getStatus(), -1)) {
                        continue;
                    }
                    //接诊完成，且收费完成或退费的不处理
                    if (Objects.nonNull(queryRecipelInfo) && Objects.equals(queryRecipelInfo.getStatus(), 1)
                            && (Objects.equals(queryRecipelInfo.getChargeStatus(), 1) || Objects.equals(queryRecipelInfo.getChargeStatus(), -1))) {
                        continue;
                    }
                }

                if (!org.springframework.util.StringUtils.hasText(recipelInfo.getId())) {
                    String recipelNo = serialNoUtils.generateSerialNo(org.apache.commons.lang3.StringUtils.EMPTY);
                    recipelInfo.setCode(recipelNo);
                }

                recipelInfo.setRegistration(registration);
                recipelInfo.setSeq(i + 1);
                recipelInfo.setStatus(1);
                if (Objects.equals(registration.getSource().getValue(), BizConstants.REG_SOURCE_RETAIL)) {
                    recipelInfo.setChargeStatus(1);
                    recipelInfo.setIsPay("1");  //?是否付款是否前台传
                } else {
                    recipelInfo.setChargeStatus(0);
                    recipelInfo.setIsPay("0");
                }
                //保存处方主表
                RecipelInfo recipelInfoEntity = this.recipelInfoService.save(recipelInfo);

                List<RecipelDetail> recipelDetails = this.recipelDetailService.getByRecipelInfoId(recipelInfoEntity.getId());
                if (!CollectionUtils.isEmpty(recipelDetails)) {
                    this.recipelDetailService.batchDelete(recipelDetails);
                }
                for (RecipelDetail recipelDetail : recipelDetailEvtList) {
                    recipelDetail.setId(null);
                    recipelDetail.setRecipelInfo(recipelInfoEntity);
                    // 如果前端没有传minTotal（制剂单位总量），则用total作为默认值
                    if (recipelDetail.getMinTotal() == null || recipelDetail.getMinTotal() == 0) {
                        recipelDetail.setMinTotal(recipelDetail.getTotal());
                    }
                    if (recipelDetail.getInfuseGroup() != null) {
                        recipelDetail.setStuffType("2");
                    }
                    this.recipelDetailService.save(recipelDetail);
                }

                //TODO:动态库存操作
                this.medicinalStorageControlService.preOccupyStock(recipelInfoEntity);
            }
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
           throw new ServiceException("处方信息不能为空.");
        }
        String registrationId = entity.getRegistrationId();
        // 获取登记信息
        Registration registration = this.registrationService.get(registrationId);

        if (Objects.equals(registration.getStatus().getValue(), BizConstants.REG_STATUS_PENDING))
        {
            DictItem statusDictItem = new DictItem();
            statusDictItem.setValue(BizConstants.REG_STATUS_VISITED);
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
                throw new ServiceException("[" + recipelInfo.getName() + "]明细不能为空.");
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
            if (Objects.equals(registration.getSource().getValue(), BizConstants.REG_SOURCE_RETAIL)) {
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
            if (BizConstants.TREAT_TYPE_NORMAL.equals(registration.getTreatType().getValue())){
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
                    dictItem.setValue(recipelDetail.getStuffType().equals("2") ? BizConstants.MEDICAL_TYPE_WESTERN : BizConstants.MEDICAL_TYPE_PATENT);
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
            if (Objects.equals(registration.getSource().getValue(), BizConstants.REG_SOURCE_QUICK))
            {
                receptionEvt.setType(1);
            }
            else if (Objects.equals(registration.getSource().getValue(), BizConstants.REG_SOURCE_RETAIL))
            {
                receptionEvt.setType(2);
            }
            else {
                receptionEvt.setType(0);
            }

            receptionEvt.setId(registration.getId());
            receptionEvt.setRegistration(registration);

            List<Parameter> parameters = SearchParamsBuilder.create()
                    .eq("registration_id", registration.getId())
                    .build();
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
                // 批量查询所有处方明细（优化 N+1 查询）
                List<String> recipelInfoIdsList = recipelInfoList.stream()
                        .map(RecipelInfo::getId)
                        .collect(Collectors.toList());
                Map<String, List<RecipelDetail>> detailsMap = this.recipelDetailService.getByRecipelInfoIds(recipelInfoIdsList);

                for (int i = 0; i < recipelInfoList.size(); i++) {
                    RecipelInfoEvt recipelInfoEvt = new RecipelInfoEvt();
                    RecipelInfo recipelInfo = recipelInfoList.get(i);
                    recipelInfo.setRegistration(registration);

                    // 从批量查询结果中获取处方明细
                    List<RecipelDetail> recipelDetailList = detailsMap.getOrDefault(recipelInfo.getId(), Collections.emptyList());
                    if (!CollectionUtils.isEmpty(recipelDetailList))
                    {
                        for (RecipelDetail recipelDetail:recipelDetailList)
                        {
                            recipelDetail.setRecipelInfo(recipelInfo);
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
            PresDrug presDrug = presDrugService.getById(recipelDetail.getDrugStuffId().getDrugStuffId());
            if( recipelDetail.getRecipelInfo().getIsPre() != null &&
                    recipelDetail.getRecipelInfo().getIsPre() &&
                    presDrug != null ){
                //电子处方药品
                drugStuffEvt.setDrugStuffId(presDrug.getId());
                drugStuffEvt.setName(presDrug.getRegname());
                drugStuffEvt.setPrice(BigDecimal.valueOf(0));
                drugStuffEvt.setRetailPrice(BigDecimal.valueOf(0));
                drugStuffEvt.setIsUnpackSell("0");
                DictItem  dosisUnit = new DictItem();
                DictItem  PreparationUnit = new DictItem();
                DictItem  Pack = new DictItem();
                DictItem  type = new DictItem();
                ManufactureFactory factory = new ManufactureFactory();
                type.setValue(BizConstants.MEDICAL_TYPE_WESTERN);
                dosisUnit.setName("");
                dosisUnit.setValue("");
                PreparationUnit.setName(presDrug.getMinprepunt());
                PreparationUnit.setValue("");
                Pack.setName(presDrug.getMinpacunt());
                Pack.setValue("");
                factory.setName(presDrug.getPrdrname());
                drugStuffEvt.setDosisUnit(dosisUnit);
                drugStuffEvt.setPreparationUnit(PreparationUnit);
                drugStuffEvt.setPack(Pack);
                Drug drug  = new Drug();
                drug.setDosis(presDrug.getSpecname());
                drug.setDosisUnit(dosisUnit);
                drug.setPreparationUnit(PreparationUnit);
                drug.setPreparation(presDrug.getMinpaccnt());
                drug.setPack(Pack);
                drug.setFactory(factory);
                drug.setIsUnpackSell("0");
                drug.setType(type);
                drug.setGoodsName(presDrug.getRegname());
                drugStuffEvt.setDrug(drug);
            }else{
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
            }
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
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, order);
        List<RecipelInfoDTO> recipelInfos = recipelInfoService.getHistoryRecipel(pageRequest);
        if (CollectionUtils.isEmpty(recipelInfos)) {
            return recipelInfos;
        }

        // 批量查询所有处方明细（优化 N+1 查询）
        List<String> recipelInfoIds = recipelInfos.stream()
                .map(RecipelInfoDTO::getId)
                .collect(Collectors.toList());
        Map<String, List<RecipelDetail>> detailsMap = recipelDetailService.getByRecipelInfoIds(recipelInfoIds);

        // 批量获取所有 registrationId
        List<String> registrationIds = recipelInfos.stream()
                .map(r -> r.getRegistration().getId())
                .distinct()
                .collect(Collectors.toList());

        // 批量查询患者信息
        Map<String, Patient> patientMap = new HashMap<>();
        for (String regId : registrationIds) {
            Patient patient = patientService.getPatientByregistrationId(regId);
            if (patient != null) {
                patientMap.put(regId, patient);
            }
        }

        // 批量查询病历信息
        Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();
        for (String regId : registrationIds) {
            MedicalRecord medicalRecord = this.dao.getByRegistrationId(regId);
            if (medicalRecord != null) {
                medicalRecordMap.put(regId, medicalRecord);
            }
        }

        // 组装数据
        for (RecipelInfoDTO recipelInfoDTO : recipelInfos) {
            // 从批量查询结果中获取处方明细
            List<RecipelDetail> byRecipelInfoId = detailsMap.getOrDefault(recipelInfoDTO.getId(), Collections.emptyList());
            for (RecipelDetail recipelDetail : byRecipelInfoId) {
                recipelDetail.setDrugStuffId(this.getDrugStuffEvt(recipelDetail));
            }
            recipelInfoDTO.setRecipelDetail(byRecipelInfoId);

            // 设置患者信息
            String regId = recipelInfoDTO.getRegistration().getId();
            recipelInfoDTO.setPatient(patientMap.get(regId));

            // 设置病历信息
            recipelInfoDTO.setMedicalRecord(medicalRecordMap.get(regId));
        }
        return recipelInfos;
    }

    public List<Recordpatvo> recordpatlist(String id){
        Company company = SessionUtils.getUser().getCompany();
        return medicalRecordDao.recordpatlist(company.getId(),id);
    }


}