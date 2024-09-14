package com.geeke.stock.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.geeke.common.data.Parameter;
import com.geeke.common.sequence.service.SequenceService;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.PageRegistration;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.stock.entity.*;
import com.geeke.toll.service.TollInfoService;
import com.geeke.utils.SessionUtils;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.commons.lang.ObjectUtils;
import org.apache.xmlbeans.impl.jam.mutable.MMember;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.stock.dao.InventoryVerificationDao;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

/**
 * 库存盘点Service
 * @author rys
 * @version 2022-11-02
 */
 
@Service("inventoryVerificationService")
@Transactional(readOnly = false)
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
        System.out.println(inventoryVerification);
        if(Objects.equals(inventoryVerification.getType(),"0")){
            //药品进行库存增减
            List<InventoryVerificationDetail> list = inventoryVerificationDetailService.getByInfoId(inventoryVerification.getId());
            for (InventoryVerificationDetail inventoryVerificationDetail : list) {
                List<Parameter> parameters = new ArrayList<>();
                parameters.add(new Parameter("id","=",inventoryVerificationDetail.getMedicinalStorageControl().getId()));
//                parameters.add(new Parameter("storage_stock-used_stock-reimburse_stock",">",0));
//                List<SupplierStock> supplierStocks = supplierStockService.listAll(parameters, "a.create_date");
                List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(parameters, "a.create_date");
                //循环增减库存
                if(inventoryVerificationDetail.getProfitAndLoss()>0){
                    //进行增加库存
                    addStock(medicinalStorageControls,inventoryVerificationDetail.getProfitAndLoss(),inventoryVerificationDetail);
                }else {
                    //进行扣减库存
                    stockReduction(medicinalStorageControls,inventoryVerificationDetail.getProfitAndLoss(),inventoryVerificationDetail);
                }
            }
        }else {
            //材料进行库存增减
            List<InventoryVerificationDetail> list = inventoryVerificationDetailService.getByInfoId(inventoryVerification.getId());
            for (InventoryVerificationDetail inventoryVerificationDetail : list) {
                List<Parameter> parameters = new ArrayList<>();
                parameters.add(new Parameter("id","=",inventoryVerificationDetail.getMedicinalStorageControl().getId()));
                List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(parameters, "a.create_date");
//                parameters.add(new Parameter("stuff_id","=",inventoryVerificationDetail.getStuff().getId()));
//                parameters.add(new Parameter("number",">",0));
//                List<SupplierStock> supplierStocks = supplierStockService.listAll(parameters, "a.create_date");
                //循环增减库存
                if(inventoryVerificationDetail.getProfitAndLoss()>0){
                    //进行增加库存
                    addStock(medicinalStorageControls,inventoryVerificationDetail.getProfitAndLoss(),inventoryVerificationDetail);
                }else {
                    //进行扣减库存
                    stockReduction(medicinalStorageControls,inventoryVerificationDetail.getProfitAndLoss(),inventoryVerificationDetail);
                }
            }
        }
        return inventoryVerification;
    }

    private void addStock(List<MedicinalStorageControl> medicinalStorageControls, Integer profitAndLoss,InventoryVerificationDetail inventoryVerificationDetail) {
        if(!CollectionUtils.isEmpty(medicinalStorageControls)){
            MedicinalStorageControl medicinalStorageControl = medicinalStorageControls.get(0);
            BigDecimal add = medicinalStorageControl.getStorageStock().add(new BigDecimal(profitAndLoss));
            medicinalStorageControl.setStorageStock(add);
            medicinalStorageControl.setSurplusStock(add);
            medicinalStorageControlService.save(medicinalStorageControl);

            //将动态库存总控制表进行库存增加
            ArrayList<Parameter> parameters = new ArrayList<>();
            parameters.add(new Parameter("drug_stuff_id","=",medicinalStorageControl.getDrug().getId()));
            List<MedicinalStockControl> medicinalStockControls = medicinalStockControlService.listAll(parameters, "");
            if(CollectionUtils.isEmpty(medicinalStorageControls)){
                return;
            }

            //库存操作记录
            MedicinalStockRecord medicinalStockRecord = new MedicinalStockRecord();
            medicinalStockRecord.setOperationType(5);
            medicinalStockRecord.setOperationStock(new BigDecimal(profitAndLoss+""));
            medicinalStockRecord.setCompany(medicinalStorageControl.getCompany());
            medicinalStockRecord.setDrugStuffId(medicinalStorageControl.getDrugStuffId());
            medicinalStockRecord.setStorageId(medicinalStorageControl.getStorageId());
            if(inventoryVerificationDetail.getDrug()!=null&&Objects.equals("",inventoryVerificationDetail.getDrug().getId())){
                medicinalStockRecord.setType(1);
            }else {
                medicinalStockRecord.setType(2);
            }
            medicinalStockRecord.setDrugStuffName(medicinalStorageControl.getDrugStuffName());
            medicinalStockRecordService.save(medicinalStockRecord);


            //动态库存总控制表修改
            MedicinalStockControl medicinalStockControl = medicinalStockControls.get(0);
            BigDecimal add1 = medicinalStockControl.getStorageStock().add(new BigDecimal(profitAndLoss));
            medicinalStockControl.setStorageStock(add1);
            medicinalStockControl.setSurplusStock(add1);
            medicinalStockControlService.save(medicinalStockControl);

            InventoryVerificationRecord inventoryVerificationRecord = new InventoryVerificationRecord();
            inventoryVerificationRecord.setCompany(medicinalStorageControl.getCompany());
            SupplierStock supplierStock = new SupplierStock();
            supplierStock.setId(medicinalStorageControl.getStorageId());
            inventoryVerificationRecord.setSupplierStock(supplierStock);
            inventoryVerificationRecord.setNumber(profitAndLoss);
            inventoryVerificationRecordService.save(inventoryVerificationRecord);
        }
    }

    //循环扣减库存
    private void stockReduction(List<MedicinalStorageControl> medicinalStorageControls, Integer profitAndLoss,InventoryVerificationDetail inventoryVerificationDetail) {
        if(!CollectionUtils.isEmpty(medicinalStorageControls)){
            MedicinalStorageControl medicinalStorageControl = medicinalStorageControls.get(0);
            BigDecimal add = medicinalStorageControl.getStorageStock().add(new BigDecimal(profitAndLoss));
            medicinalStorageControl.setStorageStock(add);
            medicinalStorageControl.setSurplusStock(add);
            medicinalStorageControlService.save(medicinalStorageControl);

            //将动态库存总控制表进行库存增加
            ArrayList<Parameter> parameters = new ArrayList<>();
            parameters.add(new Parameter("drug_stuff_id","=",medicinalStorageControl.getDrug().getId()));
            List<MedicinalStockControl> medicinalStockControls = medicinalStockControlService.listAll(parameters, "");
            if(CollectionUtils.isEmpty(medicinalStorageControls)){
                return;
            }

            //动态库存总控制表修改
            MedicinalStockControl medicinalStockControl = medicinalStockControls.get(0);
            BigDecimal add1 = medicinalStockControl.getStorageStock().add(new BigDecimal(profitAndLoss));
            medicinalStockControl.setStorageStock(add1);
            medicinalStockControl.setSurplusStock(add1);
            medicinalStockControlService.save(medicinalStockControl);

            //库存操作记录
            MedicinalStockRecord medicinalStockRecord = new MedicinalStockRecord();
            medicinalStockRecord.setOperationType(5);
            medicinalStockRecord.setOperationStock(new BigDecimal(profitAndLoss+""));
            medicinalStockRecord.setCompany(medicinalStorageControl.getCompany());
            medicinalStockRecord.setDrugStuffId(medicinalStorageControl.getDrugStuffId());
            medicinalStockRecord.setStorageId(medicinalStorageControl.getStorageId());
            if(inventoryVerificationDetail.getDrug()!=null&&Objects.equals("",inventoryVerificationDetail.getDrug().getId())){
                medicinalStockRecord.setType(1);
            }else {
                medicinalStockRecord.setType(2);
            }
            medicinalStockRecord.setDrugStuffName(medicinalStorageControl.getDrugStuffName());
            medicinalStockRecordService.save(medicinalStockRecord);

            //保存完后进行盘点库存操作表保存
//            InventoryVerificationRecord inventoryVerificationRecord = new InventoryVerificationRecord();
//            inventoryVerificationRecord.setSupplierStock(supplierStock);
//            inventoryVerificationRecord.setCompany(supplierStock.getCompany());
//            inventoryVerificationRecord.setNumber(-number);
//            inventoryVerificationRecordService.save(inventoryVerificationRecord);
            InventoryVerificationRecord inventoryVerificationRecord = new InventoryVerificationRecord();
            inventoryVerificationRecord.setCompany(medicinalStorageControl.getCompany());
            SupplierStock supplierStock = new SupplierStock();
            supplierStock.setId(medicinalStorageControl.getStorageId());
            inventoryVerificationRecord.setSupplierStock(supplierStock);
            inventoryVerificationRecord.setNumber(profitAndLoss);
            inventoryVerificationRecordService.save(inventoryVerificationRecord);
//            for (SupplierStock supplierStock : supplierStocks) {
//                Integer stockNumber = supplierStock.getNumber();
//                int profit = Math.abs(profitAndLoss);
//                if(profit<=stockNumber){
//                    int number = profit;
//                    supplierStock.setNumber(stockNumber-number);
//                    supplierStockService.save(supplierStock);
//                    //药品进行库存扣减
//                    if(supplierStock.getDrug().getId()!=null){
//                        drugService.updateInventory(0-number,supplierStock.getDrug().getId());
//                    }else {
//                        stuffService.updateInventory(0-number,supplierStock.getStuff().getId());
//                    }
//

//                    break;
//                }else if(profit>stockNumber){
//                    profit = profit-stockNumber;
//                    supplierStock.setNumber(0);
//                    supplierStockService.save(supplierStock);
//
//                    if(supplierStock.getDrug().getId()!=null){
//                        drugService.updateInventory(0-stockNumber,supplierStock.getDrug().getId());
//                    }else {
//                        stuffService.updateInventory(0-stockNumber,supplierStock.getStuff().getId());
//                    }
//
//                    //保存完后进行盘点库存操作表保存
//                    InventoryVerificationRecord inventoryVerificationRecord = new InventoryVerificationRecord();
//                    inventoryVerificationRecord.setSupplierStock(supplierStock);
//                    inventoryVerificationRecord.setCompany(supplierStock.getCompany());
//                    inventoryVerificationRecord.setNumber(-stockNumber);
//                    inventoryVerificationRecordService.save(inventoryVerificationRecord);
//                }
//            }
        }
    }

    //判断是否存在药品没有发药
    public void whetherTheDispensing(Company company){
        PageRegistration pageRegistration = new PageRegistration();
        pageRegistration.setCompanyId(company.getId());
        pageRegistration.setChargeStatus(1);
        pageRegistration.setColumnName("charge_date");
        pageRegistration.setDispensionStatus(0);
        pageRegistration.setStatus("registrationStatus_1");
        pageRegistration.setRecipelType("recipelType_3");
        pageRegistration.setRecipeStatus("0");
        String dateTime="2022-06-01 00:00:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        try {
            Date parse = simpleDateFormat.parse(dateTime);
            pageRegistration.setUpdateDate(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<String> strings = registrationService.whetherTheDispensing(pageRegistration);
        if(!CollectionUtils.isEmpty(strings)){
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "存在药品/材料还未发药，不能进行盘点!"));
        }

    }

    //判断是否存在未收费的
    public void whetherChargeOrNot(Company company) {
        PageRegistration pageRegistration = new PageRegistration();
        pageRegistration.setCompanyId(company.getId());
        pageRegistration.setColumnName("reception_end_date");
        pageRegistration.setDispensionStatus(3);
        pageRegistration.setStatus("registrationStatus_1");
        pageRegistration.setChargeStatus(0);
//        pageRegistration.setRecipelType("recipelType_3");
        pageRegistration.setRecipeStatus("0");
        String dateTime="2022-06-01 00:00:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        try {
            Date parse = simpleDateFormat.parse(dateTime);
            pageRegistration.setUpdateDate(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> strings = registrationService.whetherChargeOrNot(pageRegistration);
        if(!CollectionUtils.isEmpty(strings)){
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "存在药品/材料还未收费，不能进行盘点!"));
        }
    }

    public List<InventoryVerification> getByCompanyId(String companyId) {
        List<InventoryVerification> inventoryVerifications = this.dao.getByCompanyId(companyId,"0");
        return inventoryVerifications;
    }
}