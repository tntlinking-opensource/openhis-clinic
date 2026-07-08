package com.geeke.stock.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.geeke.common.constants.BizConstants;
import com.geeke.common.data.Parameter;
import com.geeke.common.data.SearchParamsBuilder;
import com.geeke.common.sequence.service.SequenceService;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.PageRegistration;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.stock.entity.*;
import com.geeke.toll.service.TollInfoService;
import com.geeke.utils.SessionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.common.service.ServiceException;
import com.geeke.stock.dao.InventoryVerificationDao;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

/**
 * 库存盘点Service
 * @author rys
 * @version 2022-11-02
 */
 
@Service("inventoryVerificationService")
@Transactional(readOnly = true)
public class InventoryVerificationService extends CrudService<InventoryVerificationDao, InventoryVerification>{

    private static final Boolean Flage=Boolean.FALSE;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private InventoryVerificationDetailService inventoryVerificationDetailService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private SupplierStockService supplierStockService;

    @Autowired
    private InventoryVerificationRecordService inventoryVerificationRecordService;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Autowired
    private MedicinalStockControlService medicinalStockControlService;

    @Autowired
    private MedicinalStockRecordService medicinalStockRecordService;

    @Transactional
    public  void updateAllPrice(String id, BigDecimal allPrice) {
        this.dao.updateAllPrice(id,allPrice);
    }

    @Transactional(readOnly = false)
    /*保存库存盘点单*/
    public InventoryVerification save(String type,String variety){

        Company company = SessionUtils.getUser().getCompany();

         // 新增盘点时先去判断是否有药品没有发药，需要进行发药或者退药处理
        // 药品不能进行收费操作
        // 医生不能进行完成接诊的操作


        //获取是否存在未发药的药品/材料
        if(InventoryVerificationService.Flage){
            whetherTheDispensing(company);
        }

        //获取是否存在未收费的药品/材料
        if(InventoryVerificationService.Flage){
            //whetherTheDispensing(company);
            whetherChargeOrNot(company);
        }

        InventoryVerification inventoryVerification = new InventoryVerification();

        inventoryVerification.setCompany(company);
        inventoryVerification.setStatus("0");
        inventoryVerification.setType(variety);
        inventoryVerification.setCreateDate(new Date());
        if (StringUtils.isBlank(inventoryVerification.getId())){
            String sn = sequenceService.generate(SessionUtils.getUser().getCompanyId(), "check_inventory_code", inventoryVerification);
            inventoryVerification.setCode(sn);
        }
        InventoryVerification save = super.save(inventoryVerification);

        if(!StringUtils.isNullOrEmpty(save)){
            List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.getAll(company.getId(), type, variety);
            //保存库存盘点详情表
            if(ObjectUtils.equals("0",variety)){
                //盘点所有存在或者不存在库存的药品
                //List<Drug> drugs = drugService.getAll(company.getId(), type);
                inventoryVerificationDetailService.save(save,variety,medicinalStorageControls);
            }else {
//                //获取材料
//                List<Stuff> stuffs = stuffService.getAll(company.getId(),type);
                inventoryVerificationDetailService.saveStuff(save,variety,medicinalStorageControls);
            }
        }

        return save;
    }


    @Transactional(readOnly = false)
    public InventoryVerification accomplishInventoryVerification(InventoryVerification inventoryVerification) {
        int i = this.dao.accomplishInventoryVerification(inventoryVerification);
        //完成盘点后去进行相关的库存增减工作
        logger.debug("Accomplish inventory verification: {}", inventoryVerification);
        if(Objects.equals(inventoryVerification.getType(),"0")){
            //药品进行库存增减
            List<InventoryVerificationDetail> list = inventoryVerificationDetailService.getByInfoId(inventoryVerification.getId());
            for (InventoryVerificationDetail inventoryVerificationDetail : list) {
                List<Parameter> parameters = SearchParamsBuilder.create()
                        .eq("id", inventoryVerificationDetail.getMedicinalStorageControl().getId())
                        .build();
//                parameters.add(new Parameter("storage_stock-used_stock-reimburse_stock",">",0));
//                List<SupplierStock> supplierStocks = supplierStockService.listAll(parameters, "a.create_date");
                List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(parameters, "a.create_date");
                //循环增减库存（正值增加，负值扣减）
                adjustStock(medicinalStorageControls, inventoryVerificationDetail.getProfitAndLoss(), inventoryVerificationDetail);
            }
        }else {
            //材料进行库存增减
            List<InventoryVerificationDetail> list = inventoryVerificationDetailService.getByInfoId(inventoryVerification.getId());
            for (InventoryVerificationDetail inventoryVerificationDetail : list) {
                List<Parameter> parameters = SearchParamsBuilder.create()
                        .eq("id", inventoryVerificationDetail.getMedicinalStorageControl().getId())
                        .build();
                List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(parameters, "a.create_date");
//                parameters.add(new Parameter("stuff_id","=",inventoryVerificationDetail.getStuff().getId()));
//                parameters.add(new Parameter("number",">",0));
//                List<SupplierStock> supplierStocks = supplierStockService.listAll(parameters, "a.create_date");
                //循环增减库存（正值增加，负值扣减）
                adjustStock(medicinalStorageControls, inventoryVerificationDetail.getProfitAndLoss(), inventoryVerificationDetail);
            }
        }
        return inventoryVerification;
    }

