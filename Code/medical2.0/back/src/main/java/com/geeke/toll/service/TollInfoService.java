package com.geeke.toll.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.sequence.service.SequenceService;
import com.geeke.common.service.CrudService;
import com.geeke.common.service.ServiceException;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.cure.entity.InspectionCheck;
import com.geeke.cure.entity.InspectionCheckDetail;
import com.geeke.cure.entity.InspectionCheckInfo;
import com.geeke.cure.service.InspectionCheckDetailService;
import com.geeke.cure.service.InspectionCheckInfoService;
import com.geeke.cure.service.InspectionCheckService;
import com.geeke.member.entity.MemberManagement;
import com.geeke.member.service.MemberManagementDetailService;
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
import com.geeke.toll.untils.BigdecimalConvert;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.entity.CostItemPackage;
import com.geeke.treatment.service.CostItemService;
import com.geeke.treatment.service.impl.CostItemPackageService;
import com.geeke.utils.*;
import com.geeke.utils.constants.ErrorEnum;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 收费管理Service
 * @author lc
 * @version 2022-06-22
 */
 
@Service("tollInfoService")
@Transactional(readOnly = true)
public class TollInfoService extends CrudService<TollInfoDao, TollInfo>{
    @Autowired
    private PatientService patientService;
    @Autowired
    private RecipelInfoService recipelInfoService;
    @Autowired
    private SequenceService sequenceService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private TollDetailService tollDetailService;
    @Autowired
    private MedicalRecordService medicalRecordService;
    @Autowired
    private RecipelDetailService recipelDetailService;
    @Autowired
    private StuffService stuffService;
    @Autowired
    private DrugService drugService;
    @Autowired
    private TollInfoDao tollInfoDao;

    @Autowired
    private CostItemService costItemService;

    @Autowired
    private InspectionCheckInfoService inspectionCheckInfoService;

    @Autowired
    private InspectionCheckDetailService inspectionCheckDetailService;

    @Autowired
    private InspectionCheckService inspectionCheckService;

    @Autowired
    private CostItemPackageService costItemPackageService;

    @Autowired
    private MemberManagementDetailService memberManagementDetailService;

    @Autowired
    private InventoryVerificationService inventoryVerificationService;

    @Autowired
    private SupplierStockService supplierStockService;

    @Autowired
    private DispensingService dispensingService;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Autowired
    private SerialNoUtils serialNoUtils;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private RecipelDetailDao recipelDetailDao;

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
        //正在进行盘点时不能进行收费
        List<InventoryVerification> inventoryVerifications = inventoryVerificationService.getByCompanyId(company.getId());
       if("amountStatus_1".equals(tollEvt.getTollInfo().getAmountStatus().getValue())) {
           if (!CollectionUtils.isEmpty(inventoryVerifications)) {
               throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "正在进行盘点，无法进行收费操作!"));

           }
       }else {
           if (!CollectionUtils.isEmpty(inventoryVerifications)) {
               throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "正在进行盘点，无法进行退费操作!"));

           }
       }
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
            if ("amountStatus_1".equals(tollInfo.getAmountStatus().getValue())) {
                if ("tollType_5".equals(tollInfo.getTollType().getValue())){
                    //零售收费
                    //患者如果不存在添加默认的
                    Patient patient1 = patientService.get(patient.getId());
                    if (ObjectUtils.isEmpty(patient1)) {
                        patient = patientService.save(patient);
                    }else {
                        patient = patient1;
                    }
                    //添加零售挂号
                    registration = this.addRetailRegistration(patient.getCompany(),patient);
                    //添加零售病例
                    MedicalRecord medicalRecord = this.addMedicalRecord(registration);
                    //添加零售处方
                    for (RecipelInfoEvt recipelInfoEvt : recipelInfoEvts) {
                        RecipelInfo recipelInfo = recipelInfoEvt.getRecipelInfo();
                        recipelInfo.setIsDispension("0");
                        recipelInfo.setIsPay("1");
                        recipelInfo.setPayDate(new Date());
                        recipelInfo.setChargeStatus(1);
                    }
                    RecipelInfo retail =this.addRetailRecipelInfo(recipelInfoEvts,medicalRecord);
                    tollInfo.setPatient(patient);
                    tollInfo.setRecipel(retail);
                    tollInfo.setMedical(medicalRecord);
                    tollSave = this.save(tollInfo);

                    //预占用库存
                    this.medicinalStorageControlService.preOccupyStock(retail);
                }else {
                    tollInfo.setPatient(patient);
                    //保存收费
                    List<RecipelInfoEvt> recipelInfos = recipelInfoEvts;
                    for (RecipelInfoEvt recipelInfoEvt : recipelInfos) {
                        RecipelInfo recipelInfo = recipelInfoEvt.getRecipelInfo();
                        recipelInfo = recipelInfoService.get(recipelInfo.getId());
                        String value = tollInfo.getAmountStatus().getValue();
                        recipelInfo.setIsPay(value.substring(value.length() - 1));
                        recipelInfo.setPayDate(new Date());
                        recipelInfo.setIsDispension("0");
                        recipelInfo.setChargeStatus(1);
                        recipelInfo.setAmountReceivedTotal(tollEvt.getTollInfo().getAmountReceived());
                        recipelInfoService.save(recipelInfo);
                        TollInfo newTollInfo = new TollInfo();
                        BeanUtils.copyProperties(tollInfo,newTollInfo);
                        newTollInfo.setMedical(medicalRecordService.get(tollInfo.getMedical().getId()));
                        newTollInfo.setRecipel(recipelInfo);
                        newTollInfo.setAmountReceivable(recipelInfo.getFee());
                        newTollInfo.setAmountReceived(this.getAmountReceived(recipelInfo,tollInfo));
//                        newTollInfo.setAmountReceived(tollInfo.getAmountReceived());
                        newTollInfo.setAmountDiscounted(newTollInfo.getAmountReceivable().subtract(newTollInfo.getAmountReceived()));
                        DictItem tollType = newTollInfo.getTollType();

                        String tollTyepName=recipelInfo.getName().substring(0,2);
                        if("西药".equals(tollTyepName)){
                            tollType.setName("西药处方");
                            tollType.setValue("tollType_0");
                        }else if("中药".equals(tollTyepName)){
                            tollType.setName("中药处方");
                            tollType.setValue("tollType_1");
                        }else if("输液".equals(tollTyepName)){
                            tollType.setName("输液处方");
                            tollType.setValue("tollType_2");
                        }else if("诊疗".equals(tollTyepName)){
                            tollType.setName("诊疗项目");
                            tollType.setValue("tollType_3");
                        }
                        if("诊疗".equals(tollTyepName) && newTollInfo.getAmountReceivable()!=newTollInfo.getAmountReceived()){
                            newTollInfo.setRemarks("体验卡扣减");
                        }
                        newTollInfo.setTollType(tollType);
                        tollSave = this.save(newTollInfo);

                        //收完费后如果是诊疗项目并且体验卡不为空时进行修改
                        MemberManagement memberManagement = tollEvt.getMemberManagement();
                        if(!ObjectUtils.isEmpty(memberManagement)){
                            if("诊疗".equals(tollTyepName) && !CollectionUtils.isEmpty(memberManagement.getMemberManagementDetails())){
                                memberManagementDetailService.updateUseNumber(memberManagement);
                            }
                        }

                        //收完费，如果存在诊疗检验检查的项目，将数据存入到数据库中
                        if("诊疗".equals(tollTyepName)){
                            addInspectionCheck(recipelInfo);
                        }

                        //收费后，如果存在诊疗项目中的附加费有材料，需要进行扣减
                        if("诊疗".equals(tollTyepName)){
                            deduction(recipelInfo);

                        }
                    }
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
            if ("amountStatus_2".equals(tollInfo.getAmountStatus().getValue())) {
                for (RecipelInfoEvt recipelInfoEvt : recipelInfoEvts) {

                    RecipelInfo recipelInfo = recipelInfoEvt.getRecipelInfo();
                    recipelInfo = recipelInfoService.get(recipelInfo.getId());
                    String value = tollInfo.getAmountStatus().getValue();
                    System.out.println(recipelInfo.getIsDispension());
                    if("1".equals(recipelInfo.getIsDispension())){
                        throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "该用户已发药，无法退费！"));
                    }
                    if("recipelType_3".equals(recipelInfo.getRecipelType().getValue())){
                        List<RecipelDetail> recipelDetails = recipelDetailService.getByRecipelInfoId(recipelInfo.getId());
                        for (RecipelDetail recipelDetail:
                                recipelDetails) {
                            if(recipelDetail.getExecutions().compareTo(BigDecimal.valueOf(0))!=0){
                                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "诊疗项目已执行划扣，无法退费！"));
                            }
                        }
                        //根据登记信息获取检验检查信息
                        boolean flages=false;
                        List<InspectionCheck> inspectionChecks = inspectionCheckService.getByRecipelInfoId(recipelInfo.getId());
                        for (InspectionCheck inspectionCheck : inspectionChecks) {
                            if("1".equals(inspectionCheck.getStatus())){
                                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "检验检查报告已填写，无法退费！"));
                            }
                        }
                        //没有填写则删除
                        inspectionCheckService.deleteByRecipelInfoId(recipelInfo.getId());
                    }
                    recipelInfo.setIsPay(value.substring(value.length() - 1));
                    recipelInfo.setChargeStatus(-1);
                    recipelInfo.setRetreatDate(new Date());
                    recipelInfoService.save(recipelInfo);
                    List<TollInfo> tollInfos = this.getByRecipeId(recipelInfo.getId());
                    for (TollInfo info : tollInfos) {
                        TollInfo refund = new TollInfo();
                        TollInfo tollInfo1 = new TollInfo();
                        BeanUtils.copyProperties(info,refund);
                        BeanUtils.copyProperties(info,tollInfo1);
                        refund.setId("");
                        tollInfo1.setReturnType(1);
                        refund.setAmountStatus(tollInfo.getAmountStatus());
                        refund.setReturnType(1);
                        tollSave = this.save(refund);
                        this.save(tollInfo1);
                    }
