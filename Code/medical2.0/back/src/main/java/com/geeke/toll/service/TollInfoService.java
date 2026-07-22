package com.geeke.toll.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.geeke.admin.entity.User;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.constants.BizConstants;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.data.SearchParamsBuilder;
import com.geeke.common.sequence.service.SequenceService;
import com.geeke.common.service.CrudService;
import com.geeke.common.service.ServiceException;

import com.geeke.cure.entity.InspectionCheck;
import com.geeke.cure.entity.InspectionCheckDetail;
import com.geeke.cure.entity.InspectionCheckInfo;
import com.geeke.cure.service.InspectionCheckDetailService;
import com.geeke.cure.service.InspectionCheckInfoService;
import com.geeke.cure.service.InspectionCheckService;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.medicareutils.domain.respo.MdFeeDetail;
import com.geeke.medicareutils.service.MdInventoryService;
import com.geeke.medicareutils.service.MdPsnDataService;
import com.geeke.medicareutils.service.MdRegistrationService;
import com.geeke.member.entity.MemberManagement;
import com.geeke.member.service.MemberManagementDetailService;
import com.geeke.org.entity.ClinicOffice;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.outpatient.dao.RecipelDetailDao;
import com.geeke.outpatient.entity.*;
import com.geeke.outpatient.service.*;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.InventoryVerification;
import com.geeke.stock.entity.Stuff;
import com.geeke.stock.service.*;
import com.geeke.sys.entity.DictItem;
import com.geeke.toll.dao.TollInfoDao;
import com.geeke.toll.entity.*;
import com.geeke.toll.utils.BigdecimalConvert;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.entity.CostItemPackage;
import com.geeke.treatment.service.CostItemService;
import com.geeke.treatment.service.impl.CostItemPackageService;
import com.geeke.utils.*;

import lombok.RequiredArgsConstructor;
import com.geeke.utils.excel.ExcelExportBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.BeanUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 收费管理Service
 * @author lc
 * @version 2022-06-22
 */
 