    /**
     * 调整库存（正值增加，负值扣减）
     */
    private void adjustStock(List<MedicinalStorageControl> medicinalStorageControls, Integer profitAndLoss, InventoryVerificationDetail inventoryVerificationDetail) {
        if (CollectionUtils.isEmpty(medicinalStorageControls)) {
            return;
        }
        MedicinalStorageControl medicinalStorageControl = medicinalStorageControls.get(0);
        BigDecimal newStock = medicinalStorageControl.getStorageStock().add(new BigDecimal(profitAndLoss));
        medicinalStorageControl.setStorageStock(newStock);
        medicinalStorageControl.setSurplusStock(newStock);
        medicinalStorageControlService.save(medicinalStorageControl);

        //查询动态库存总控制表
        List<Parameter> parameters = SearchParamsBuilder.create()
                .eq("drug_stuff_id", medicinalStorageControl.getDrug().getId())
                .build();
        List<MedicinalStockControl> medicinalStockControls = medicinalStockControlService.listAll(parameters, "");
        if (CollectionUtils.isEmpty(medicinalStockControls)) {
            return;
        }

        //动态库存总控制表修改
        MedicinalStockControl medicinalStockControl = medicinalStockControls.get(0);
        BigDecimal newControlStock = medicinalStockControl.getStorageStock().add(new BigDecimal(profitAndLoss));
        medicinalStockControl.setStorageStock(newControlStock);
        medicinalStockControl.setSurplusStock(newControlStock);
        medicinalStockControlService.save(medicinalStockControl);

        //库存操作记录
        MedicinalStockRecord medicinalStockRecord = new MedicinalStockRecord();
        medicinalStockRecord.setOperationType(5);
        medicinalStockRecord.setOperationStock(new BigDecimal(profitAndLoss + ""));
        medicinalStockRecord.setCompany(medicinalStorageControl.getCompany());
        medicinalStockRecord.setDrugStuffId(medicinalStorageControl.getDrugStuffId());
        medicinalStockRecord.setStorageId(medicinalStorageControl.getStorageId());
        if (inventoryVerificationDetail.getDrug() != null && Objects.equals("", inventoryVerificationDetail.getDrug().getId())) {
            medicinalStockRecord.setType(1);
        } else {
            medicinalStockRecord.setType(2);
        }
        medicinalStockRecord.setDrugStuffName(medicinalStorageControl.getDrugStuffName());
        medicinalStockRecordService.save(medicinalStockRecord);

        //盘点库存操作表保存
        InventoryVerificationRecord inventoryVerificationRecord = new InventoryVerificationRecord();
        inventoryVerificationRecord.setCompany(medicinalStorageControl.getCompany());
        SupplierStock supplierStock = new SupplierStock();
        supplierStock.setId(medicinalStorageControl.getStorageId());
        inventoryVerificationRecord.setSupplierStock(supplierStock);
        inventoryVerificationRecord.setNumber(profitAndLoss);
        inventoryVerificationRecordService.save(inventoryVerificationRecord);
    }

    //判断是否存在药品没有发药
    public void whetherTheDispensing(Company company){
        PageRegistration pageRegistration = new PageRegistration();
        pageRegistration.setCompanyId(company.getId());
        pageRegistration.setChargeStatus(1);
        pageRegistration.setColumnName("charge_date");
        pageRegistration.setDispensionStatus(0);
        pageRegistration.setStatus(BizConstants.REG_STATUS_VISITED);
        pageRegistration.setRecipelType(BizConstants.RECIPEL_TYPE_OTHER);
        pageRegistration.setRecipeStatus("0");
        String dateTime="2022-06-01 00:00:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = simpleDateFormat.parse(dateTime);
            pageRegistration.setUpdateDate(parse);
        } catch (ParseException e) {
            logger.warn("解析日期失败: {}", dateTime, e);
        }

        List<String> strings = registrationService.whetherTheDispensing(pageRegistration);
        if(!CollectionUtils.isEmpty(strings)){
            throw new ServiceException("存在药品/材料还未发药，不能进行盘点!");
        }

    }

    //判断是否存在未收费的
    public void whetherChargeOrNot(Company company) {
        PageRegistration pageRegistration = new PageRegistration();
        pageRegistration.setCompanyId(company.getId());
        pageRegistration.setColumnName("reception_end_date");
        pageRegistration.setDispensionStatus(3);
        pageRegistration.setStatus(BizConstants.REG_STATUS_VISITED);
        pageRegistration.setChargeStatus(0);
        pageRegistration.setRecipeStatus("0");
        String dateTime="2022-06-01 00:00:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = simpleDateFormat.parse(dateTime);
            pageRegistration.setUpdateDate(parse);
        } catch (ParseException e) {
            logger.warn("解析日期失败: {}", dateTime, e);
        }
        List<String> strings = registrationService.whetherChargeOrNot(pageRegistration);
        if(!CollectionUtils.isEmpty(strings)){
            throw new ServiceException("存在药品/材料还未收费，不能进行盘点!");
        }
    }

    public List<InventoryVerification> getByCompanyId(String companyId) {
        List<InventoryVerification> inventoryVerifications = this.dao.getByCompanyId(companyId,"0");
        return inventoryVerifications;
    }
}