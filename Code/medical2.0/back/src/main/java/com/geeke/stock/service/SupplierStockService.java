package com.geeke.stock.service;
import com.geeke.common.constants.BizConstants;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.data.SearchParamsBuilder;
import com.geeke.common.service.CrudService;
import com.geeke.common.service.ServiceException;
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
import com.geeke.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    @Lazy
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private SupplierStorageService supplierStorageService;

    @Lazy
    @Autowired
    private RecipelInfoService recipelInfoService;

    @Autowired
    private StuffService stuffService;

    @Lazy
    @Autowired
    private RecipelDetailService recipelDetailService;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Lazy
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
                throw new ServiceException("正在进行盘点，无法进行发药操作!");

            }
        } else {
            if (!CollectionUtils.isEmpty(inventoryVerifications)) {
                throw new ServiceException("正在进行盘点，无法进行退药操作!");

            }
        }
        String registrationId = dispensingEvt.getRegistrationId();
        Registration registration = registrationService.get(registrationId);
        if ("0".equals(dispensingEvt.getDispensingType())) {

            //发药
            for (String recipelInfoId : dispensingEvt.getRecipelInfoIdList()) {
                //进行动态库存实占
                medicinalStorageControlService.okOccupyStock(registrationId, recipelInfoId);
            }


            //更新处方状态
            List<String> recipelInfoIdList = dispensingEvt.getRecipelInfoIdList();
            if (CollectionUtils.isEmpty(recipelInfoIdList)) {
                throw new ServiceException("发药的处方信息不能为空");
            }

            for (String recipelInfoId : recipelInfoIdList) {
                RecipelInfo recipelInfo = this.recipelInfoService.get(recipelInfoId);
                if (recipelInfo == null || !"0".equals(recipelInfo.getIsDispension())) {
                    throw new ServiceException("处方信息校验失败，不存在或已发药过");
                }
                recipelInfo.setIsDispension("1");
                recipelInfo.setDispensionDate(new Date());
                recipelInfo.setDispensionStatus(1);
                this.recipelInfoService.save(recipelInfo);
            }


            registration.setDispensingDate(new Date());
        } else {
            //退药
            List<Parameter> parameters = SearchParamsBuilder.create()
                    .eq("registration_id", registrationId)
                    .in("recipel_info_id", dispensingEvt.getRecipelInfoIdList())
                    .build();
            List<Dispensing> dispensingList = dispensingService.listAll(parameters, "");
            if (null != dispensingList && !dispensingList.isEmpty()) {
            }
            //更新处方状态
            List<String> recipelInfoIdList = dispensingEvt.getRecipelInfoIdList();
            if (CollectionUtils.isEmpty(recipelInfoIdList)) {
                throw new ServiceException("发药的处方信息不能为空");
            }
            for (String recipelInfoId : recipelInfoIdList) {
                RecipelInfo recipelInfo = this.recipelInfoService.get(recipelInfoId);
                if (recipelInfo == null || !"1".equals(recipelInfo.getIsDispension())) {
                    throw new ServiceException("处方信息校验失败，不存在或已退药过");
                }
                if (BizConstants.RECIPEL_TYPE_INFUSION.equals(recipelInfo.getRecipelType().getValue())) {
                    List<RecipelDetail> recipelDetails = recipelDetailService.getByRecipelInfoId(recipelInfo.getId());
                    for (RecipelDetail recipelDetail :
                            recipelDetails) {
                        if (recipelDetail.getExecutions() != null) {
                            throw new ServiceException("输液处方已执行，无法退费！");
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
//                        drugService.updateInventory_3502(entity.getNumber(),entity.getDrug().getId());
//                    }else if (entity.getStuff().getId()!=null){
//                        stuffService.updateInventory_3502(entity.getNumber(),entity.getStuff().getId());
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
        return paginate(
            () -> this.dao.countWarning(pageRequest),
            () -> {
                List<SupplierStock> list = this.dao.getDrugIndateWarning(pageRequest);
                for (SupplierStock supplierStock : list) {
                    if (!Objects.isNull(supplierStock.getMedicinalStorageControl()) && !Objects.isNull(supplierStock.getMedicinalStorageControl().getStorageStock())) {
                        String number =
                                supplierStock.getMedicinalStorageControl().getStorageStock().subtract(supplierStock.getMedicinalStorageControl().getUsedStock().add(supplierStock.getMedicinalStorageControl().getReimburseStock())).stripTrailingZeros().toPlainString();
                        supplierStock.setNumber(Integer.parseInt(number));
                    } else {
                        supplierStock.setNumber(0);
                    }
                }
                return list;
            }
        );
    }

    public Page<SupplierStock> getStuffIndateWarning(List<Parameter> parameters, int offset, int limit,
                                                     String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        return paginate(
            () -> this.dao.countStuffWarning(pageRequest),
            () -> {
                List<SupplierStock> list = this.dao.getStuffIndateWarning(pageRequest);
                for (SupplierStock supplierStock : list) {
                    if (!Objects.isNull(supplierStock.getMedicinalStorageControl()) && !Objects.isNull(supplierStock.getMedicinalStorageControl().getStorageStock())) {
                        String number =
                                supplierStock.getMedicinalStorageControl().getStorageStock().subtract(supplierStock.getMedicinalStorageControl().getUsedStock().add(supplierStock.getMedicinalStorageControl().getReimburseStock())).stripTrailingZeros().toPlainString();
                        supplierStock.setNumber(Integer.parseInt(number));
                    } else {
                        supplierStock.setNumber(0);
                    }
                }
                return list;
            }
        );
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
            if (storage.getExamine().getValue().equalsIgnoreCase(BizConstants.SUPPLIER_STORAGE_EXAMINE_PENDING)) {
                storage.setExamine(examine);
                storage.setExamineDate(new Date());
                supplierStorageService.save(storage);
                //动态库存控制
                this.medicinalStorageControlService.addStorageStock(storage);
            }
        });
    }
}