@Service("tollInfoService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TollInfoService extends CrudService<TollInfoDao, TollInfo>{
    private final PatientService patientService;

    @Lazy
    private final RecipelInfoService recipelInfoService;

    private final SequenceService sequenceService;

    @Lazy
    private final RegistrationService registrationService;

    private final TollDetailService tollDetailService;

    @Lazy
    private final MedicalRecordService medicalRecordService;

    private final RecipelDetailService recipelDetailService;

    private final StuffService stuffService;

    private final DrugService drugService;

    private final TollInfoDao tollInfoDao;

    private final CostItemService costItemService;

    private final InspectionCheckInfoService inspectionCheckInfoService;

    private final InspectionCheckDetailService inspectionCheckDetailService;

    private final InspectionCheckService inspectionCheckService;

    private final CostItemPackageService costItemPackageService;

    private final MemberManagementDetailService memberManagementDetailService;

    private final InventoryVerificationService inventoryVerificationService;

    private final SupplierStockService supplierStockService;

    private final DispensingService dispensingService;

    private final MedicinalStorageControlService medicinalStorageControlService;

    private final SerialNoUtils serialNoUtils;

    private final CompanyService companyService;

    private final RecipelDetailDao recipelDetailDao;

    private final MedicareConfigProperties medicareConfigProperties;

    private final PatientMdDataService patientMdDataService;

    private final MdRegistrationService mdRegistrationService;

    private final MdInventoryService mdInventoryService;

    private final MdPsnDataService mdPsnDataService;

    /**
     * 将 SearchParams 转换为 PageRequest
     * 简化报表查询方法中的重复代码
     */
    private PageRequest toPageRequest(SearchParams searchParams) {
        return new PageRequest(searchParams.getOffset(), searchParams.getLimit(),
                searchParams.getParams(), searchParams.getOrderby());
    }

    @Override
    @Transactional(readOnly = false)
    public TollInfo save(TollInfo tollInfo) {
        // 新增时, 处理自动编号字段
        tollInfo.setCreateDate(new Date());
        if (StringUtils.isBlank(tollInfo.getTollNumber())){
            String sn = sequenceService.generate(SessionUtils.getUser().getCompanyId(), "toll_code", tollInfo);
            tollInfo.setTollNumber(sn);
        }

        TollInfo tollInfoTemp = super.save(tollInfo);
        return tollInfoTemp;
    }
    

    @Transactional(readOnly = false)
    public void saveToll(TollEvt tollEvt,String type) {
        Company company = SessionUtils.getUser().getCompany();
        String seltId="";//医保结算id付费后存入挂号表
        //正在进行盘点时不能进行收费/退费
        validateInventoryNotInProgress(company.getId(), tollEvt.getTollInfo().getAmountStatus().getValue());
        TollInfo tollInfo = tollEvt.getTollInfo();
        List<RecipelInfoEvt> recipelInfoEvts = tollEvt.getRecipelInfos();
        TollInfo tollSave = null;
        Registration registration = null;
        if (!CollectionUtils.isEmpty(recipelInfoEvts)) {
            //校验重复提交
            List<String> InRecipeIds = recipelInfoEvts.stream().map(recipelInfoEvt -> {
                return recipelInfoEvt.getRecipelInfo().getId();
            }).collect(Collectors.toList());
            List<TollInfo> inRecipeIds = this.getInRecipeIds(InRecipeIds,tollInfo.getAmountStatus().getValue());
            if (!CollectionUtils.isEmpty(inRecipeIds)) {
                throw new ServiceException("数据已存在请勿重复提交");
            }
            Patient patient = tollEvt.getTollInfo().getPatient();
            if (StringUtils.isNotBlank(patient.getId())) {
                patient = patientService.get(patient.getId());
            }
            //收费
            if (BizConstants.AMOUNT_STATUS_PAID.equals(tollInfo.getAmountStatus().getValue())) {
                if (BizConstants.TOLL_TYPE_REGISTRATION.equals(tollInfo.getTollType().getValue())){
                    //零售收费
                    registration = processRetailCharge(tollEvt, tollInfo, patient, recipelInfoEvts);
                }else {
                    registration = processNormalCharge(tollEvt, tollInfo, patient, recipelInfoEvts);
                }
            }
            for (RecipelInfoEvt recipelInfo:tollEvt.getRecipelInfos()){
                List<RecipelDetail> entity = recipelDetailService.getRecipelDetail(recipelInfo.getRecipelInfo().getId());
                for (RecipelDetail e:entity){
                    BigDecimal c = BigDecimal.valueOf(tollEvt.getTollInfo().getDiscount());
                    e.setActualPayment(e.getAllFee().multiply(c));
                    recipelDetailDao.updateActualPayment(e.getActualPayment(),e.getId());
                }
            }
            //退费
            if (BizConstants.AMOUNT_STATUS_REFUNDED.equals(tollInfo.getAmountStatus().getValue())) {
                processRefund(tollEvt, tollInfo, recipelInfoEvts);
            }
            //挂号支付方式，收费状态
            if (null == registration) {
                registration = registrationService.get(tollInfo.getMedical().getRegistration().getId());

            }
            //支付方式共用
            if (StringUtils.isNullOrEmpty(registration.getPayType())) {
                registration.setPayType(tollInfo.getPaymentType());
            }
            //没有申明方式 或零售 就默认是整个挂号单
            if(StringUtils.isNullOrEmpty(type) || BizConstants.TOLL_TYPE_INFUSION.equals(tollInfo.getTollType().getValue()))
            {
                registration.setChargeStatus(tollEvt.getChargeStatus());
            }
            //否则只是单个处方
            if (BizConstants.AMOUNT_STATUS_PAID.equals(tollInfo.getAmountStatus().getValue())) {
                registration.setChargeDate(new Date());
            }else if (BizConstants.AMOUNT_STATUS_REFUNDED.equals(tollInfo.getAmountStatus().getValue())) {
                registration.setretreatsDate(new Date());
            }
            registration.setSetlId(seltId);
            registrationService.save(registration);
        }
    }

    //诊疗项目退费，如果存在材料收费则需退还
    private void returnPremium(RecipelInfo recipelInfo) {
        medicinalStorageControlService.materialRefund(recipelInfo);
        //修改发药信息表
        dispensingService.updateDelFlag(recipelInfo.getId());
    }

    //诊疗项目进行附加费材料扣减
    private void deduction(RecipelInfo recipelInfo) {
        medicinalStorageControlService.okOccupyStock(recipelInfo.getRegistration().getId(),recipelInfo.getId());
    }

    /**
     * 校验盘点状态：盘点中禁止收费/退费操作
     */
    private void validateInventoryNotInProgress(String companyId, String amountStatus) {
        List<InventoryVerification> inventoryVerifications = inventoryVerificationService.getByCompanyId(companyId);
        if (!CollectionUtils.isEmpty(inventoryVerifications)) {
            String msg = BizConstants.AMOUNT_STATUS_PAID.equals(amountStatus)
                    ? "正在进行盘点，无法进行收费操作!" : "正在进行盘点，无法进行退费操作!";
            throw new ServiceException(msg);
        }
    }

    /**
     * 处理零售收费：创建患者、挂号、病例、处方，预占库存
     */
    private Registration processRetailCharge(TollEvt tollEvt, TollInfo tollInfo, Patient patient, List<RecipelInfoEvt> recipelInfoEvts) {
        Patient patient1 = patientService.get(patient.getId());
        if (ObjectUtils.isEmpty(patient1)) {
            patient = patientService.save(patient);
        } else {
            patient = patient1;
        }
        Registration registration = this.addRetailRegistration(patient.getCompany(), patient);
        MedicalRecord medicalRecord = this.addMedicalRecord(registration);
        for (RecipelInfoEvt recipelInfoEvt : recipelInfoEvts) {
            RecipelInfo recipelInfo = recipelInfoEvt.getRecipelInfo();
            recipelInfo.setIsDispension("0");
            recipelInfo.setIsPay("1");
            recipelInfo.setPayDate(new Date());
            recipelInfo.setChargeStatus(1);
        }
        RecipelInfo retail = this.addRetailRecipelInfo(recipelInfoEvts, medicalRecord);
        tollInfo.setPatient(patient);
        tollInfo.setRecipel(retail);
        tollInfo.setMedical(medicalRecord);
        this.save(tollInfo);
        medicinalStorageControlService.preOccupyStock(retail);
        if ("true".equals(medicareConfigProperties.getCheck())) {
            mdInventoryService.updateInventoryList_3502A(tollEvt.getRecipelInfos().get(0));
        }
        return registration;
    }

    /**
     * 处理正常收费（非零售）：保存处方、生成收费记录、医保上传、体验卡扣减、检验检查
     */
    private Registration processNormalCharge(TollEvt tollEvt, TollInfo tollInfo, Patient patient, List<RecipelInfoEvt> recipelInfoEvts) {
        Registration registration = null;
        tollInfo.setPatient(patient);
        JSONArray array = new JSONArray();
        for (RecipelInfoEvt recipelInfoEvt : recipelInfoEvts) {
            RecipelInfo recipelInfo = recipelInfoService.get(recipelInfoEvt.getRecipelInfo().getId());
            String value = tollInfo.getAmountStatus().getValue();
            recipelInfo.setIsPay(value.substring(value.length() - 1));
            recipelInfo.setPayDate(new Date());
            recipelInfo.setIsDispension("0");
            recipelInfo.setChargeStatus(1);
            recipelInfo.setAmountReceivedTotal(tollEvt.getTollInfo().getAmountReceived());
            recipelInfoService.save(recipelInfo);
            TollInfo newTollInfo = new TollInfo();
            BeanUtils.copyProperties(tollInfo, newTollInfo);
            newTollInfo.setMedical(medicalRecordService.get(tollInfo.getMedical().getId()));
            newTollInfo.setRecipel(recipelInfo);
            newTollInfo.setAmountReceivable(recipelInfo.getFee());
            newTollInfo.setAmountReceived(tollInfo.getAmountReceived());
            newTollInfo.setAmountDiscounted(newTollInfo.getAmountReceivable().subtract(newTollInfo.getAmountReceived()));
            // 根据处方名称映射收费类型
            String tollTypeName = recipelInfo.getName().substring(0, 2);
            DictItem tollType = newTollInfo.getTollType();
            tollType.setValue(getTollTypeByTypeName(tollTypeName));
            if ("诊疗".equals(tollTypeName) && newTollInfo.getAmountReceivable() != null && newTollInfo.getAmountReceived() != null && newTollInfo.getAmountReceivable().compareTo(newTollInfo.getAmountReceived()) != 0) {
                newTollInfo.setRemarks("体验卡扣减");
            }
            newTollInfo.setTollType(tollType);
            this.save(newTollInfo);
            // 医保收费信息上传
            if ("true".equals(medicareConfigProperties.getCheck())) {
                registration = buildAndUploadMedicareData(tollInfo, newTollInfo, recipelInfoEvt, array);
            }
            // 体验卡扣减
            MemberManagement memberManagement = tollEvt.getMemberManagement();
            if (!ObjectUtils.isEmpty(memberManagement) && "诊疗".equals(tollTypeName) && !CollectionUtils.isEmpty(memberManagement.getMemberManagementDetails())) {
                memberManagementDetailService.updateUseNumber(memberManagement);
            }
            // 检验检查项目入库
            if ("诊疗".equals(tollTypeName)) {
                addInspectionCheck(recipelInfo);
                deduction(recipelInfo);
            }
        }
        if (medicareConfigProperties.getCheck().equals("true") && registration != null) {
            mdRegistrationService.upRegistrationInfo_2203(registration);
        }
        return registration;
    }

    /**
     * 根据处方名称前两个字映射收费类型值
     */
    private String getTollTypeByTypeName(String typeName) {
        switch (typeName) {
            case "西药": return BizConstants.TOLL_TYPE_WESTERN;
            case "中药": return BizConstants.TOLL_TYPE_CHINESE;
            case "输液": return BizConstants.TOLL_TYPE_INFUSION;
            case "诊疗": return BizConstants.TOLL_TYPE_TREATMENT;
            default: return "";
        }
    }

    /**
     * 构建医保收费数据并上传
     */
    private Registration buildAndUploadMedicareData(TollInfo tollInfo, TollInfo tollSave, RecipelInfoEvt recipelInfoEvt, JSONArray array) {
        Registration registration = registrationService.get(tollInfo.getMedical().getRegistration().getId());
        PatientMdData patientMdData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
        ClinicOffice clinicOffice = registration.getClinicOffice();
        User doctor = registration.getDoctor();
        JSONObject data = new JSONObject();
        data.put("feedetl_sn", tollSave.getId());
        data.put("mdtrt_id", registration.getMdtrtId());
        data.put("psn_no", patientMdData.getPsnNo());
        data.put("chrg_bchno", tollSave.getTollNumber());
        if (Objects.nonNull(tollSave.getRecipel())) {
            data.put("rxno", tollSave.getRecipel().getCode());
            data.put("rxd_circ_flag", "0");
        }
        data.put("fee_ocur_time", tollSave.getRecipel().getCreateDate());
        data.put("med_list_codg", "");
        data.put("medins_list_codg", "");
        data.put("det_item_fee_sumamt", recipelInfoEvt.getRecipelInfo().getFee());
        data.put("cnt", recipelInfoEvt.getRecipelDetailEvtList().get(0).getTotal());
        data.put("pric", recipelInfoEvt.getRecipelDetailEvtList().get(0).getUnitPrice());
        data.put("sin_dos_dscr", "");
        data.put("used_frqu_dscr", "");
        data.put("prd_days", recipelInfoEvt.getRecipelDetailEvtList().get(0).getDays().getValue());
        data.put("medc_way_dscr", "");
        data.put("bilg_dept_codg", clinicOffice.getCode());
        data.put("bilg_dept_name", clinicOffice.getName());
        data.put("bilg_dr_codg", doctor.getUserExt().getPracPsnCode());
        data.put("bilg_dr_name", doctor.getName());
        data.put("acord_dept_codg", clinicOffice.getCode());
        data.put("acord_dept_name", clinicOffice.getName());
        data.put("orders_dr_code", doctor.getUserExt().getPracPsnCode());
        data.put("orders_dr_name", doctor.getName());
        data.put("hosp_appr_flag", "1");
        data.put("tcmdrug_used_way", "");
        data.put("etip_flag", "");
        data.put("etip_hosp_code", "");
        data.put("dscg_tkdrug_flag", "");
        data.put("matn_fee_flag", "");
        data.put("comb_no", "");
        data.put("expContent", "");
        array.add(data);
        return registration;
    }

    /**
     * 处理退费逻辑：校验、更新处方状态、生成退费记录、退还库存、医保退费
     */
    private void processRefund(TollEvt tollEvt, TollInfo tollInfo, List<RecipelInfoEvt> recipelInfoEvts) {
        for (RecipelInfoEvt recipelInfoEvt : recipelInfoEvts) {
            RecipelInfo recipelInfo = recipelInfoService.get(recipelInfoEvt.getRecipelInfo().getId());
            String value = tollInfo.getAmountStatus().getValue();
            // 校验是否已发药
            if ("1".equals(recipelInfo.getIsDispension())) {
                throw new ServiceException("该用户已发药，无法退费！");
            }
            // 诊疗处方退费校验
            if (BizConstants.RECIPEL_TYPE_OTHER.equals(recipelInfo.getRecipelType().getValue())) {
                validateRefundForTreatmentRecipel(recipelInfo);
            }
            // 更新处方状态
            recipelInfo.setIsPay(value.substring(value.length() - 1));
            recipelInfo.setChargeStatus(-1);
            recipelInfo.setRetreatDate(new Date());
            recipelInfoService.save(recipelInfo);
            // 生成退费记录
            List<TollInfo> tollInfos = this.getByRecipeId(recipelInfo.getId());
            for (TollInfo info : tollInfos) {
                TollInfo refund = new TollInfo();
                TollInfo tollInfo1 = new TollInfo();
                BeanUtils.copyProperties(info, refund);
                BeanUtils.copyProperties(info, tollInfo1);
                refund.setId("");
                tollInfo1.setReturnType(1);
                refund.setAmountStatus(tollInfo.getAmountStatus());
                refund.setReturnType(1);
                this.save(refund);
                this.save(tollInfo1);
            }
            // 退还材料
            if (BizConstants.RECIPEL_TYPE_OTHER.equals(recipelInfo.getRecipelType().getValue())) {
                returnPremium(recipelInfo);
            }
            // 退药退材料操作
            if (recipelInfo.getDispensionStatus() == 0 && !BizConstants.RECIPEL_TYPE_OTHER.equals(recipelInfo.getRecipelType().getValue())) {
                medicinalStorageControlService.cancelOccupy(recipelInfo);
            } else if (recipelInfo.getDispensionStatus() == -1 && !BizConstants.RECIPEL_TYPE_OTHER.equals(recipelInfo.getRecipelType().getValue())) {
                medicinalStorageControlService.goBackFee(recipelInfo);
            }
        }
        // 医保退费
        if ("true".equals(medicareConfigProperties.getCheck())) {
            Registration registration = registrationService.get(tollInfo.getMedical().getRegistration().getId());
            mdPsnDataService.getAndSetPsnData(registration);
            PatientMdData psnData = patientMdDataService.getOne(new LambdaQueryWrapper<PatientMdData>().eq(PatientMdData::getPatientId, registration.getPatientId().getId()));
            mdRegistrationService.revokeOutpatientSettlement_2208(registration.getSetlId(), psnData.getPsnNo(), registration.getMdtrtId());
        }
    }

    /**
     * 诊疗处方退费校验：检查执行状态和检验检查报告
     */
    private void validateRefundForTreatmentRecipel(RecipelInfo recipelInfo) {
        List<RecipelDetail> recipelDetails = recipelDetailService.getByRecipelInfoId(recipelInfo.getId());
        for (RecipelDetail recipelDetail : recipelDetails) {
            if (recipelDetail.getExecutions().compareTo(BigDecimal.valueOf(0)) != 0) {
                throw new ServiceException("诊疗项目已执行划扣，无法退费！");
            }
        }
        List<InspectionCheck> inspectionChecks = inspectionCheckService.getByRecipelInfoId(recipelInfo.getId());
        for (InspectionCheck inspectionCheck : inspectionChecks) {
            if ("1".equals(inspectionCheck.getStatus())) {
                throw new ServiceException("检验检查报告已填写，无法退费！");
            }
        }
        inspectionCheckService.deleteByRecipelInfoId(recipelInfo.getId());
    }

    //保存检验检测
    @Transactional(readOnly = false)
    public void addInspectionCheck(RecipelInfo recipelInfo) {
        //通过处方id，去获取诊疗项目
        List<CostItem> costItems = costItemService.getByRecipelInfo(recipelInfo);
        //判断诊疗项目是否为检验检查
        for (CostItem costItem : costItems) {
            String itemType = costItem.getItemType().getValue();
            if (BizConstants.TREATMENT_ITEM_TYPE_LAB.equals(itemType) || BizConstants.TREATMENT_ITEM_TYPE_EXAM.equals(itemType)) {
                // 0-检验 1-检查
                String type = BizConstants.TREATMENT_ITEM_TYPE_LAB.equals(itemType) ? "0" : "1";
                saveInspectionCheck(recipelInfo, costItem, type);
            }
        }
    }

    /**
     * 保存检验检查记录（合并检验和检查的公共逻辑）
     */
    private void saveInspectionCheck(RecipelInfo recipelInfo, CostItem costItem, String type) {
        //根据登记信息获取患者信息
        Registration registration = registrationService.get(recipelInfo.getRegistration().getId());
        Patient patient = patientService.get(registration.getPatientId().getId());

        InspectionCheck inspectionCheck = new InspectionCheck();
        inspectionCheck.setRecipelInfo(recipelInfo);
        inspectionCheck.setCompany(recipelInfo.getCompany());
        inspectionCheck.setCostItem(costItem);
        inspectionCheck.setName(costItem.getItemName());
        inspectionCheck.setStatus("0");
        inspectionCheck.setType(type);
        inspectionCheck.setRegistration(recipelInfo.getRegistration());
        inspectionCheck.setPatient(patient);
        inspectionCheck.setPatientName(patient.getName());
        inspectionCheck.setSex(patient.getGender().getValue());
        inspectionCheck.setPhone(patient.getPhone());
        inspectionCheck.setCompleteBy(registration.getDoctor().getName());
        inspectionCheck.setCompleteDate(registration.getReceptionEndDate());
        inspectionCheck.setId(IdGen.uuid());

        // 检验类型需要设置 recipelDetail
        if ("0".equals(type)) {
            inspectionCheck.setRecipelDetail(costItem.getRecipelDetail());
        }

        inspectionCheckService.allSave(inspectionCheck);

        //插入主表后，需要插入到明细和详情表中
        InspectionCheckInfo inspectionCheckInfo = new InspectionCheckInfo();
        inspectionCheckInfo.setId(IdGen.uuid());
        inspectionCheckInfo.setCompany(recipelInfo.getCompany());
        inspectionCheckInfo.setPatient(patient);
        inspectionCheckInfo.setInspectionCheck(inspectionCheck);
        JSONObject userObj = com.geeke.sys.utils.SessionUtils.getUserJson();
        inspectionCheckInfo.setCreateBy(userObj.getString("name"));
        inspectionCheckInfo.setUpdateBy(userObj.getString("name"));
        inspectionCheckInfo.setCreateDate(new Date());
        inspectionCheckInfo.setUpdateDate(new Date());
        inspectionCheckInfoService.allSave(inspectionCheckInfo);

        //插入详情表
        List<CostItemPackage> all = costItemPackageService.getAll(costItem.getId());
        if (!CollectionUtils.isEmpty(all)) {
            int seq = 0;
            for (CostItemPackage costItemPackage : all) {
                seq++;
                InspectionCheckDetail inspectionCheckDetail = new InspectionCheckDetail();
                inspectionCheckDetail.setId(IdGen.uuid());
                inspectionCheckDetail.setCompany(recipelInfo.getCompany());
                inspectionCheckDetail.setInspectionCheckInfo(inspectionCheckInfo);
                inspectionCheckDetail.setSeq(seq);
                CostItem costItem1 = new CostItem();
                String costItemId = costItemPackage.getCostItemId();
                if (costItemId != null) {
                    costItem1.setId(costItemId);
                } else {
                    costItem1.setId(costItemPackage.getCostItemPkgId());
                }
                inspectionCheckDetail.setCostItem(costItem1);
                inspectionCheckDetail.setCreateBy(userObj.getString("name"));
                inspectionCheckDetail.setUpdateBy(userObj.getString("name"));
                inspectionCheckDetail.setCreateDate(new Date());
                inspectionCheckDetail.setUpdateDate(new Date());
                inspectionCheckDetailService.allSave(inspectionCheckDetail);
            }
        }
    }

    private MedicalRecord addMedicalRecord(Registration registration) {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setCompany(registration.getCompany());
        medicalRecord.setRegistration(registration);
        //这个字段必填
        medicalRecord.setDoctor(SessionUtils.getUser());
        medicalRecord.setPatientTell("零售挂号");
        medicalRecord.setDiagnose("零售挂号");
        return medicalRecordService.save(medicalRecord);
    }

    private List<Patient> getPatientByNameAndPhone(Patient patient) {
        Company company = SessionUtils.getUser().getCompany();
        List<Parameter> parameters = SearchParamsBuilder.create()
                .eq("name", patient.getName())
                .eq("company_id", company.getId())
                .eq("phone", patient.getPhone())
                .build();
        return patientService.listAll(parameters,"");
    }


    private BigDecimal getAmountReceived(RecipelInfo recipelInfo, TollInfo tollInfo){
        if (tollInfo.getDiscount() == 0) {
            return recipelInfo.getFee();
        }
        return recipelInfo.getFee().multiply(new BigDecimal(tollInfo.getDiscount()));
    }

    private Registration addRetailRegistration(Company company,Patient patient) {
            Registration registration = new Registration();
            registration.setCompany(company);
            registration.setChargeDate(new Date());
            registration.setPatientId(patient);
            registration.setDispensingStatus("0");
            DictItem dictItem = new DictItem();
            //已就诊
            dictItem.setValue(BizConstants.REG_STATUS_VISITED);
            registration.setStatus(dictItem);
            //已收费
            registration.setChargeStatus("2");
            return registrationService.save(registration);
    }

    private RecipelInfo addRetailRecipelInfo(List<RecipelInfoEvt> recipelInfos,MedicalRecord medicalRecord) {
        RecipelInfo save = null;
        DictItem dictItem = new DictItem();
        dictItem.setValue(BizConstants.RECIPEL_TYPE_EXTERNAL);
        //零售处方默认只有一个处方
        for (RecipelInfoEvt recipelInfoEvt : recipelInfos) {
            RecipelInfo recipelInfo = recipelInfoEvt.getRecipelInfo();
            recipelInfo.setRecipelType(dictItem);
            //recipelInfo.setMedicalRecord(medicalRecord);
            recipelInfo.setRegistration(medicalRecord.getRegistration());
            recipelInfo.setIsFollowUp(0);
            String recipelNo = serialNoUtils.generateSerialNo(org.apache.commons.lang3.StringUtils.EMPTY);
            recipelInfo.setCode(recipelNo);
            save = recipelInfoService.save(recipelInfo);
            //添加处方详情
            for (RecipelDetail recipelDetail : recipelInfoEvt.getRecipelDetailEvtList()) {
                recipelDetail.setRecipelInfo(recipelInfo);
                recipelDetail.setCompany(recipelInfo.getCompany());
                //目前只考虑药品
                DrugStuffEvt drugStuffEvt = new DrugStuffEvt();
                if(Objects.equals("4",recipelDetail.getStuffType())){
                    Stuff stuff = stuffService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    drugStuffEvt.setDrugStuffId(stuff.getId());
                    drugStuffEvt.setName(stuff.getName());
                    drugStuffEvt.setPrice(stuff.getPriceOutSell());
                    drugStuffEvt.setPreparationUnit(stuff.getMinUnit());
                    drugStuffEvt.setPack(stuff.getPackUnit());
                    drugStuffEvt.setStuff(stuff);
                }else {
                    Drug drug = drugService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
                    drugStuffEvt.setDrugStuffId(drug.getId());
                    drugStuffEvt.setName(drug.getGoodsName());
                    drugStuffEvt.setPrice(drug.getPrice());
                    drugStuffEvt.setDosisUnit(drug.getDosisUnit());
                    drugStuffEvt.setPreparationUnit(drug.getPreparationUnit());
                    drugStuffEvt.setPack(drug.getPack());
                    drugStuffEvt.setDrug(drug);
                }
                recipelDetail.setDrugStuffId(drugStuffEvt);
                recipelDetail.setId("");
                recipelDetailService.save(recipelDetail);
            }
        }
        return save;
    }

    public List<TollInfo> getInRecipeIds(List<String> recipelInfoIds,String amountStatus){
        List<Parameter> parameters = SearchParamsBuilder.create()
                .in("recipel_id", recipelInfoIds)
                .eq("amount_status", amountStatus)
                .build();
        return this.listAll(parameters,"");
    }

    public List<TollInfo> getByRecipeId(String recipelInfoId){
        List<Parameter> parameters = SearchParamsBuilder.create()
                .eq("recipel_id", recipelInfoId)
                .build();
        return this.listAll(parameters,"");
    }

    public List<TollInfo> getByMedicalId(String medicalId) {
        List<Parameter> parameters = SearchParamsBuilder.create()
                .eq("medical_id", medicalId)
                .build();
        return this.listAll(parameters, "");
    }

    public ResponseEntity<JSONObject> tollTotalForm(SearchParams searchParams) {
        // 入参columnName格式：recipelInfo.recipel_type|costItem.item_type
        PageRequest pageRequest = toPageRequest(searchParams);
        Page<TollInfo> page = paginate(
            () -> this.dao.formTollCount(pageRequest),
            () -> this.dao.tollTotalForm(pageRequest)
        );
        TollVo tollVo = this.getTotalTollForSearch(pageRequest);
        tollVo.setPage(page);
        return ResponseEntity.ok(ResultUtil.successJson(tollVo));
    }

    public ResponseEntity<JSONObject> orgtolldetail(SearchParams searchParams) {
        // 入参columnName格式：recipelInfo.recipel_type|costItem.item_type
        PageRequest pageRequest = toPageRequest(searchParams);
        Page<TollInfo> page = paginate(
            () -> this.dao.formTollCount(pageRequest),
            () -> this.dao.tollTotalForm(pageRequest)
        );
        TollVo tollVo = this.getTotalTollForSearch(pageRequest);
        tollVo.setPage(page);
        return ResponseEntity.ok(ResultUtil.successJson(tollVo));
    }

    public ResponseEntity<JSONObject> tollDetailForm(SearchParams searchParams) {
        // 入参columnName格式：toll_type|patient_id|department.id|user.id|recipelInfo.recipel_type in ["_0","_1"]
        PageRequest pageRequest = toPageRequest(searchParams);
        Page<TollInfo> page = paginate(
            () -> this.dao.formDetailCount(pageRequest),
            () -> {
                List<TollInfo> list = this.dao.tollDetailForm(pageRequest);
                for (TollInfo i : list) {
                    if (i.getDoctor() != null && i.getDoctor().getDepartment() != null) {
                        i.getDoctor().getDepartment().setName(i.getDepartment());
                    }
                }
                return list;
            }
        );
        TollVo tollVo = this.dao.tollDetailAmountReceivedAble(pageRequest);
        tollVo.setPage(page);
        return ResponseEntity.ok(ResultUtil.successJson(tollVo));
    }


    public ResponseEntity<JSONObject> orgtollDetailForm(SearchParams searchParams) {
        // 入参columnName格式：toll_type|patient_id|department.id|user.id|recipelInfo.recipel_type in ["_0","_1"]
        PageRequest pageRequest = toPageRequest(searchParams);
        Page<TollInfo> page = paginate(
            () -> this.dao.formDetailCount(pageRequest),
            () -> this.dao.tollDetailForm(pageRequest)
        );
        TollVo tollVo = this.dao.tollDetailAmountReceivedAble(pageRequest);
        tollVo.setPage(page);
        return ResponseEntity.ok(ResultUtil.successJson(tollVo));
    }

    //获取查询总金额（优化：合并为一次查询）
    private TollVo getTotalTollForSearch(PageRequest pageRequest) {
        return this.dao.tollTotalAllPayments(pageRequest);
    }

    public TollInfo getTollInfoByRegistrationId(String registrationId) {
        return tollInfoDao.getTollInfoByRegistrationId(registrationId);
    }

    public List<TollInfo> getCreateBy(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);
        return this.dao.getCreateBy(pageRequest);
    }

    public Page<WorkLoad> getWorkload(List<Parameter> params,int offset,int limit, String orderby) {
        List<Parameter> parameters = new ArrayList<>();
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        List<WorkLoad> count = this.dao.countWorkload(pageRequest);
        int total=0;
        if(count!=null&&count.size()>0){
            total=count.size();
        }
        List<WorkLoad> workloads=null;
        if(total>0){
            workloads = this.dao.getWorkload(pageRequest);
            List<WorkLoad> workLoads = this.dao.getCount(pageRequest);

            // 设置就诊次数
            for (WorkLoad workLoad: workloads) {
                for (WorkLoad workload: workLoads){
                    if (workLoad.getName().equals(workload.getName())){
                        workLoad.setCount(workload.getCount());
                    }
                }
            }

            // 批量查询所有医生的临时费用（优化 N+1 查询）
            List<String> doctorNames = workloads.stream()
                    .map(WorkLoad::getName)
                    .distinct()
                    .collect(Collectors.toList());
            PageRequest batchPageRequest = new PageRequest(params, orderby);
            List<WorkLoad> allTemporaryCosts = this.dao.getTemporaryCostBatch(batchPageRequest, doctorNames);

            // 按医生名称分组
            Map<String, List<WorkLoad>> costsByDoctor = allTemporaryCosts.stream()
                    .collect(Collectors.groupingBy(WorkLoad::getName));

            // 设置临时费用
            for (WorkLoad workLoad: workloads) {
                List<WorkLoad> temporaryCosts = costsByDoctor.getOrDefault(workLoad.getName(), Collections.emptyList());
                for (WorkLoad temporaryCost : temporaryCosts) {
                    setCostByTypeName(workLoad, temporaryCost.getTypes(), temporaryCost.getTemporaryCost());
                }
            }
        }

        return new Page<>((long)total, workloads);
    }

    public WorkLoadStat getWorkLoadStat(List<Parameter> params,int offset,int limit, String orderby){
        List<Parameter> parameters = new ArrayList<>();
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        WorkLoadStat workLoadStat = this.dao.getWorkLoadStat(pageRequest);
        WorkLoadStat workLoads = null;
        workLoads = this.dao.getCounts(pageRequest);
        if (workLoads != null && workLoads.getCount() != null && !workLoads.getCount().equals("0")){
            workLoadStat.setCount(workLoads.getCount());
        }
        if(workLoadStat!=null){
            parameters.clear();
            parameters.addAll(params);
            PageRequest pageRequest1 = new PageRequest(parameters, orderby);
            List<WorkLoadStat> temporaryCostStat = this.dao.getTemporaryCostStat(pageRequest1);
            if (temporaryCostStat != null && temporaryCostStat.size() > 0) {
                for (WorkLoadStat workLoadStat1 : temporaryCostStat) {
                    setCostByTypeName(workLoadStat, workLoadStat1.getTypes(), workLoadStat1.getTemporaryCost());
                }
            }
        }
        return workLoadStat;
    }

    public Page<DrugSales> getDrugSales(List<Parameter> params, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        List<DrugSales> count = this.dao.countDrugSales(pageRequest);
        int total=0;
        if(count!=null&&count.size()>0){
            total=count.size();
        }
        List<DrugSales> drugSales=null;
        if(total>0){
            drugSales=this.dao.getDrugSales(pageRequest);
        }
        return new Page<>(total,drugSales);
    }

    public DrugSales getDrugSalesStat(List<Parameter> params, int offset, int limit, String orderby){
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        DrugSales drugSalesStat = this.dao.getDrugSalesStat(pageRequest);
        return drugSalesStat;
    }

    /**
     * 药品进销存信息
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getypjxcmanagement(YpjxcRc ypjxcRc){
        String institution = companyService.getInstitution(ypjxcRc.getCompanyId());
        ypjxcRc.setJgid(institution);
        int total = tollInfoDao.countYpjxcManagement(ypjxcRc);
        List<Ypjxc> list2 = null;
        if(total > 0){
            list2 = tollInfoDao.getypjxcmanagement(ypjxcRc);
        }
        return new Page<>((long) total, list2);
    }

    /**
     * 药品进销存汇总数量和价格
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getypjxcmanagementsums(YpjxcRc ypjxcRc){
        String institution = companyService.getInstitution(ypjxcRc.getCompanyId());
        ypjxcRc.setJgid(institution);
        int total = tollInfoDao.countYpjxcManagement(ypjxcRc);
        List<Ypjxc> list2 = null;
        if(total > 0){
            list2 = tollInfoDao.getypjxcmanagementsums(ypjxcRc);
        }
        return new Page<>((long) total, list2);
    }

    /**
     * 获取耗材销售情况
     * @param stuffsalessummaryRc
     * @return
     */
    public Page<Stuffsalessummary> getStuffsalessummarylists(StuffsalessummaryRc stuffsalessummaryRc){
        String institution = companyService.getInstitution(stuffsalessummaryRc.getCompanyId());
        stuffsalessummaryRc.setJgid(institution);
        int total = tollInfoDao.countStuffSalesSummary(stuffsalessummaryRc);
        List<Stuffsalessummary> list2 = null;
        if(total > 0){
            list2 = tollInfoDao.getStuffsalessummarylist(stuffsalessummaryRc);
        }
        return new Page<>((long) total, list2);
    }

    public Page<Stuffsalessummary> getStuffsalessummarysumss(StuffsalessummaryRc stuffsalessummaryRc){
        String institution = companyService.getInstitution(stuffsalessummaryRc.getCompanyId());
        stuffsalessummaryRc.setJgid(institution);
        int total = tollInfoDao.countStuffSalesSummary(stuffsalessummaryRc);
        List<Stuffsalessummary> list2 = null;
        if(total > 0){
            list2 = tollInfoDao.getStuffsalessummarysums(stuffsalessummaryRc);
        }
        return new Page<>((long) total, list2);
    }

    /**
     * 材料进销存信息
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getcljxcmanagement(YpjxcRc ypjxcRc){
        String institution = companyService.getInstitution(ypjxcRc.getCompanyId());
        ypjxcRc.setJgid(institution);
        int total = tollInfoDao.countCljxcManagement(ypjxcRc);
        List<Ypjxc> list2 = null;
        if(total > 0){
            list2 = tollInfoDao.getcljxcmanagement(ypjxcRc);
        }
        return new Page<>((long) total, list2);
    }

    /**
     * 材料进销存汇总数量和价格
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getcljxcmanagementsums(YpjxcRc ypjxcRc){
        String institution = companyService.getInstitution(ypjxcRc.getCompanyId());
        ypjxcRc.setJgid(institution);
        int total = tollInfoDao.countCljxcManagement(ypjxcRc);
        List<Ypjxc> list2 = null;
        if(total > 0){
            list2 = tollInfoDao.getcljxcmanagementsums(ypjxcRc);
        }
        return new Page<>((long) total, list2);
    }

    /**
     * 药品材料入库信息统计
     * @param ypjxcRc
     * @return
     */
    public Page<Ypclrkcx> getypclrkcxlist(YpjxcRc ypjxcRc){
        int total = tollInfoDao.countYpclrkcx(ypjxcRc);
        List<Ypclrkcx> list2 = null;
        if(total > 0){
            list2 = tollInfoDao.getypclrkcxlist(ypjxcRc);
        }
        return new Page<>((long) total, list2);
    }

    /**
     * 药品材料入库信息汇总
     * @param ypjxcRc
     * @return
     */
    public Page<Ypclrkcx> getypclrkcxsums(YpjxcRc ypjxcRc){
        int total = tollInfoDao.countYpclrkcx(ypjxcRc);
        List<Ypclrkcx> list2 = null;
        if(total > 0){
            list2 = tollInfoDao.getypclrkcxsums(ypjxcRc);
        }
        return new Page<>((long) total, list2);
    }

    /**
     * 机构管理-药品进销存信息
     */
    public Page<Ypjxc> getpharmaceuticalInventoryManagement(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        return getypjxcmanagement(ypjxcRc);
    }
    /**
     * 机构管理-药品进销存汇总数量和价格
     */
    public Page<Ypjxc> getpharmaceuticalInventoryManagementsums(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        return getypjxcmanagementsums(ypjxcRc);
    }

    public List<Ypjxc> getjglist(YpjxcRc ypjxcRc){
        return tollInfoDao.getjglist(ypjxcRc);
    }

    /**
     * 机构管理-材料进销存信息
     */
    public Page<Ypjxc> getmaterialmanagement(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        return getcljxcmanagement(ypjxcRc);
    }

    /**
     * 机构管理-材料进销存汇总数量和价格
     */
    public Page<Ypjxc> getmaterialmanagementsums(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        return getcljxcmanagementsums(ypjxcRc);
    }

    /**
     * 机构管理-药品材料入库信息统计
     */
    public Page<Ypclrkcx> drugmaterialsstockmanagement(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        return getypclrkcxlist(ypjxcRc);
    }

    /**
     * 机构管理-药品材料入库信息汇总
     */
    public Page<Ypclrkcx> drugmaterialsstockmanagementsums(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        return getypclrkcxsums(ypjxcRc);
    }

    /**
     * 机构管理-获取耗材销售情况
     */
    public Page<Stuffsalessummary> getconsumablemarketstatistics(StuffsalessummaryRc stuffsalessummaryRc){
        stuffsalessummaryRc.setJgzt("1");
        return getStuffsalessummarylists(stuffsalessummaryRc);
    }

    /**
     * 机构管理-获取耗材销售信息汇总价格和数量
     */
    public Page<Stuffsalessummary> getconsumablemarketstatisticssum(StuffsalessummaryRc stuffsalessummaryRc){
        stuffsalessummaryRc.setJgzt("1");
        return getStuffsalessummarysumss(stuffsalessummaryRc);
    }

    public Page<DrugSales> getdrugmarketstatistics(List<Parameter> params, int offset, int limit, String orderby) {
        return getDrugSales(params, offset, limit, orderby);
    }

    public DrugSales getdrugmarketstatisticsStat(List<Parameter> params, int offset, int limit, String orderby){
        return getDrugSalesStat(params, offset, limit, orderby);
    }


    public Page<WorkLoad> getdoctorDetailstatistics(List<Parameter> params,int offset,int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        List<WorkLoad> count = this.dao.countWorkload(pageRequest);
        int total=0;
        if(count!=null&&count.size()>0){
            total=count.size();
        }
        List<WorkLoad> workloads=null;
        if(total>0){
            workloads = this.dao.getWorkload(pageRequest);

            for (WorkLoad workLoad:
                    workloads) {
                List<Parameter> doctorParams = new ArrayList<>(params);
                doctorParams.addAll(SearchParamsBuilder.create()
                        .eq("su.name", workLoad.getName())
                        .build());
                PageRequest pageRequest1 = new PageRequest(doctorParams, orderby);
                List<WorkLoad> temporaryCosts = this.dao.getTemporaryCost(pageRequest1);
                for (WorkLoad temporaryCost : temporaryCosts) {
                    setCostByTypeName(workLoad, temporaryCost.getTypes(), temporaryCost.getTemporaryCost());
                }
            }
        }

        return new Page<>((long)total, workloads);
    }

    public WorkLoadStat getdoctorDetailstatisticsStat(List<Parameter> params,int offset,int limit, String orderby){
        List<Parameter> parameters = new ArrayList<>();
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        List<WorkLoadStat> workLoadStat = this.dao.getWorkLoadStats(pageRequest);
        WorkLoadStat workLoadStat2 = new WorkLoadStat();
        if(!CollectionUtils.isEmpty(workLoadStat)){

            BigDecimal count = new BigDecimal("0");
            BigDecimal grossAmount = new BigDecimal("0");
            BigDecimal registrationCost = new BigDecimal("0");
            for (WorkLoadStat loadStat : workLoadStat) {
                count = count.add(new BigDecimal(loadStat.getCount())==null?new BigDecimal("0"):new BigDecimal(loadStat.getCount()));
                grossAmount = grossAmount.add(loadStat.getGrossAmount()==null?new BigDecimal("0"):loadStat.getGrossAmount());
                registrationCost = registrationCost.add(loadStat.getRegistrationCost()==null?new BigDecimal("0"):loadStat.getRegistrationCost());
            }

            workLoadStat2.setCount(count.toString());
            workLoadStat2.setGrossAmount(grossAmount);
            workLoadStat2.setRegistrationCost(registrationCost);

            parameters.clear();
            parameters.addAll(params);
            PageRequest pageRequest1 = new PageRequest(parameters, orderby);
            List<WorkLoadStat> temporaryCostStat = this.dao.getTemporaryCostStat(pageRequest1);
            if (temporaryCostStat != null && temporaryCostStat.size() > 0) {
                for (WorkLoadStat workLoadStat1 : temporaryCostStat) {
                    setCostByTypeName(workLoadStat2, workLoadStat1.getTypes(), workLoadStat1.getTemporaryCost());
                }
            }
        }
        return workLoadStat2;
    }


    //报表导出
    public void exportExcel(SearchParams searchParams, HttpServletResponse response) throws IOException {
        if(Objects.equals("doctorDetail",searchParams.getColumnName())){
            //医生收入统计
            exportDoctorDetail(searchParams,response);
        }else if(Objects.equals("drugDetail",searchParams.getColumnName())){
            //药品销售汇总统计
            exportDrugDetail(searchParams,response);
        }
    }

    //药品销售汇总统计导出
    private void exportDrugDetail(SearchParams searchParams, HttpServletResponse response) throws IOException {
        Page<DrugSales> drugSalesPage = getDrugSales(searchParams.getParams(), 0, 1000000, searchParams.getOrder());
        List<Map<String, Object>> rows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(drugSalesPage.getRows())) {
            for (DrugSales ds : drugSalesPage.getRows()) {
                Map<String, Object> row = new HashMap<>();
                row.put("type", ds.getType());
                row.put("name", ds.getName());
                // 规格 = 剂量+剂量单位*数量+数量单位/包装
                String norms = ds.getDosis() + ds.getDosisUnit() + "*" + ds.getPreparation() + ds.getPreparationUnit() + "/" + ds.getPack();
                row.put("norms", norms);
                // 数量换算
                int prep = Integer.parseInt(ds.getPreparation());
                int wholePacks = ds.getTotal() / prep;
                int remainder = ds.getTotal() % prep;
                String number = wholePacks + ds.getPack() + (remainder > 0 ? remainder + ds.getPreparationUnit() : "");
                row.put("number", number);
                row.put("allFee", BigdecimalConvert.convert(ds.getAllFee()));
                rows.add(row);
            }
        }
        new ExcelExportBuilder("药品销售汇总统计表")
                .addColumns(
                        new ExcelExportBuilder.Column("药品分类", "type"),
                        new ExcelExportBuilder.Column("药品名称", "name"),
                        new ExcelExportBuilder.Column("规格", "norms"),
                        new ExcelExportBuilder.Column("数量", "number"),
                        new ExcelExportBuilder.Column("总价(元)", "allFee")
                )
                .data(rows)
                .write(response, "药品销售汇总统计.xlsx");
    }

    //医生收入统计导出
    private void exportDoctorDetail(SearchParams searchParams, HttpServletResponse response) throws IOException {
        Page<WorkLoad> workloadPage = getWorkload(searchParams.getParams(), 0, 1000000, searchParams.getOrder());
        List<Map<String, Object>> rows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(workloadPage.getRows())) {
            for (WorkLoad wl : workloadPage.getRows()) {
                Map<String, Object> row = new HashMap<>();
                row.put("name", wl.getName());
                row.put("count", wl.getCount());
                row.put("grossAmount", BigdecimalConvert.convert(wl.getGrossAmount()));
                row.put("registrationCost", BigdecimalConvert.convert(wl.getRegistrationCost()));
                row.put("westCost", BigdecimalConvert.convert(wl.getWestCost()));
                row.put("chineseCost", BigdecimalConvert.convert(wl.getChineseCost()));
                row.put("chinesePatentCost", BigdecimalConvert.convert(wl.getChinesePatentCost()));
                row.put("stuffCost", BigdecimalConvert.convert(wl.getStuffCost()));
                row.put("examinesCost", BigdecimalConvert.convert(wl.getExaminesCost()));
                row.put("checkoutCost", BigdecimalConvert.convert(wl.getCheckoutCost()));
                row.put("therapyCost", BigdecimalConvert.convert(wl.getTherapyCost()));
                row.put("cureCost", BigdecimalConvert.convert(wl.getCureCost()));
                row.put("otherCost", BigdecimalConvert.convert(wl.getOtherCost()));
                rows.add(row);
            }
        }
        new ExcelExportBuilder("医生收入统计表")
                .addColumns(
                        new ExcelExportBuilder.Column("医生姓名", "name"),
                        new ExcelExportBuilder.Column("接诊次数", "count"),
                        new ExcelExportBuilder.Column("总金额(元)", "grossAmount"),
                        new ExcelExportBuilder.Column("挂号费(元)", "registrationCost"),
                        new ExcelExportBuilder.Column("西药费(元)", "westCost"),
                        new ExcelExportBuilder.Column("中草药费(元)", "chineseCost"),
                        new ExcelExportBuilder.Column("中成药费(元)", "chinesePatentCost"),
                        new ExcelExportBuilder.Column("材料费(元)", "stuffCost"),
                        new ExcelExportBuilder.Column("检验费(元)", "examinesCost"),
                        new ExcelExportBuilder.Column("检查费(元)", "checkoutCost"),
                        new ExcelExportBuilder.Column("理疗费(元)", "therapyCost"),
                        new ExcelExportBuilder.Column("治疗费(元)", "cureCost"),
                        new ExcelExportBuilder.Column("其他", "otherCost")
                )
                .data(rows)
                .write(response, "医生收入统计.xlsx");
    }

    @Transactional(readOnly = true)
    public void exportDrugOrStuffStock(YpjxcRc ypjxcRc, HttpServletResponse response) throws IOException {
            exportDrugStock(ypjxcRc,response);
    }

    private void exportDrugStock(YpjxcRc ypjxcRc, HttpServletResponse response) throws IOException {
        boolean isDrug = Objects.equals("1", ypjxcRc.getYpcltype());
        String sheetName = isDrug ? "药品入库统计表" : "材料入库统计表";
        String fileName = isDrug ? "药品入库统计.xlsx" : "材料入库统计.xlsx";

        Page<Ypclrkcx> page = getypclrkcxlist(ypjxcRc);
        List<Map<String, Object>> rows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(page.getRows())) {
            for (Ypclrkcx item : page.getRows()) {
                Map<String, Object> row = new HashMap<>();
                row.put("rkrq", item.getRkrq());
                row.put("rkdh", item.getRkdh());
                row.put("rkry", item.getRkry());
                row.put("spbh", item.getSpbh());
                row.put("spmc", item.getSpmc());
                // 分类
                if (isDrug) {
                    String type = Objects.equals(BizConstants.MEDICAL_TYPE_WESTERN, item.getLx()) ? "西药" : Objects.equals(BizConstants.MEDICAL_TYPE_HERBAL, item.getLx()) ? "中草药" : "中成药";
                    row.put("type", type);
                } else {
                    String type = Objects.equals(BizConstants.STUFF_TYPE_MATERIAL, item.getLx()) ? "非医用材料" : "医用材料";
                    row.put("type", type);
                }
                row.put("ph", item.getPh() != null ? item.getPh() : "");
                row.put("gg", item.getGg());
                row.put("sccj", item.getSccj() != null ? item.getSccj() : "");
                row.put("gys", item.getGys() != null ? item.getGys() : "");
                // 数量换算
                int sl = Integer.parseInt(item.getSl());
                int zj = Integer.parseInt(item.getZj());
                int wholePacks = sl / zj;
                int remainder = sl % zj;
                String number = wholePacks + item.getDw() + (remainder > 0 ? remainder + item.getZxdw() : "");
                row.put("number", number);
                row.put("dw", item.getDw() != null ? item.getDw() : "");
                row.put("lsj", new BigDecimal(item.getLsj() != null ? item.getLsj() : "0"));
                row.put("cbj", new BigDecimal(item.getCbj() != null ? item.getCbj() : "0"));
                row.put("cbhj", new BigDecimal(item.getCbhj() != null ? item.getCbhj() : "0"));
                row.put("yxq", item.getYxq() != null ? item.getYxq() : "");
                row.put("shzt", Objects.equals(BizConstants.SUPPLIER_STORAGE_EXAMINE_PASS, item.getShzt()) ? "通过" : "已作废");
                row.put("bz", item.getBz() != null ? item.getBz() : "");
                rows.add(row);
            }
        }
        new ExcelExportBuilder(sheetName)
                .addColumns(
                        new ExcelExportBuilder.Column("入库日期", "rkrq"),
                        new ExcelExportBuilder.Column("入库单号", "rkdh"),
                        new ExcelExportBuilder.Column("入库人员", "rkry"),
                        new ExcelExportBuilder.Column("商品编码", "spbh"),
                        new ExcelExportBuilder.Column("商品名称", "spmc"),
                        new ExcelExportBuilder.Column("分类", "type"),
                        new ExcelExportBuilder.Column("批号", "ph"),
                        new ExcelExportBuilder.Column("规格", "gg"),
                        new ExcelExportBuilder.Column("生产厂家", "sccj"),
                        new ExcelExportBuilder.Column("供应商", "gys"),
                        new ExcelExportBuilder.Column("数量", "number"),
                        new ExcelExportBuilder.Column("单位", "dw"),
                        new ExcelExportBuilder.Column("零售价(元)", "lsj"),
                        new ExcelExportBuilder.Column("成本价(元)", "cbj"),
                        new ExcelExportBuilder.Column("成本合计(元)", "cbhj"),
                        new ExcelExportBuilder.Column("有效期", "yxq"),
                        new ExcelExportBuilder.Column("审核状态", "shzt"),
                        new ExcelExportBuilder.Column("备注", "bz")
                )
                .data(rows)
                .write(response, fileName);
    }

    /**
     * 将费用明细按类型映射到统计对象的对应字段
     * 消除 getWorkload/getWorkLoadStat/getdoctorDetailstatistics 中重复的 if-else 链
     *
     * @param items 费用明细列表（每项包含 types 和 temporaryCost）
     * @param setter 类型→费用 的设置回调
     */
    private <T> void applyCostByType(List<T> items, java.util.function.BiConsumer<String, java.math.BigDecimal> setter) {
        if (items == null || items.isEmpty()) return;
        for (T item : items) {
            String types;
            java.math.BigDecimal cost;
            if (item instanceof WorkLoad) {
                types = ((WorkLoad) item).getTypes();
                cost = ((WorkLoad) item).getTemporaryCost();
            } else if (item instanceof WorkLoadStat) {
                types = ((WorkLoadStat) item).getTypes();
                cost = ((WorkLoadStat) item).getTemporaryCost();
            } else {
                continue;
            }
            setter.accept(types, cost);
        }
    }

    /**
     * 根据类型名称设置 WorkLoad 对应的费用字段
     */
    private void setCostByTypeName(WorkLoad workLoad, String types, java.math.BigDecimal cost) {
        switch (types) {
            case "中成药": workLoad.setChinesePatentCost(cost); break;
            case "西药":   workLoad.setWestCost(cost); break;
            case "中草药": workLoad.setChineseCost(cost); break;
            case "材料":   workLoad.setStuffCost(cost); break;
            case "检验":   workLoad.setExaminesCost(cost); break;
            case "检查":   workLoad.setCheckoutCost(cost); break;
            case "理疗":   workLoad.setTherapyCost(cost); break;
            case "治疗":   workLoad.setCureCost(cost); break;
            case "其他":   workLoad.setOtherCost(cost); break;
            default: break;
        }
    }

    /**
     * 根据类型名称设置 WorkLoadStat 对应的费用字段
     */
    private void setCostByTypeName(WorkLoadStat stat, String types, java.math.BigDecimal cost) {
        switch (types) {
            case "中成药": stat.setChinesePatentCost(cost); break;
            case "西药":   stat.setWestCost(cost); break;
            case "中草药": stat.setChineseCost(cost); break;
            case "材料":   stat.setStuffCost(cost); break;
            case "检验":   stat.setExaminesCost(cost); break;
            case "检查":   stat.setCheckoutCost(cost); break;
            case "理疗":   stat.setTherapyCost(cost); break;
            case "治疗":   stat.setCureCost(cost); break;
            case "其他":   stat.setOtherCost(cost); break;
            default: break;
        }
    }
}