//                    //退完费用如果是诊疗项目需要退还其材料
                    if("recipelType_3".equals(recipelInfo.getRecipelType().getValue())) {
                        returnPremium(recipelInfo);
                    }

                    //退费后需要进行退药退材料操作
                    if(recipelInfo.getDispensionStatus()==0&&!"recipelType_3".equals(recipelInfo.getRecipelType().getValue())){
                        //收费未发药退费情况
                        medicinalStorageControlService.cancelOccupy(recipelInfo);
                    }else if(recipelInfo.getDispensionStatus()==-1&&!"recipelType_3".equals(recipelInfo.getRecipelType().getValue())){
                        //退药后退费情况
                        medicinalStorageControlService.goBackFee(recipelInfo);
                    }
                }

            }
            //挂号支付方式，收费状态
            if (null == registration) {
                registration = registrationService.get(tollInfo.getMedical().getRegistration().getId());

            }
            //支付方式共用
            if (StringUtils.isNullOrEmpty(registration.getPayType())) {
                registration.setPayType(tollInfo.getPaymentType());
            }
//            //支付状态为：部分收费，已收费时改收费时间
//            if ("1".equals(tollEvt.getChargeStatus()) || "2".equals(tollEvt.getChargeStatus()) ) {
//                //修改付费状态
//
//                registration.setChargeDate(new Date());
//            }
            //没有申明方式 或零售 就默认是整个挂号单
            if(StringUtils.isNullOrEmpty(type) || "tollType_2".equals(tollInfo.getTollType().getValue()))
            {
                registration.setChargeStatus(tollEvt.getChargeStatus());
            }
            //否则只是单个处方
            if ("amountStatus_1".equals(tollInfo.getAmountStatus().getValue())) {
                registration.setChargeDate(new Date());
            }else if ("amountStatus_2".equals(tollInfo.getAmountStatus().getValue())) {
                registration.setretreatsDate(new Date());
            }
            registrationService.save(registration);
        }
    }

    //诊疗项目退费，如果存在材料收费则需退还
    private void returnPremium(RecipelInfo recipelInfo) {
//        ArrayList<Parameter> parameters = new ArrayList<>();
//        parameters.add(new Parameter("recipel_info_id","=",recipelInfo.getId()));
//        List<Dispensing> dispensings = dispensingService.listAll(parameters, "");
//        if(!CollectionUtils.isEmpty(dispensings)){
//            for (Dispensing dispensing : dispensings) {
//                SupplierStock supplierStock = supplierStockService.get(dispensing.getSupplierStock().getId());
//                Integer number = dispensing.getNumber();
//                supplierStock.setNumber(supplierStock.getNumber()+number);
//                supplierStockService.save(supplierStock);
//                stuffService.updateInventory(number,supplierStock.getStuff().getId());
//            }
             medicinalStorageControlService.materialRefund(recipelInfo);
            //修改发药信息表
            dispensingService.updateDelFlag(recipelInfo.getId());
       // }
    }

    //诊疗项目进行附加费材料扣减
    private void deduction(RecipelInfo recipelInfo) {
        medicinalStorageControlService.okOccupyStock(recipelInfo.getRegistration().getId(),recipelInfo.getId());
//        List<RecipelDetail> byRecipelInfoId = recipelDetailService.getByRecipelInfoId(recipelInfo.getId());
//        for (RecipelDetail recipelDetail : byRecipelInfoId) {
//            if(Objects.equals(recipelDetail.getStuffType(),"4")&&recipelDetail.getIsExtra()==1){
//                Dispensing dispensing = new Dispensing();
//                Registration registration = new Registration();
//                registration.setId(recipelInfo.getRegistration().getId());
//                dispensing.setRecipelInfo(recipelInfo);
//                dispensing.setRegistration(registration);
                //进行材料扣减
                //进行实际暂用

//                Integer total = recipelDetail.getTotal();
//                List<SupplierStock> byStuffIdDetail = supplierStockService.getByStuffIdDetail(recipelDetail.getDrugStuffId().getDrugStuffId());
//                if(!CollectionUtils.isEmpty(byStuffIdDetail)){
//                    for (SupplierStock supplierStock : byStuffIdDetail) {
//                        Integer number = supplierStock.getNumber();
//                        if(number>=total && total>0){
//                            int initalNumber = total;
//                            supplierStock.setNumber(number-total);
//                            supplierStockService.save(supplierStock);
//                            stuffService.updateInventory(0-initalNumber,supplierStock.getStuff().getId());
//                            total = 0;
//                            //保存发药明细表
//                            dispensing.setId("");
//                            dispensing.setSupplierStock(supplierStock);
//                            dispensing.setNumber(initalNumber);
//                            dispensing.setCompany(supplierStock.getCompany());
//                            String id = dispensingService.save(dispensing).getId();
//                            break;
//                        }else if(total>number&&total>0){
//                            total = total-number;
//                            supplierStock.setNumber(0);
//                            supplierStockService.save(supplierStock);
//                            stuffService.updateInventory(0-number,supplierStock.getStuff().getId());
//                            //保存发药明细表
//                            dispensing.setId("");
//                            dispensing.setSupplierStock(supplierStock);
//                            dispensing.setNumber(number);
//                            dispensing.setCompany(supplierStock.getCompany());
//                            String id = dispensingService.save(dispensing).getId();
//                        }
//                    }
//
//                    if(total>0){
//                        throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "库存不足，请补充库存!或者进行退费和作废处方。"));
//                    }
               // }
         //   }
       // }
    }

    //保存检验检测
    @Transactional(readOnly = false)
    private void addInspectionCheck(RecipelInfo recipelInfo) {
        //通过处方id，去获取诊疗项目
        List<CostItem> costItems = costItemService.getByRecipelInfo(recipelInfo);
        //判断诊疗项目是否为检验检查
        for (CostItem costItem : costItems) {
            InspectionCheck inspectionCheck = new InspectionCheck();
            if("treatmentItemType_0".equals(costItem.getItemType().getValue())){
                inspectionCheck.setRecipelInfo(recipelInfo);
                inspectionCheck.setRecipelDetail(costItem.getRecipelDetail());
                inspectionCheck.setCompany(recipelInfo.getCompany());
                inspectionCheck.setCostItem(costItem);
                inspectionCheck.setName(costItem.getItemName());
                inspectionCheck.setStatus("0");
                inspectionCheck.setType("0");
                inspectionCheck.setRegistration(recipelInfo.getRegistration());
                //根据登记信息获取患者信息
                Registration registration = registrationService.get(recipelInfo.getRegistration().getId());
                Patient patient = patientService.get(registration.getPatientId().getId());
                inspectionCheck.setPatient(patient);
                inspectionCheck.setPatientName(patient.getName());
                inspectionCheck.setSex(patient.getGender().getValue());
                inspectionCheck.setPhone(patient.getPhone());
                inspectionCheck.setCompleteBy(registration.getDoctor().getName());
                inspectionCheck.setCompleteDate(registration.getReceptionEndDate());
                String uuid = IdGen.uuid();
                inspectionCheck.setId(uuid);

                inspectionCheckService.allSave(inspectionCheck);
                //插入主表后，需要插入到明细和详情表中
                InspectionCheckInfo inspectionCheckInfo = new InspectionCheckInfo();
                String uuid1 = IdGen.uuid();
                inspectionCheckInfo.setId(uuid1);
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
                int seq=0;
                if(!CollectionUtils.isEmpty(all)){
                    for (CostItemPackage costItemPackage : all) {
                        seq++;
                        InspectionCheckDetail inspectionCheckDetail = new InspectionCheckDetail();
                        inspectionCheckDetail.setId(IdGen.uuid());
                        inspectionCheckDetail.setCompany(recipelInfo.getCompany());
                        inspectionCheckDetail.setInspectionCheckInfo(inspectionCheckInfo);
                        inspectionCheckDetail.setSeq(seq);
                        CostItem costItem1 = new CostItem();
                        String costIitemId=costItemPackage.getCostItemId();
                        if(costIitemId!=null){
                            costItem1.setId(costIitemId);
                        }else {
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
            }else if( "treatmentItemType_1".equals(costItem.getItemType().getValue())){
                inspectionCheck.setRecipelInfo(recipelInfo);
                inspectionCheck.setCompany(recipelInfo.getCompany());
                inspectionCheck.setCostItem(costItem);
                inspectionCheck.setName(costItem.getItemName());
                inspectionCheck.setStatus("0");
                inspectionCheck.setType("1");
                inspectionCheck.setRegistration(recipelInfo.getRegistration());
                //根据登记信息获取患者信息
                Registration registration = registrationService.get(recipelInfo.getRegistration().getId());
                Patient patient = patientService.get(registration.getPatientId().getId());
                inspectionCheck.setPatient(patient);
                inspectionCheck.setPatientName(patient.getName());
                inspectionCheck.setSex(patient.getGender().getValue());
                inspectionCheck.setPhone(patient.getPhone());
                inspectionCheck.setCompleteBy(registration.getDoctor().getName());
                inspectionCheck.setCompleteDate(registration.getReceptionEndDate());
                String uuid = IdGen.uuid();
                inspectionCheck.setId(uuid);
                inspectionCheckService.allSave(inspectionCheck);

                //插入主表后，需要插入到明细和详情表中
                InspectionCheckInfo inspectionCheckInfo = new InspectionCheckInfo();
                String uuid1 = IdGen.uuid();
                inspectionCheckInfo.setId(uuid1);
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
                int seq=0;
                if(!CollectionUtils.isEmpty(all)){
                    for (CostItemPackage costItemPackage : all) {
                        seq++;
                        InspectionCheckDetail inspectionCheckDetail = new InspectionCheckDetail();
                        inspectionCheckDetail.setId(IdGen.uuid());
                        inspectionCheckDetail.setCompany(recipelInfo.getCompany());
                        inspectionCheckDetail.setInspectionCheckInfo(inspectionCheckInfo);
                        inspectionCheckDetail.setSeq(seq);
                        CostItem costItem1 = new CostItem();
                        costItem1.setId(costItemPackage.getCostItemId());
                        inspectionCheckDetail.setCostItem(costItem1);
                        inspectionCheckDetail.setCreateBy(userObj.getString("name"));
                        inspectionCheckDetail.setUpdateBy(userObj.getString("name"));
                        inspectionCheckDetail.setCreateDate(new Date());
                        inspectionCheckDetail.setUpdateDate(new Date());
                        inspectionCheckDetailService.allSave(inspectionCheckDetail);
                    }
                }
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
        List parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("name", "=", patient.getName()));
        parameters.add(new Parameter("company_id", "=", company.getId()));
        parameters.add(new Parameter("phone", "=", patient.getPhone()));
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
            dictItem.setValue("registrationStatus_1");
            registration.setStatus(dictItem);
            //已收费
            registration.setChargeStatus("2");
            return registrationService.save(registration);
    }

    private RecipelInfo addRetailRecipelInfo(List<RecipelInfoEvt> recipelInfos,MedicalRecord medicalRecord) {
        RecipelInfo save = null;
        DictItem dictItem = new DictItem();
        dictItem.setValue("recipelType_5");
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
//                Stuff stuff = stuffService.get(recipelDetail.getDrugStuffId().getDrugStuffId());
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
        List parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("recipel_id", "in", recipelInfoIds));
        parameters.add(new Parameter("amount_status", "=", amountStatus));
        return this.listAll(parameters,"");
    }

    public List<TollInfo> getByRecipeId(String recipelInfoId){
        List parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("recipel_id", "=", recipelInfoId));
        return this.listAll(parameters,"");
    }

    public List<TollInfo> getByMedicalId(String medicalId) {
        List parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("medical_id", "=", medicalId));
        return this.listAll(parameters, "");
    }

    public ResponseEntity<JSONObject> tollTotalForm(SearchParams searchParams) {
        // 入参columnName格式：recipelInfo.recipel_type|costItem.item_type
        PageRequest pageRequest = new PageRequest(searchParams.getOffset(), searchParams.getLimit(), searchParams.getParams(), searchParams.getOrderby());
        int total = this.dao.formTollCount(pageRequest);
        List<TollInfo> list = null;
        if (total > 0) {
            list = this.dao.tollTotalForm(pageRequest);
        }
        TollVo tollVo = this.getTotalTollForSearch(pageRequest);
        tollVo.setPage(new Page((long)total, list));
        return ResponseEntity.ok(ResultUtil.successJson(tollVo));
    }

    public ResponseEntity<JSONObject> orgtolldetail(SearchParams searchParams) {
        // 入参columnName格式：recipelInfo.recipel_type|costItem.item_type
        PageRequest pageRequest = new PageRequest(searchParams.getOffset(), searchParams.getLimit(), searchParams.getParams(), searchParams.getOrderby());
        int total = this.dao.formTollCount(pageRequest);
        List<TollInfo> list = null;
        if (total > 0) {
            list = this.dao.tollTotalForm(pageRequest);
        }
        TollVo tollVo = this.getTotalTollForSearch(pageRequest);
        tollVo.setPage(new Page((long)total, list));
        return ResponseEntity.ok(ResultUtil.successJson(tollVo));
    }

    public ResponseEntity<JSONObject> tollDetailForm(SearchParams searchParams) {
        // 入参columnName格式：toll_type|patient_id|department.id|user.id|recipelInfo.recipel_type in ["_0","_1"]
        PageRequest pageRequest = new PageRequest(searchParams.getOffset(), searchParams.getLimit(), searchParams.getParams(), searchParams.getOrderby());
        int total = this.dao.formDetailCount(pageRequest);
        List<TollInfo> list = null;
        if (total > 0) {
            list = this.dao.tollDetailForm(pageRequest);
            for (TollInfo i : list){
                i.getDoctor().getDepartment().setName(i.getDepartment());
            }
        }
        TollVo tollVo = this.dao.tollDetailAmountReceivedAble(pageRequest);
        tollVo.setPage(new Page((long)total, list));
        return ResponseEntity.ok(ResultUtil.successJson(tollVo));
    }


    public ResponseEntity<JSONObject> orgtollDetailForm(SearchParams searchParams) {
        // 入参columnName格式：toll_type|patient_id|department.id|user.id|recipelInfo.recipel_type in ["_0","_1"]
        PageRequest pageRequest = new PageRequest(searchParams.getOffset(), searchParams.getLimit(), searchParams.getParams(), searchParams.getOrderby());
        int total = this.dao.formDetailCount(pageRequest);
        List<TollInfo> list = null;
        if (total > 0) {
            list = this.dao.tollDetailForm(pageRequest);
        }
        TollVo tollVo = this.dao.tollDetailAmountReceivedAble(pageRequest);
        tollVo.setPage(new Page((long)total, list));
        return ResponseEntity.ok(ResultUtil.successJson(tollVo));
    }

    //获取查询总金额
    private TollVo getTotalTollForSearch(PageRequest pageRequest) {
        TollVo tollVo = this.dao.tollTotalAmountReceivableAndAbleTotal(pageRequest);
//        TollVo tollVoReturn = this.dao.tollTotalAmountReturnAndAbleTotal(pageRequest);
//        BigDecimal amountReceivableTotal = tollVo.getAmountReceivableTotal().subtract(tollVoReturn.getAmountReceivableTotal());
//        BigDecimal amountReceivedTotal = tollVo.getAmountReceivedTotal().subtract(tollVoReturn.getAmountReceivedTotal());
        BigDecimal cashTotal = this.dao.tollTotalCashTotal(pageRequest);
        BigDecimal alipayTotal = this.dao.tollTotalAlipayTotal(pageRequest);
        BigDecimal weChatTotal = this.dao.tollTotalWeChatTotal(pageRequest);
        BigDecimal bankCardPayTotal = this.dao.tollTotalBankCardPayTotal(pageRequest);
        BigDecimal ybTotalTotal = this.dao.tollTotalYbTotalTotal(pageRequest);

//        tollVo.setAmountReceivableTotal(amountReceivableTotal);
//        tollVo.setAmountReceivedTotal(amountReceivedTotal);
        tollVo.setCashTotal(cashTotal);
        tollVo.setAlipayTotal(alipayTotal);
        tollVo.setWeChatTotal(weChatTotal);
        tollVo.setBankCardPayTotal(bankCardPayTotal);
        tollVo.setYbTotal(ybTotalTotal);
        return tollVo;
    }

    public TollInfo getTollInfoByRegistrationId(String registrationId) {
        return tollInfoDao.getTollInfoByRegistrationId(registrationId);
    }

    public List<TollInfo> getCreateBy(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);
        return this.dao.getCreateBy(pageRequest);
    }

    public Page<WorkLoad> getWorkload(List<Parameter> params,int offset,int limit, String orderby) {
        List parameters =new ArrayList<Parameter>();
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        List<WorkLoad> count = this.dao.countWorkload(pageRequest);
        int total=0;
        if(count!=null&&count.size()>0){
            total=count.size();
        }
        List<WorkLoad> workloads=null;
        if(total>0){
            workloads = this.dao.getWorkload(pageRequest);
            List<WorkLoad> workLoads = null;
            workLoads = this.dao.getCount(pageRequest);
            for (WorkLoad workLoad: workloads) {
                for (WorkLoad workload: workLoads){
                    if (workLoad.getName().equals(workload.getName())){
                        workLoad.setCount(workload.getCount());
                    }
                }
                parameters.clear();
                parameters.addAll(params);
                parameters.add(new Parameter("su.name", "=", workLoad.getName()));
                PageRequest pageRequest1 = new PageRequest(parameters, orderby);
                List<WorkLoad> temporaryCosts = this.dao.getTemporaryCost(pageRequest1);
                //System.out.println(temporaryCost.get(1).toString());
                for (WorkLoad temporaryCost:
                        temporaryCosts) {
                    if("中成药".equals(temporaryCost.getTypes())){
                        workLoad.setChinesePatentCost(temporaryCost.getTemporaryCost());
                    }else if("西药".equals(temporaryCost.getTypes())){
                        workLoad.setWestCost(temporaryCost.getTemporaryCost());
                    }else if("中草药".equals(temporaryCost.getTypes())){
                        workLoad.setChineseCost(temporaryCost.getTemporaryCost());
                    }else if("材料".equals(temporaryCost.getTypes())){
                        workLoad.setStuffCost(temporaryCost.getTemporaryCost());
                    }else if("检验".equals(temporaryCost.getTypes())){
                        workLoad.setExaminesCost(temporaryCost.getTemporaryCost());
                    }else if("检查".equals(temporaryCost.getTypes())){
                        workLoad.setCheckoutCost(temporaryCost.getTemporaryCost());
                    }else if("理疗".equals(temporaryCost.getTypes())){
                        workLoad.setTherapyCost(temporaryCost.getTemporaryCost());
                    }else if("治疗".equals(temporaryCost.getTypes())){
                        workLoad.setCureCost(temporaryCost.getTemporaryCost());
                    }else if("其他".equals(temporaryCost.getTypes())){
                        workLoad.setOtherCost(temporaryCost.getTemporaryCost());
                    }
                }
            }
        }

        return new Page((long)total, workloads);
    }

    public WorkLoadStat getWorkLoadStat(List<Parameter> params,int offset,int limit, String orderby){
        List parameters = new ArrayList<Parameter>();
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        WorkLoadStat workLoadStat = this.dao.getWorkLoadStat(pageRequest);
        WorkLoadStat workLoads = null;
        workLoads = this.dao.getCounts(pageRequest);
        if (!workLoads.getCount().equals("0")){
            workLoadStat.setCount(workLoads.getCount());
        }
        if(workLoadStat!=null){
            parameters.clear();
            parameters.addAll(params);
            PageRequest pageRequest1 = new PageRequest(parameters, orderby);
            List<WorkLoadStat> temporaryCostStat = this.dao.getTemporaryCostStat(pageRequest1);
            if(temporaryCostStat!=null&&temporaryCostStat.size()>0){
                for (WorkLoadStat workLoadStat1:
                temporaryCostStat) {
                    if("中成药".equals(workLoadStat1.getTypes())){
                        workLoadStat.setChinesePatentCost(workLoadStat1.getTemporaryCost());
                    }else if("西药".equals(workLoadStat1.getTypes())){
                        workLoadStat.setWestCost(workLoadStat1.getTemporaryCost());
                    }else if("中草药".equals(workLoadStat1.getTypes())){
                        workLoadStat.setChineseCost(workLoadStat1.getTemporaryCost());
                    }else if("材料".equals(workLoadStat1.getTypes())){
                        workLoadStat.setStuffCost(workLoadStat1.getTemporaryCost());
                    }else if("检验".equals(workLoadStat1.getTypes())){
                        workLoadStat.setExaminesCost(workLoadStat1.getTemporaryCost());
                    }else if("检查".equals(workLoadStat1.getTypes())){
                        workLoadStat.setCheckoutCost(workLoadStat1.getTemporaryCost());
                    }else if("理疗".equals(workLoadStat1.getTypes())){
                        workLoadStat.setTherapyCost(workLoadStat1.getTemporaryCost());
                    }else if("治疗".equals(workLoadStat1.getTypes())){
                        workLoadStat.setCureCost(workLoadStat1.getTemporaryCost());
                    }else if("其他".equals(workLoadStat1.getTypes())){
                        workLoadStat.setOtherCost(workLoadStat1.getTemporaryCost());
                    }
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
        List<Ypjxc> list=tollInfoDao.getypjxcmanagementcounts(ypjxcRc);
        int total=list.size();
        List<Ypjxc> list2=null;
        if(total>0){
            list2=tollInfoDao.getypjxcmanagement(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 药品进销存汇总数量和价格
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getypjxcmanagementsums(YpjxcRc ypjxcRc){
        String institution = companyService.getInstitution(ypjxcRc.getCompanyId());
        ypjxcRc.setJgid(institution);
        List<Ypjxc> list=tollInfoDao.getypjxcmanagementcounts(ypjxcRc);
        int total=list.size();
        List<Ypjxc> list2=null;
        if(total>0){
            list2=tollInfoDao.getypjxcmanagementsums(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 获取耗材销售情况
     * @param stuffsalessummaryRc
     * @return
     */
    public Page<Stuffsalessummary> getStuffsalessummarylists(StuffsalessummaryRc stuffsalessummaryRc){
        String institution = companyService.getInstitution(stuffsalessummaryRc.getCompanyId());
        stuffsalessummaryRc.setJgid(institution);
        List<Stuffsalessummary> list=tollInfoDao.getStuffsalessummarytotals(stuffsalessummaryRc);
        int total=list.size();
        List<Stuffsalessummary> list2=null;
        if(total>0){
            list2=tollInfoDao.getStuffsalessummarylist(stuffsalessummaryRc);
        }
        return new Page<>((long) total,list2);
    }

    public Page<Stuffsalessummary> getStuffsalessummarysumss(StuffsalessummaryRc stuffsalessummaryRc){
        String institution = companyService.getInstitution(stuffsalessummaryRc.getCompanyId());
        stuffsalessummaryRc.setJgid(institution);
        List<Stuffsalessummary> list=tollInfoDao.getStuffsalessummarytotals(stuffsalessummaryRc);
        int total=list.size();
        List<Stuffsalessummary> list2=null;
        if(total>0){
            list2=tollInfoDao.getStuffsalessummarysums(stuffsalessummaryRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 材料进销存信息
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getcljxcmanagement(YpjxcRc ypjxcRc){
        String institution = companyService.getInstitution(ypjxcRc.getCompanyId());
        ypjxcRc.setJgid(institution);
        List<Ypjxc> list=tollInfoDao.getcljxcmanagementcounts(ypjxcRc);
        int total=list.size();
        List<Ypjxc> list2=null;
        if(total>0){
            list2=tollInfoDao.getcljxcmanagement(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 材料进销存汇总数量和价格
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getcljxcmanagementsums(YpjxcRc ypjxcRc){
        String institution = companyService.getInstitution(ypjxcRc.getCompanyId());
        ypjxcRc.setJgid(institution);
        List<Ypjxc> list=tollInfoDao.getcljxcmanagementcounts(ypjxcRc);
        int total=list.size();
        List<Ypjxc> list2=null;
        if(total>0){
            list2=tollInfoDao.getcljxcmanagementsums(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 药品材料入库信息统计
     * @param ypjxcRc
     * @return
     */
    public Page<Ypclrkcx> getypclrkcxlist(YpjxcRc ypjxcRc){
        List<Ypclrkcx> list=tollInfoDao.getypclrkcxtotal(ypjxcRc);
        int total=list.size();
        List<Ypclrkcx> list2=null;
        if(total>0){
            list2=tollInfoDao.getypclrkcxlist(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 药品材料入库信息汇总
     * @param ypjxcRc
     * @return
     */
    public Page<Ypclrkcx> getypclrkcxsums(YpjxcRc ypjxcRc){
        List<Ypclrkcx> list=tollInfoDao.getypclrkcxtotal(ypjxcRc);
        int total=list.size();
        List<Ypclrkcx> list2=null;
        if(total>0){
            list2=tollInfoDao.getypclrkcxsums(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 药品进销存信息
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getpharmaceuticalInventoryManagement(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        List<Ypjxc> list=tollInfoDao.getypjxcmanagementcounts(ypjxcRc);
        int total=list.size();
        List<Ypjxc> list2=null;
        if(total>0){
            list2=tollInfoDao.getypjxcmanagement(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }
    /**
     * 机构管理-药品进销存汇总数量和价格
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getpharmaceuticalInventoryManagementsums(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        List<Ypjxc> list=tollInfoDao.getypjxcmanagementcounts(ypjxcRc);
        int total=list.size();
        List<Ypjxc> list2=null;
        if(total>0){
            list2=tollInfoDao.getypjxcmanagementsums(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    public List<Ypjxc> getjglist(YpjxcRc ypjxcRc){
        return tollInfoDao.getjglist(ypjxcRc);
    }

    /**
     * 机构管理-材料进销存信息
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getmaterialmanagement(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        List<Ypjxc> list=tollInfoDao.getcljxcmanagementcounts(ypjxcRc);
        int total=list.size();
        List<Ypjxc> list2=null;
        if(total>0){
            list2=tollInfoDao.getcljxcmanagement(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 机构管理-材料进销存汇总数量和价格
     * @param ypjxcRc
     * @return
     */
    public Page<Ypjxc> getmaterialmanagementsums(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        List<Ypjxc> list=tollInfoDao.getcljxcmanagementcounts(ypjxcRc);
        int total=list.size();
        List<Ypjxc> list2=null;
        if(total>0){
            list2=tollInfoDao.getcljxcmanagementsums(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 机构管理-药品材料入库信息统计
     * @param ypjxcRc
     * @return
     */
    public Page<Ypclrkcx> drugmaterialsstockmanagement(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        List<Ypclrkcx> list=tollInfoDao.getypclrkcxtotal(ypjxcRc);
        int total=list.size();
        List<Ypclrkcx> list2=null;
        if(total>0){
            list2=tollInfoDao.getypclrkcxlist(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 机构管理-药品材料入库信息汇总
     * @param ypjxcRc
     * @return
     */
    public Page<Ypclrkcx> drugmaterialsstockmanagementsums(YpjxcRc ypjxcRc){
        ypjxcRc.setJgzt("1");
        List<Ypclrkcx> list=tollInfoDao.getypclrkcxtotal(ypjxcRc);
        int total=list.size();
        List<Ypclrkcx> list2=null;
        if(total>0){
            list2=tollInfoDao.getypclrkcxsums(ypjxcRc);
        }
        return new Page<>((long) total,list2);
    }

    /**
     * 机构管理-获取耗材销售情况
     * @param stuffsalessummaryRc
     * @return
     */
    public Page<Stuffsalessummary> getconsumablemarketstatistics(StuffsalessummaryRc stuffsalessummaryRc){
        stuffsalessummaryRc.setJgzt("1");
        List<Stuffsalessummary> list=tollInfoDao.getStuffsalessummarytotals(stuffsalessummaryRc);
        int total=list.size();
        List<Stuffsalessummary> list2=null;
        if(total>0){
            list2=tollInfoDao.getStuffsalessummarylist(stuffsalessummaryRc);
        }
        return new Page<>((long) total,list2);
    }

    /*
    机构管理-获取耗材销售信息汇总价格和数量
     */
    public Page<Stuffsalessummary> getconsumablemarketstatisticssum(StuffsalessummaryRc stuffsalessummaryRc){
        stuffsalessummaryRc.setJgzt("1");
        List<Stuffsalessummary> list=tollInfoDao.getStuffsalessummarytotals(stuffsalessummaryRc);
        int total=list.size();
        List<Stuffsalessummary> list2=null;
        if(total>0){
            list2=tollInfoDao.getStuffsalessummarysums(stuffsalessummaryRc);
        }
        return new Page<>((long) total,list2);
    }

    public Page<DrugSales> getdrugmarketstatistics(List<Parameter> params, int offset, int limit, String orderby) {
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

    public DrugSales getdrugmarketstatisticsStat(List<Parameter> params, int offset, int limit, String orderby){
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        DrugSales drugSalesStat = this.dao.getDrugSalesStat(pageRequest);
        return drugSalesStat;
    }


    public Page<WorkLoad> getdoctorDetailstatistics(List<Parameter> params,int offset,int limit, String orderby) {
        List parameters =new ArrayList<Parameter>();
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
                parameters.clear();
                parameters.addAll(params);
                parameters.add(new Parameter("su.name", "=", workLoad.getName()));
                PageRequest pageRequest1 = new PageRequest(parameters, orderby);
                List<WorkLoad> temporaryCosts = this.dao.getTemporaryCost(pageRequest1);
                //System.out.println(temporaryCost.get(1).toString());
                for (WorkLoad temporaryCost:
                        temporaryCosts) {
                    if("中成药".equals(temporaryCost.getTypes())){
                        workLoad.setChinesePatentCost(temporaryCost.getTemporaryCost());
                    }else if("西药".equals(temporaryCost.getTypes())){
                        workLoad.setWestCost(temporaryCost.getTemporaryCost());
                    }else if("中草药".equals(temporaryCost.getTypes())){
                        workLoad.setChineseCost(temporaryCost.getTemporaryCost());
                    }else if("材料".equals(temporaryCost.getTypes())){
                        workLoad.setStuffCost(temporaryCost.getTemporaryCost());
                    }else if("检验".equals(temporaryCost.getTypes())){
                        workLoad.setExaminesCost(temporaryCost.getTemporaryCost());
                    }else if("检查".equals(temporaryCost.getTypes())){
                        workLoad.setCheckoutCost(temporaryCost.getTemporaryCost());
                    }else if("理疗".equals(temporaryCost.getTypes())){
                        workLoad.setTherapyCost(temporaryCost.getTemporaryCost());
                    }else if("治疗".equals(temporaryCost.getTypes())){
                        workLoad.setCureCost(temporaryCost.getTemporaryCost());
                    }else if("其他".equals(temporaryCost.getTypes())){
                        workLoad.setOtherCost(temporaryCost.getTemporaryCost());
                    }
                }
            }
        }

        return new Page((long)total, workloads);
    }

    public WorkLoadStat getdoctorDetailstatisticsStat(List<Parameter> params,int offset,int limit, String orderby){
        List parameters = new ArrayList<Parameter>();
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
            if(temporaryCostStat!=null&&temporaryCostStat.size()>0){
                for (WorkLoadStat workLoadStat1:
                        temporaryCostStat) {
                    if("中成药".equals(workLoadStat1.getTypes())){
                        workLoadStat2.setChinesePatentCost(workLoadStat1.getTemporaryCost());
                    }else if("西药".equals(workLoadStat1.getTypes())){
                        workLoadStat2.setWestCost(workLoadStat1.getTemporaryCost());
                    }else if("中草药".equals(workLoadStat1.getTypes())){
                        workLoadStat2.setChineseCost(workLoadStat1.getTemporaryCost());
                    }else if("材料".equals(workLoadStat1.getTypes())){
                        workLoadStat2.setStuffCost(workLoadStat1.getTemporaryCost());
                    }else if("检验".equals(workLoadStat1.getTypes())){
                        workLoadStat2.setExaminesCost(workLoadStat1.getTemporaryCost());
                    }else if("检查".equals(workLoadStat1.getTypes())){
                        workLoadStat2.setCheckoutCost(workLoadStat1.getTemporaryCost());
                    }else if("理疗".equals(workLoadStat1.getTypes())){
                        workLoadStat2.setTherapyCost(workLoadStat1.getTemporaryCost());
                    }else if("治疗".equals(workLoadStat1.getTypes())){
                        workLoadStat2.setCureCost(workLoadStat1.getTemporaryCost());
                    }else if("其他".equals(workLoadStat1.getTypes())){
                        workLoadStat2.setOtherCost(workLoadStat1.getTemporaryCost());
                    }
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
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("药品销售汇总统计表");
        String fileName = "药品销售汇总统计"+".xls";
        //设置样式
        CellStyle blackStyle = workbook.createCellStyle();
        //自动换行*重要*
        blackStyle.setWrapText(true);
        blackStyle.setAlignment(HorizontalAlignment.CENTER);
        blackStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //新增数据行，并且设置单元格数据
        int rowNum=1;

        // headers表示excel表中第一行的表头 在excel表中添加表头
        String[] headers = {"药品分类","药品名称","规格","数量","总价(元)"};
        HSSFRow row = sheet.createRow(0);


        //设置字体样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        HSSFFont font1 = workbook.createFont();
        font1.setColor(IndexedColors.BLACK.getIndex());
        font1.setBold(false);
        font1.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font1);


        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString hssfRichTextString = new HSSFRichTextString(headers[i]);
            HSSFFont font = workbook.createFont();
            font.setColor(IndexedColors.BLACK.getIndex());//设置excel数据字体颜色
            font.setFontHeightInPoints((short) 14);//设置excel数据字体大小
            font.setBold(true);

            hssfRichTextString.applyFont(font);

            cell.setCellValue(hssfRichTextString);
            cell.setCellStyle(blackStyle);
        }

        row.setHeightInPoints(30);//目的是想把行高设置成30px
        //获取数据库数据
        Page<DrugSales> drugSalesList = getDrugSales(searchParams.getParams(), 0, 1000000, searchParams.getOrder());
        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<5;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(drugSalesList.getRows())){
            for (DrugSales drugSales : drugSalesList.getRows()) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);
                //药品类型
                row1.createCell(0).setCellValue(drugSales.getType());
                map.put(0,Math.max(drugSales.getType().getBytes().length* 256 + 200,map.get(0)));

                //药品名称
                row1.createCell(1).setCellValue(drugSales.getName());
                map.put(1,Math.max(drugSales.getName().getBytes().length*250+200,map.get(1)));

                //规格
                String norms = drugSales.getDosis()+drugSales.getDosisUnit()+"*"+drugSales.getPreparation()+drugSales.getPreparationUnit()+"/"+drugSales.getPack();
                row1.createCell(2).setCellValue(norms);
                map.put(2,Math.max((norms.getBytes().length)*250+200,map.get(2)));

                //数量
                String number = Math.floor(drugSales.getTotal()/Integer.parseInt(drugSales.getPreparation())) >= 0 ? (int)Math.floor(drugSales.getTotal()/Integer.parseInt(drugSales.getPreparation()))+drugSales.getPack()+(drugSales.getTotal()%Integer.parseInt(drugSales.getPreparation())>0?(drugSales.getTotal()%Integer.parseInt(drugSales.getPreparation()))+drugSales.getPreparationUnit():""):drugSales.getTotal()+drugSales.getPreparation();
                row1.createCell(3).setCellValue(number);
                map.put(3,Math.max(number.getBytes().length*250+200,map.get(3)));

                //总价
                BigDecimal convert = BigdecimalConvert.convert(drugSales.getAllFee());
                row1.createCell(4).setCellValue(convert.doubleValue());
                map.put(4,Math.max(convert.toString().getBytes().length*250+200,map.get(4)));


                rowNum++;

                for (int i = 1; i < 5; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }


            }
        }

        for (int i= 0; i<5;i++){
            //设置列宽
            sheet.setColumnWidth(i,map.get(i));
        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

    //医生收入统计导出
    private void exportDoctorDetail(SearchParams searchParams, HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("医生收入统计表");
        String fileName = "医生收入统计"+".xls";
        //设置样式
        CellStyle blackStyle = workbook.createCellStyle();
        //自动换行*重要*
        blackStyle.setWrapText(true);
        blackStyle.setAlignment(HorizontalAlignment.CENTER);
        blackStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //新增数据行，并且设置单元格数据
        int rowNum=1;

        // headers表示excel表中第一行的表头 在excel表中添加表头
        String[] headers = {"医生姓名","接诊次数","总金额(元)","挂号费(元)","西药费(元)","中草药费(元)","中成药费(元)","材料费(元)","检验费(元)","检查费(元)","理疗费(元)","治疗费(元)","其他"};
        HSSFRow row = sheet.createRow(0);


        //设置字体样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        HSSFFont font1 = workbook.createFont();
        font1.setColor(IndexedColors.BLACK.getIndex());
        font1.setBold(false);
        font1.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font1);

        //设置第十列的字体颜色
        CellStyle cellStyle101 = workbook.createCellStyle();
        cellStyle101.setWrapText(true);
        HSSFFont font101 = workbook.createFont();
        font101.setColor(IndexedColors.GREEN.getIndex());
        font101.setBold(false);
        font101.setFontHeightInPoints((short) 11);
        cellStyle101.setFont(font101);

        CellStyle cellStyle102 = workbook.createCellStyle();
        cellStyle102.setWrapText(true);
        HSSFFont font102 = workbook.createFont();
        font102.setColor(IndexedColors.RED.getIndex());
        font102.setBold(false);
        font102.setFontHeightInPoints((short) 11);
        cellStyle102.setFont(font102);



        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString hssfRichTextString = new HSSFRichTextString(headers[i]);
            HSSFFont font = workbook.createFont();
            font.setColor(IndexedColors.BLACK.getIndex());//设置excel数据字体颜色
            font.setFontHeightInPoints((short) 14);//设置excel数据字体大小
            font.setBold(true);

            hssfRichTextString.applyFont(font);

            cell.setCellValue(hssfRichTextString);
            cell.setCellStyle(blackStyle);
        }

        row.setHeightInPoints(30);//目的是想把行高设置成30px
        //获取数据库数据
        Page<WorkLoad> workload = getWorkload(searchParams.getParams(), 0, 1000000, searchParams.getOrder());
        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<13;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(workload.getRows())){
            for (WorkLoad workLoad : workload.getRows()) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);
                //医生姓名
                row1.createCell(0).setCellValue(workLoad.getName());
                map.put(0,Math.max(workLoad.getName().getBytes().length* 256 + 200,map.get(0)));

                //接诊次数
                row1.createCell(1).setCellValue(workLoad.getCount());
                map.put(1,Math.max(workLoad.getCount().getBytes().length*250+200,map.get(1)));

                //总金额
                row1.createCell(2).setCellValue(workLoad.getGrossAmount()!=null?workLoad.getGrossAmount().doubleValue():new BigDecimal("0").doubleValue());
                map.put(2,Math.max(((workLoad.getGrossAmount()!=null?workLoad.getGrossAmount():"0").toString().getBytes().length)*250+200,map.get(2)));

                //挂号费
                row1.createCell(3).setCellValue(workLoad.getRegistrationCost()!=null?workLoad.getRegistrationCost().doubleValue():new BigDecimal("0").doubleValue());
                map.put(3,Math.max((workLoad.getRegistrationCost()!=null?workLoad.getRegistrationCost():"0").toString().getBytes().length*250+200,map.get(3)));

                //西药费
                row1.createCell(4).setCellValue(workLoad.getWestCost()!=null?workLoad.getWestCost().doubleValue():new BigDecimal("0").doubleValue());
                map.put(4,Math.max((workLoad.getWestCost()!=null?workLoad.getWestCost():"0").toString().getBytes().length*250+200,map.get(4)));

                //中药费
                row1.createCell(5).setCellValue(workLoad.getChineseCost()!=null?workLoad.getChineseCost().doubleValue():new BigDecimal("0").doubleValue());
                map.put(5,Math.max(((workLoad.getChineseCost()!=null?workLoad.getChineseCost():"0").toString().getBytes().length)*250+200,map.get(5)));

                //中成药费
                BigDecimal chinesePatentCost = workLoad.getChinesePatentCost() != null ? workLoad.getChinesePatentCost() : new BigDecimal("0");
                row1.createCell(6).setCellValue(chinesePatentCost.doubleValue());
                map.put(6,Math.max(chinesePatentCost.toString().getBytes().length*250+200,map.get(6)));

                //材料费
                BigDecimal stuffCost = workLoad.getStuffCost() != null ? workLoad.getStuffCost() : new BigDecimal("0");
                row1.createCell(7).setCellValue(stuffCost.doubleValue());
                map.put(7,Math.max(stuffCost.toString().getBytes().length*250+200,map.get(7)));

                //检验费
                BigDecimal examinesCost = workLoad.getExaminesCost() != null ? workLoad.getExaminesCost() : new BigDecimal("0");
                row1.createCell(8).setCellValue(examinesCost.doubleValue());
                map.put(8,Math.max(examinesCost.toString().getBytes().length*250+200,map.get(8)));

                //检查费
                BigDecimal checkoutCost = workLoad.getCheckoutCost() != null ? workLoad.getCheckoutCost() : new BigDecimal("0");
                row1.createCell(9).setCellValue(checkoutCost.doubleValue());
                map.put(9,Math.max((checkoutCost.toString().getBytes().length)*250+200,map.get(9)));

                //理疗费
                BigDecimal therapyCost = workLoad.getTherapyCost() != null ? workLoad.getTherapyCost() : new BigDecimal("0");
                row1.createCell(10).setCellValue(therapyCost.doubleValue());
                map.put(10,Math.max((therapyCost.toString().getBytes().length)*250+200,map.get(10)));

                //治疗费
                BigDecimal cureCost = workLoad.getCureCost() != null ? workLoad.getCureCost() : new BigDecimal("0");
                row1.createCell(11).setCellValue(cureCost.doubleValue());
                map.put(11,Math.max((cureCost.toString().getBytes().length)*250 + 200,map.get(11)));

                //其他
                BigDecimal otherCost = workLoad.getOtherCost() != null ? workLoad.getOtherCost() : new BigDecimal("0");
                row1.createCell(12).setCellValue(otherCost.doubleValue());
                map.put(12,Math.max((otherCost.toString().getBytes().length)*256 + 200,map.get(12)));

                rowNum++;

                for (int i = 1; i < 13; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }


                }
        }

        for (int i= 0; i<13;i++){
            //设置列宽
            sheet.setColumnWidth(i,map.get(i));
        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

    @Transactional
    public void exportDrugOrStuffStock(YpjxcRc ypjxcRc, HttpServletResponse response) throws IOException {
            exportDrugStock(ypjxcRc,response);
    }

    private void exportDrugStock(YpjxcRc ypjxcRc, HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = null;
        String fileName = null;
        if(Objects.equals("1",ypjxcRc.getYpcltype())){
            sheet = workbook.createSheet("药品入库统计表");
            fileName = "药品入库统计"+".xls";
        }else {
            sheet = workbook.createSheet("材料入库统计表");
            fileName = "材料入库统计"+".xls";
        }
        //设置样式
        CellStyle blackStyle = workbook.createCellStyle();
        //自动换行*重要*
        blackStyle.setWrapText(true);
        blackStyle.setAlignment(HorizontalAlignment.CENTER);
        blackStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //新增数据行，并且设置单元格数据
        int rowNum=1;

        // headers表示excel表中第一行的表头 在excel表中添加表头
        String[] headers = {"入库日期","入库单号","入库人员","商品编码","商品名称","分类","批号","规格","生产厂家","供应商","数量","单位","零售价(元)","成本价(元)","成本合计(元)","有效期","审核状态","备注"};

        HSSFRow row = sheet.createRow(0);


        //设置字体样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        HSSFFont font1 = workbook.createFont();
        font1.setColor(IndexedColors.BLACK.getIndex());
        font1.setBold(false);
        font1.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font1);


        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString hssfRichTextString = new HSSFRichTextString(headers[i]);
            HSSFFont font = workbook.createFont();
            font.setColor(IndexedColors.BLACK.getIndex());//设置excel数据字体颜色
            font.setFontHeightInPoints((short) 14);//设置excel数据字体大小
            font.setBold(true);

            hssfRichTextString.applyFont(font);

            cell.setCellValue(hssfRichTextString);
            cell.setCellStyle(blackStyle);
        }

        row.setHeightInPoints(30);//目的是想把行高设置成30px
        //获取数据库数据
        //Page<DrugSales> drugSalesList = getDrugSales(searchParams.getParams(), 0, 1000000, searchParams.getOrder());
        Page<Ypclrkcx> getypclrkcxlist = getypclrkcxlist(ypjxcRc);
        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<18;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(getypclrkcxlist.getRows())){
            for (Ypclrkcx ypclrkcx : getypclrkcxlist.getRows()) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);
                //入库日期
                row1.createCell(0).setCellValue(ypclrkcx.getRkrq());
                map.put(0,Math.max(ypclrkcx.getRkrq().getBytes().length* 256 + 200,map.get(0)));

                //入库单号
                row1.createCell(1).setCellValue(ypclrkcx.getRkdh());
                map.put(1,Math.max(ypclrkcx.getRkdh().getBytes().length*250+1200,map.get(1)));

                //入库人员
                row1.createCell(2).setCellValue(ypclrkcx.getRkry());
                map.put(2,Math.max((ypclrkcx.getRkry().getBytes().length)*250+200,map.get(2)));

                //商品编码
                row1.createCell(3).setCellValue(ypclrkcx.getSpbh());
                map.put(3,Math.max(ypclrkcx.getSpbh().getBytes().length*250+200,map.get(3)));

                //商品名称
                row1.createCell(4).setCellValue(ypclrkcx.getSpmc());
                map.put(4,Math.max(ypclrkcx.getSpmc().getBytes().length*250+200,map.get(4)));

                //分类
               if(Objects.equals("1",ypclrkcx.getYpcltype())){
                   String type = Objects.equals("medicalType_0",ypclrkcx.getLx())?"西药":Objects.equals("medicalType_1",ypclrkcx.getLx())?"中草药":"中成药";
                   row1.createCell(5).setCellValue(type);
                   map.put(5,Math.max(type.getBytes().length*256+200,map.get(5)));
               }else {
                   String type = Objects.equals("stuffType_1",ypclrkcx.getLx())?"非医用材料":"医用材料";
                   row1.createCell(5).setCellValue(type);
                   map.put(5,Math.max(type.getBytes().length*256+200,map.get(5)));
               }

                //批号
                row1.createCell(6).setCellValue(ypclrkcx.getPh());
                map.put(6,Math.max((ypclrkcx.getPh()!=null?ypclrkcx.getPh():"").getBytes().length*256+1000,map.get(6)));

                //规格
                row1.createCell(7).setCellValue(ypclrkcx.getGg());
                map.put(7,Math.max(ypclrkcx.getGg().getBytes().length*256+200,map.get(7)));

                //生产厂家
                row1.createCell(8).setCellValue(ypclrkcx.getSccj());
                map.put(8,Math.max((ypclrkcx.getSccj()!=null?ypclrkcx.getSccj():"").getBytes().length*256+200,map.get(8)));

                //供应商
                row1.createCell(9).setCellValue(ypclrkcx.getGys());
                map.put(9,Math.max((ypclrkcx.getGys()!=null?ypclrkcx.getGys():"").getBytes().length*256+200,map.get(9)));

                //数量
                String number = Math.floor(Integer.parseInt(ypclrkcx.getSl())/Integer.parseInt(ypclrkcx.getZj()))>=0?(int)Math.floor(Integer.parseInt(ypclrkcx.getSl())/Integer.parseInt(ypclrkcx.getZj())) + ypclrkcx.getDw() +(Integer.parseInt(ypclrkcx.getSl())%Integer.parseInt(ypclrkcx.getZj())>0?Integer.parseInt(ypclrkcx.getSl())%Integer.parseInt(ypclrkcx.getZj())+ypclrkcx.getZxdw():""):ypclrkcx.getSl()+ypclrkcx.getZxdw();
                row1.createCell(10).setCellValue(number);
                map.put(10,Math.max(number.getBytes().length*256+200,map.get(10)));

                //单位
                row1.createCell(11).setCellValue(ypclrkcx.getDw());
                map.put(11,Math.max((ypclrkcx.getDw()!=null?ypclrkcx.getDw():"").getBytes().length*256+200,map.get(11)));

                //零售价
                BigDecimal selling = new BigDecimal(ypclrkcx.getLsj() != null ? ypclrkcx.getLsj() : "0");
                row1.createCell(12).setCellValue(selling.doubleValue());
                map.put(12,Math.max(selling.toString().getBytes().length*256+200,map.get(12)));

                //成本价
                BigDecimal cost = new BigDecimal(ypclrkcx.getCbj() != null ? ypclrkcx.getCbj() : "0");
                row1.createCell(13).setCellValue(cost.doubleValue());
                map.put(13,Math.max(cost.toString().getBytes().length*256+200,map.get(13)));

                //成本合计
                BigDecimal costTotal = new BigDecimal(ypclrkcx.getCbhj() != null ? ypclrkcx.getCbhj() : "0");
                row1.createCell(14).setCellValue(costTotal.doubleValue());
                map.put(14,Math.max(costTotal.toString().getBytes().length*256+200,map.get(14)));

                //有效期
                row1.createCell(15).setCellValue(ypclrkcx.getYxq()!=null?ypclrkcx.getYxq():"");
                map.put(15,Math.max((ypclrkcx.getYxq()!=null?ypclrkcx.getYxq():"").getBytes().length*256+200,map.get(15)));

                //审核状态
                String status = Objects.equals("supplierStorageExamineStatus_0",ypclrkcx.getShzt())?"通过":"已作废";

                row1.createCell(16).setCellValue(status);
                map.put(16,Math.max(status.getBytes().length*256+200,map.get(16)));

                //备注
                row1.createCell(17).setCellValue(ypclrkcx.getBz());
                map.put(17,Math.max((ypclrkcx.getBz()!=null?ypclrkcx.getBz():"").getBytes().length*256+200,map.get(17)));

                rowNum++;

                for (int i = 1; i < 18; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }


            }
        }

        for (int i= 0; i<18;i++){
            //设置列宽
            sheet.setColumnWidth(i,map.get(i));
        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }
}