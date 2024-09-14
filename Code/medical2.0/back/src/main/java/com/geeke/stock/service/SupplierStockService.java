package com.geeke.stock.service;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.RecipelDetailService;
import com.geeke.outpatient.service.RecipelInfoService;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.stock.dao.MedicinalStockControlDao;
import com.geeke.stock.dao.SupplierStockDao;
import com.geeke.stock.entity.*;
import com.geeke.sys.entity.DictItem;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 供应商库存Service
 *
 * @author txl
 * @version 2022-06-09
 */

@Service("supplierStockService")
@Transactional(readOnly = true)
public class SupplierStockService extends CrudService<SupplierStockDao, SupplierStock> {

    @Autowired
    private DispensingService dispensingService;

    @Autowired
    private SupplierStockService supplierStockService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private SupplierStorageService supplierStorageService;

    @Autowired
    private RecipelInfoService recipelInfoService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private RecipelDetailService recipelDetailService;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Autowired
    private InventoryVerificationService inventoryVerificationService;

    @Autowired
    private MedicinalStockControlDao medicinalStockControlDao;

    @Autowired
    private MedicinalStockControlService medicinalStockControlService;
    @Autowired
    private CompanyService companyService;
    @Transactional(readOnly = false)
    public void updateStock(DispensingEvt dispensingEvt) {

        Company company = SessionUtils.getUser().getCompany();
        //正在进行盘点时不能进行发药操作
        List<InventoryVerification> inventoryVerifications =
                inventoryVerificationService.getByCompanyId(company.getId());
        if ("0".equals(dispensingEvt.getDispensingType())) {
            if (!CollectionUtils.isEmpty(inventoryVerifications)) {
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "正在进行盘点，无法进行发药操作!"));

            }
        } else {
            if (!CollectionUtils.isEmpty(inventoryVerifications)) {
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "正在进行盘点，无法进行退药操作!"));

            }
        }
        List<Parameter> parameters = new ArrayList<>();
        String registrationId = dispensingEvt.getRegistrationId();
        Registration registration = registrationService.get(registrationId);
        if ("0".equals(dispensingEvt.getDispensingType())) {

            //发药
//            for (DispensingDetailEvt dispensingDetailEvt : dispensingEvt.getDispensingDetailEvtList()) {
//                parameters.clear();
//                if ("0".equals(dispensingDetailEvt.getType())) {
//                    //药品发放
//                    parameters.add(new Parameter("drug_id", "=", dispensingDetailEvt.getId()));
//                } else {
//                    //材料发放
//                    parameters.add(new Parameter("stuff_id", "=", dispensingDetailEvt.getId()));
//                }
//                parameters.add(new Parameter("number" , ">", 0));
//                List<SupplierStock> supplierStockList = supplierStockService.listAll(parameters, "a.create_date");
//                Integer number = dispensingDetailEvt.getNumber();
//                //减库存
//                //stockReduction(registrationId, dispensingDetailEvt.getRecipelInfoId(), supplierStockList, number);
//            }
            for (String recipelInfoId : dispensingEvt.getRecipelInfoIdList()) {
                //进行动态库存实占
                medicinalStorageControlService.okOccupyStock(registrationId, recipelInfoId);
            }


            //更新处方状态
            List<String> recipelInfoIdList = dispensingEvt.getRecipelInfoIdList();
            if (CollectionUtils.isEmpty(recipelInfoIdList)) {
                throw new RuntimeException("发药的处方信息不能为空");
            }

            for (String recipelInfoId : recipelInfoIdList) {
                RecipelInfo recipelInfo = this.recipelInfoService.get(recipelInfoId);
                if (recipelInfo == null || !"0".equals(recipelInfo.getIsDispension())) {
                    throw new RuntimeException("处方信息校验失败，不存在或已发药过");
                }
                recipelInfo.setIsDispension("1");
                recipelInfo.setDispensionDate(new Date());
                recipelInfo.setDispensionStatus(1);
                this.recipelInfoService.save(recipelInfo);
            }


            registration.setDispensingDate(new Date());
        } else {
            //退药
            parameters.add(new Parameter("registration_id", "=", registrationId));
            if (null != dispensingEvt.getRecipelInfoIdList() && !dispensingEvt.getRecipelInfoIdList().isEmpty()) {
                parameters.add(new Parameter("recipel_info_id", "in", dispensingEvt.getRecipelInfoIdList()));
            }
            List<Dispensing> dispensingList = dispensingService.listAll(parameters, "");
            if (null != dispensingList && !dispensingList.isEmpty()) {
//                for (Dispensing dispensing : dispensingList) {
//                    String supplierStockId = dispensing.getSupplierStock().getId();
//                    Integer updateNumber = dispensing.getNumber();
//                    SupplierStock supplierStock = supplierStockService.get(supplierStockId);
//                    Integer currentNumber = supplierStock.getNumber();
//                    supplierStock.setNumber(currentNumber + updateNumber);
//                    supplierStockService.save(supplierStock);
//                    if(supplierStock.getDrug().getId()!=null){
//                        drugService.updateInventory(updateNumber,supplierStock.getDrug().getId());
//                    }else {
//                        stuffService.updateInventory(updateNumber,supplierStock.getStuff().getId());
//                    }
//                }
            }
            //更新处方状态
            List<String> recipelInfoIdList = dispensingEvt.getRecipelInfoIdList();
            if (CollectionUtils.isEmpty(recipelInfoIdList)) {
                throw new RuntimeException("发药的处方信息不能为空");
            }
            for (String recipelInfoId : recipelInfoIdList) {
                RecipelInfo recipelInfo = this.recipelInfoService.get(recipelInfoId);
                if (recipelInfo == null || !"1".equals(recipelInfo.getIsDispension())) {
                    throw new RuntimeException("处方信息校验失败，不存在或已退药过");
                }
                if ("recipelType_2".equals(recipelInfo.getRecipelType().getValue())) {
                    List<RecipelDetail> recipelDetails = recipelDetailService.getByRecipelInfoId(recipelInfo.getId());
                    for (RecipelDetail recipelDetail :
                            recipelDetails) {
                        if (recipelDetail.getExecutions() != null) {
                            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "输液处方已执行，无法退费！"));
                        }
                    }
                }
                //退药暂不退费
                medicinalStorageControlService.drugRepercussion(recipelInfo);

                recipelInfo.setIsDispension("2");
                recipelInfo.setDispensionStatus(-1);
                recipelInfo.setRetreatChargeDate(new Date());
                this.recipelInfoService.save(recipelInfo);
                //修改发药信息表
                dispensingService.updateDelFlag(recipelInfoId);
            }
            registration.setReturnDate(new Date());
        }
        //修改发药状态
        registration.setDispensingStatus(dispensingEvt.getDispensingStatus());
        //registration.setDispensingDate(new Date());
        registrationService.save(registration);
    }

    //循环减库存

    private void stockReduction(String registrationId, String recipelInfoId, List<SupplierStock> supplierStockList,
                                Integer number) {
        if (null != supplierStockList && !supplierStockList.isEmpty()) {
            Dispensing dispensing = new Dispensing();
            Registration registration = new Registration();
            registration.setId(registrationId);
            dispensing.setRegistration(registration);
            RecipelInfo recipelInfo = new RecipelInfo();
            recipelInfo.setId(recipelInfoId);
            dispensing.setRecipelInfo(recipelInfo);
            for (SupplierStock supplierStock : supplierStockList) {
                Integer stockNumber = supplierStock.getNumber();
                if (number > 0 && number <= stockNumber) {
                    int initalNumber = number;
                    //修改库存数
                    supplierStock.setNumber(stockNumber - initalNumber);
                    supplierStockService.save(supplierStock);
                    //修改药品或者材料信息表的库存
                    if (supplierStock.getDrug().getId() != null) {
                        drugService.updateInventory(0 - initalNumber, supplierStock.getDrug().getId());
                    } else {
                        stuffService.updateInventory(0 - initalNumber, supplierStock.getStuff().getId());
                    }
                    number = 0;
                    //保存发药明细表
                    dispensing.setId("");
                    dispensing.setSupplierStock(supplierStock);
                    dispensing.setNumber(initalNumber);
                    dispensing.setCompany(supplierStock.getCompany());
                    String id = dispensingService.save(dispensing).getId();
                    break;
                } else if (number > 0 && number >= stockNumber) {
                    number = number - stockNumber;
                    //修改药品或者材料信息表的库存
                    if (supplierStock.getDrug().getId() != null) {
                        drugService.updateInventory(-stockNumber, supplierStock.getDrug().getId());
                    } else {
                        stuffService.updateInventory(-stockNumber, supplierStock.getStuff().getId());
                    }
                    //修改库存数
                    supplierStock.setNumber(0);
                    supplierStockService.save(supplierStock);

                    //保存发药明细表
                    dispensing.setId("");
                    dispensing.setSupplierStock(supplierStock);
                    dispensing.setNumber(stockNumber);
                    dispensing.setCompany(supplierStock.getCompany());
                    String id = dispensingService.save(dispensing).getId();
                }
            }
            if (number > 0) {
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "库存不足，请补充库存!或者进行退费和作废处方。"));
            }
        }
    }

    public List<SupplierStock> getByStorageId(String id) {
        return this.dao.getByStorageId(id);
    }

    @Transactional(readOnly = false)
    public void saves(StorageEvt storageEvt) {
        // 新增药品或材料库存时，如果为是初始化库存则先初始化库存总控制记录数据
        for (SupplierStock entity : storageEvt.getSupplierStockList()) {
            String companyID = entity.getCompany().getId();
            String drugID = entity.getDrug().getId();
            // String drug_stuff_ID = drugID != null ? drugID : stuffID;

            if (null != drugID) {
                List<MedicinalStockControl> inventory = medicinalStockControlDao.inventory(companyID, drugID);
                if (inventory.isEmpty()) {
                    Drug drugTemp = drugService.get(drugID);
                    drugTemp.getCompany().setId(companyID);
                    this.medicinalStockControlService.initStockTo(drugTemp);
                }
            }
            String stuffID = entity.getStuff().getId();
            if (null != stuffID) {
                List<MedicinalStockControl> inventory = medicinalStockControlDao.inventory(companyID, stuffID);
                if (inventory.isEmpty()) {
                    Stuff drugTemp = stuffService.get(stuffID);
                    drugTemp.getCompany().setId(companyID);
                    this.medicinalStockControlService.initStockTo(drugTemp);
                }
            }
        }
    }

    //村入库单和库存
    @Transactional(readOnly = false)
    public void savesTo(StorageEvt storageEvt) {
        //先保存入库单
        SupplierStorage supplierStorage = storageEvt.getSupplierStorage();
        supplierStorage.setInitial(supplierStorage.getNumber());
        SupplierStorage save = supplierStorageService.save(supplierStorage);
        if (null != storageEvt.getSupplierStockList() && !storageEvt.getSupplierStockList().isEmpty()) {
            for (SupplierStock entity : storageEvt.getSupplierStockList()) {


                //将入库单号存入每个入库表中
                entity.setSupplierStorage(save);
                entity.setInitial(entity.getNumber());
                entity.setCancellation("0");
                String id = supplierStockService.save(entity).getId();
                //存入成功后将该物品的库存存入到对应的数据库中
//                if(id!=null){
//                    if(entity.getDrug().getId()!=null){
//                        //则获取对应的药品库存
//                        drugService.updateInventory(entity.getNumber(),entity.getDrug().getId());
//                    }else if (entity.getStuff().getId()!=null){
//                        stuffService.updateInventory(entity.getNumber(),entity.getStuff().getId());
//                    }
//                }
            }

            //动态库存控制
            this.medicinalStorageControlService.addStorageStock(save);
        }
    }


    /*
     * 获取入库药品的平均进价*/
    @Transactional
    public BigDecimal getByDrugId(String drugOrStuffId) {
        BigDecimal byDrugOrStuffId = this.dao.getByDrugId(drugOrStuffId);

        return byDrugOrStuffId;
    }

    @Transactional(readOnly = false)
    public void cancellation(List<SupplierStock> supplierStockList) {
        this.dao.updateCancel(supplierStockList);
    }

    public BigDecimal getByStuffId(String stuffId) {
        BigDecimal supplierStocDecimal = this.dao.getByStuffId(stuffId);
        return supplierStocDecimal;
    }

    @Transactional
    public List<SupplierStock> getByStuffIdDetail(String drugStuffId) {
        List<SupplierStock> supplierStockList = this.dao.getByStuffIdDetail(drugStuffId);
        return supplierStockList;
    }

    public Page<SupplierStock> getDrugIndateWarning(List<Parameter> parameters, int offset, int limit, String
            orderby) {
        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);

        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby, id, institution);
        int total = this.dao.countWarning(pageRequest);
        List<SupplierStock> list = null;
        if (total > 0) {
            list = this.dao.getDrugIndateWarning(pageRequest);
        }
        if (!CollectionUtils.isEmpty(list)) {
            for (SupplierStock supplierStock : list) {
                if (!Objects.isNull(supplierStock.getMedicinalStorageControl()) && !Objects.isNull(supplierStock.getMedicinalStorageControl().getStorageStock())) {
                    String number =
                            supplierStock.getMedicinalStorageControl().getStorageStock().subtract(supplierStock.getMedicinalStorageControl().getUsedStock().add(supplierStock.getMedicinalStorageControl().getReimburseStock())).stripTrailingZeros().toPlainString();
                    supplierStock.setNumber(Integer.parseInt(number));
                } else {
                    supplierStock.setNumber(0);
                }
            }

        }
        return new Page((long) total, list);
    }

    public Page<SupplierStock> getStuffIndateWarning(List<Parameter> parameters, int offset, int limit,
                                                     String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        int total = this.dao.countStuffWarning(pageRequest);
        List<SupplierStock> list = null;
        if (total > 0) {
            list = this.dao.getStuffIndateWarning(pageRequest);
        }
        if (!CollectionUtils.isEmpty(list)) {
            for (SupplierStock supplierStock : list) {
                if (!Objects.isNull(supplierStock.getMedicinalStorageControl()) && !Objects.isNull(supplierStock.getMedicinalStorageControl().getStorageStock())) {
                    String number =
                            supplierStock.getMedicinalStorageControl().getStorageStock().subtract(supplierStock.getMedicinalStorageControl().getUsedStock().add(supplierStock.getMedicinalStorageControl().getReimburseStock())).stripTrailingZeros().toPlainString();
                    supplierStock.setNumber(Integer.parseInt(number));
                } else {
                    supplierStock.setNumber(0);
                }
            }
        }
        return new Page((long) total, list);
    }

    /**
     * 入库待审核
     *
     * @param storageEvt
     */
    @Transactional
    public void savesToAudit(StorageEvt storageEvt) {
        //先保存入库单
        SupplierStorage supplierStorage = storageEvt.getSupplierStorage();
        supplierStorage.setInitial(supplierStorage.getNumber());
        SupplierStorage save = supplierStorageService.save(supplierStorage);


        if (null != storageEvt.getSupplierStockList() && !storageEvt.getSupplierStockList().isEmpty()) {
            for (SupplierStock entity : storageEvt.getSupplierStockList()) {

                //将入库单号存入每个入库表中
                entity.setSupplierStorage(save);
                entity.setInitial(entity.getNumber());
                entity.setCancellation("0");
                supplierStockService.save(entity);

            }
        }

    }

    /**
     * 审核通过并入库
     *
     * @param storageEvt
     */

    @Transactional
    public void auditStorage(StorageEvt storageEvt) {
        DictItem examine = storageEvt.getSupplierStorage().getExamine();
        storageEvt.getSupplierStockList().forEach(stock -> {
            SupplierStorage storage = supplierStorageService.get(stock.getId());
            if (storage.getExamine().getValue().equalsIgnoreCase("supplierStorageExamineStatus_3")) {
                storage.setExamine(examine);
                storage.setExamineDate(new Date());
                supplierStorageService.save(storage);
                //动态库存控制
                this.medicinalStorageControlService.addStorageStock(storage);
            }
        });
    }
}