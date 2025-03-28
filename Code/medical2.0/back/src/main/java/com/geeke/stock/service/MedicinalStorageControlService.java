package com.geeke.stock.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import com.geeke.outpatient.controller.RegistrationController;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.geeke.config.exception.CommonJsonException;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.outpatient.service.RecipelDetailService;
import com.geeke.stock.dao.MedicinalStorageControlDao;
import com.geeke.stock.entity.*;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;

/**
 * 药品/材料入库明细控制Service
 * @author hl
 * @version 2022-09-26
 */
 
@Service("medicinalStorageControlService")
@Transactional(readOnly = true)
public class MedicinalStorageControlService extends CrudService<MedicinalStorageControlDao, MedicinalStorageControl>{
    @Autowired
    private DrugService drugService;
    @Autowired
    private StuffService stuffService;
    @Autowired
    private SupplierStockService supplierStockService;
    @Autowired
    private MedicinalStockControlService medicinalStockControlService;
    @Autowired
    private MedicinalStockRecordService medicinalStockRecordService;
    @Autowired
    private RecipelDetailService recipelDetailService;

    @Autowired
    private DispensingService dispensingService;

    @Autowired
    private RegistrationService registrationService;
    /**
     * 新增入库信息时，控制药品或材料的动态库存明细
     * @param supplierStorage
     */
    @Transactional(readOnly = false)
    public void addStorageStock(SupplierStorage supplierStorage)
    {
        if (!MedicinalStockControlService.enable)
        {
            return;
        }
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("supplier_storage_id", "=", supplierStorage.getId()));
        List<SupplierStock> list = this.supplierStockService.listAll(parameters, "");
        if (CollectionUtils.isEmpty(list))
        {
            return;
        }

        for (SupplierStock supplierStock:list)
        {
            MedicinalStorageControl medicinalStorageControl = new MedicinalStorageControl();
            medicinalStorageControl.setCompany(supplierStock.getCompany());
            medicinalStorageControl.setStorageId(supplierStock.getId());
            if (Objects.nonNull(supplierStock.getDrug()) && StringUtils.isNotBlank(supplierStock.getDrug().getId()))
            {
                Drug drug = this.drugService.get(supplierStock.getDrug().getId());
                if (Objects.isNull(drug) || StringUtils.isBlank(drug.getId()))
                {
                    throw new RuntimeException("入库失败,获取入库的药品基础维护信息失败！");
                }
                this.medicinalStockControlService.initStock(drug);

                medicinalStorageControl.setDrugStuffId(drug.getId());
                medicinalStorageControl.setDrugStuffName(drug.getGoodsName());
                medicinalStorageControl.setType(1);
            }
            else if (Objects.nonNull(supplierStock.getStuff()) && StringUtils.isNotBlank(supplierStock.getStuff().getId()))
            {
                Stuff stuff = this.stuffService.get(supplierStock.getStuff().getId());
                if (Objects.isNull(stuff) || StringUtils.isBlank(stuff.getId()))
                {
                    throw new RuntimeException("入库失败,获取入库的材料基础维护信息失败！");
                }
                this.medicinalStockControlService.initStock(stuff);

                medicinalStorageControl.setDrugStuffId(stuff.getId());
                medicinalStorageControl.setDrugStuffName(stuff.getName());
                medicinalStorageControl.setType(2);
            }
            else
            {
                throw new RuntimeException("入库失败，暂只支持药品或材料入库！");
            }

            medicinalStorageControl.setStorageStock(new BigDecimal(supplierStock.getInitial()));
            medicinalStorageControl.setUsedStock(BigDecimal.ZERO);
            medicinalStorageControl.setOccupyStock(BigDecimal.ZERO);
            medicinalStorageControl.setSurplusStock(new BigDecimal(supplierStock.getInitial()));
            medicinalStorageControl.setReimburseStock(BigDecimal.ZERO);

            //添加药品/材料入库明细控制初始化数据
            super.save(medicinalStorageControl);

            parameters.clear();
            parameters.add(new Parameter("drug_stuff_id", "=", medicinalStorageControl.getDrugStuffId()));
            parameters.add(new Parameter("company_id", "=", medicinalStorageControl.getCompany().getId()));
            List<MedicinalStockControl> medicinalStockControlList = this.medicinalStockControlService.listAll(parameters, "");
            MedicinalStockControl medicinalStockControl = medicinalStockControlList.get(0);
            medicinalStockControl.setStorageStock(medicinalStockControl.getStorageStock().add(medicinalStorageControl.getStorageStock()));
            medicinalStockControl.setSurplusStock(medicinalStockControl.getSurplusStock().add(medicinalStorageControl.getSurplusStock()));
            //更新动态库存入库汇总控制记录
            this.medicinalStockControlService.save(medicinalStockControl);

            //增加库存操作明细记录
            MedicinalStockRecord medicinalStockRecord = new MedicinalStockRecord();
            medicinalStockRecord.setCompany(supplierStock.getCompany());
            medicinalStockRecord.setDrugStuffId(medicinalStorageControl.getDrugStuffId());
            medicinalStockRecord.setDrugStuffName(medicinalStorageControl.getDrugStuffName());
            medicinalStockRecord.setType(medicinalStorageControl.getType());
            medicinalStockRecord.setOperationType(1);    //库存入库操作标识
            medicinalStockRecord.setOperationStock(new BigDecimal(supplierStock.getInitial()));
            medicinalStockRecord.setStorageId(supplierStock.getSupplierStorage().getId());

            this.medicinalStockRecordService.save(medicinalStockRecord);
        }
    }

    //作废入库信息，更新动态库存
    @Transactional(readOnly = false)
    public void invalidStorageStock(SupplierStorage supplierStorage)
    {
        if (!MedicinalStockControlService.enable)
        {
            return;
        }
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("supplier_storage_id", "=", supplierStorage.getId()));
        List<SupplierStock> list = this.supplierStockService.listAll(parameters, "");
        if (CollectionUtils.isEmpty(list))
        {
            throw new RuntimeException("入库信息作废失败，未查找到需要作废的入库明细信息。");
        }

        for (SupplierStock supplierStock:list)
        {
            parameters.clear();
            parameters.add(new Parameter("storage_id", "=", supplierStock.getId()));
            List<MedicinalStorageControl> medicinalStorageControlList = super.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStorageControlList))
            {
                continue;
            }

            for (MedicinalStorageControl medicinalStorageControl:medicinalStorageControlList)
            {
                BigDecimal storageStock = medicinalStorageControl.getStorageStock();    //入库总量
                BigDecimal surplusStock = medicinalStorageControl.getSurplusStock();    //剩余可用库存数
                if (storageStock.compareTo(surplusStock) != 0)
                {
                    throw new RuntimeException("动态库存控制已发生变化，不能作废删除入库数据！");
                }

                //1.作废入库信息时，把可用库存置为0
                medicinalStorageControl.setStorageStock(BigDecimal.ZERO);
                medicinalStorageControl.setSurplusStock(BigDecimal.ZERO);
                super.save(medicinalStorageControl);

                //2.更新动态库存入库汇总控制记录，把总入库量和总的可用量减去作废的量
                parameters.clear();
                parameters.add(new Parameter("drug_stuff_id", "=", medicinalStorageControl.getDrugStuffId()));
                List<MedicinalStockControl> medicinalStockControlList = this.medicinalStockControlService.listAll(parameters, "");
                MedicinalStockControl medicinalStockControl = medicinalStockControlList.get(0);
                medicinalStockControl.setStorageStock(medicinalStockControl.getStorageStock().subtract(storageStock));
                medicinalStockControl.setSurplusStock(medicinalStockControl.getSurplusStock().subtract(surplusStock));
                this.medicinalStockControlService.save(medicinalStockControl);

                //3.插入动态库存操作明细
                MedicinalStockRecord medicinalStockRecord = new MedicinalStockRecord();
                medicinalStockRecord.setCompany(medicinalStorageControl.getCompany());
                medicinalStockRecord.setDrugStuffId(medicinalStorageControl.getDrugStuffId());
                medicinalStockRecord.setDrugStuffName(medicinalStorageControl.getDrugStuffName());
                medicinalStockRecord.setType(medicinalStorageControl.getType());
                medicinalStockRecord.setOperationType(-1);    //库存入库作废操作标识
                medicinalStockRecord.setOperationStock(storageStock);
                medicinalStockRecord.setStorageId(medicinalStorageControl.getStorageId());

                this.medicinalStockRecordService.save(medicinalStockRecord);
            }
        }
    }

    //入库明细自动到期功能调用逻辑（此处理后面放在前台界面手工做，不然后台跑定时任务可能会出现问题）
    @Transactional(readOnly = false)
    public void autoInvalidStorageStock(SupplierStock supplierStock)
    {
        //判断当前是否还有未释放出来被占用的动态库存
        List<MedicinalStockRecord> medicinalStockRecords = this.medicinalStockRecordService.getStockRecordByStorageId(supplierStock.getId());
        if (!CollectionUtils.isEmpty(medicinalStockRecords))
        {
            throw new RuntimeException("当前还存在库存占用或退药未退费库存未释放的情况，请先处理后再操作！");
        }

        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("storage_id", "=", supplierStock.getId()));
        List<MedicinalStorageControl> medicinalStorageControlList = super.listAll(parameters, "");
        if (CollectionUtils.isEmpty(medicinalStorageControlList))
        {
            return;
        }

        MedicinalStorageControl medicinalStorageControl = medicinalStorageControlList.get(0);
        BigDecimal storageStock = medicinalStorageControl.getStorageStock();		// 入库总量
        BigDecimal usedStock = medicinalStorageControl.getUsedStock();		        // 已使用库存数
        BigDecimal occupyStock = medicinalStorageControl.getOccupyStock();		    // 当前占用库存数
        BigDecimal surplusStock = medicinalStorageControl.getSurplusStock();        // 剩余可用库存数
        BigDecimal reimburseStock = medicinalStorageControl.getReimburseStock();    // 报损库存数
        medicinalStorageControl.setSurplusStock(BigDecimal.ZERO);
        medicinalStorageControl.setReimburseStock(reimburseStock.add(surplusStock));
        super.save(medicinalStorageControl);

        parameters.clear();
        parameters.add(new Parameter("drug_stuff_id", "=", medicinalStorageControl.getDrugStuffId()));
        List<MedicinalStockControl> medicinalStockControlList = this.medicinalStockControlService.listAll(parameters, "");
        MedicinalStockControl medicinalStockControl = medicinalStockControlList.get(0);
        medicinalStockControl.setSurplusStock(medicinalStockControl.getSurplusStock().subtract(surplusStock));
        medicinalStockControl.setReimburseStock(medicinalStockControl.getReimburseStock().add(surplusStock));
        this.medicinalStockControlService.save(medicinalStockControl);

        //3.插入动态库存操作明细
        MedicinalStockRecord medicinalStockRecord = new MedicinalStockRecord();
        medicinalStockRecord.setCompany(medicinalStorageControl.getCompany());
        medicinalStockRecord.setDrugStuffId(medicinalStorageControl.getDrugStuffId());
        medicinalStockRecord.setDrugStuffName(medicinalStorageControl.getDrugStuffName());
        medicinalStockRecord.setType(medicinalStorageControl.getType());
        medicinalStockRecord.setOperationType(-2);    //库存入库作废操作标识
        medicinalStockRecord.setOperationStock(surplusStock);
        medicinalStockRecord.setStorageId(medicinalStorageControl.getStorageId());

        this.medicinalStockRecordService.save(medicinalStockRecord);
    }

    /**
     * 根据处方取消预占库存数
     */
    @Transactional(readOnly = false)
    public void cancelOccupy(RecipelInfo recipelInfo) {
        if (!MedicinalStockControlService.enable)
        {
            return;
        }
        String recipelInfoId = recipelInfo.getId();    //处方ID
        if (StringUtils.isBlank(recipelInfoId))
        {
            return;
        }
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("recipel_info_id", "=", recipelInfoId));
        List<MedicinalStockRecord> list = this.medicinalStockRecordService.listAll(parameters, "");
        if (CollectionUtils.isEmpty(list))
        {
            return;
        }

        for (MedicinalStockRecord medicinalStockRecord:list)
        {
            BigDecimal operationStock = medicinalStockRecord.getOperationStock();    //占用库存数
            //更新预占用的入库明细
            parameters.clear();
            parameters.add(new Parameter("storage_id", "=", medicinalStockRecord.getStorageId()));
            List<MedicinalStorageControl> medicinalStorageControlList = super.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStorageControlList))
            {
                return;
            }
            MedicinalStorageControl medicinalStorageControl = medicinalStorageControlList.get(0);
            medicinalStorageControl.setOccupyStock(medicinalStorageControl.getOccupyStock().subtract(operationStock));   //当前占用库存数应该减回去
            medicinalStorageControl.setSurplusStock(medicinalStorageControl.getSurplusStock().add(operationStock));     //剩余可用库存数应该加回去
            //TODO: 还回去刚好遇到入库明细到期了怎么处理？    解决方法：不处理，之前怎么占用的现在就怎么还回去
            super.save(medicinalStorageControl);

            //更新总控制的预占用
            parameters.clear();
            parameters.add(new Parameter("drug_stuff_id", "=", medicinalStockRecord.getDrugStuffId()));
            List<MedicinalStockControl> medicinalStockControlList = this.medicinalStockControlService.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStockControlList))
            {
                return;
            }
            MedicinalStockControl medicinalStockControl = medicinalStockControlList.get(0);
            medicinalStockControl.setOccupyStock(medicinalStockControl.getOccupyStock().subtract(operationStock));   //当前占用库存数应该减回去
            medicinalStockControl.setSurplusStock(medicinalStockControl.getSurplusStock().add(operationStock));     //剩余可用库存数应该加回去
            this.medicinalStockControlService.save(medicinalStockControl);
        }

        //删除明细数据(目前做物理删除)
        this.medicinalStockRecordService.batchDelete(list);
    }

    /**
     * 预占用库存
     * @param recipelInfo
     */
    @Transactional(readOnly = false)
    public void preOccupyStock(RecipelInfo recipelInfo) {
        if (!MedicinalStockControlService.enable)
        {
            return;
        }
        String recipelInfoId = recipelInfo.getId();    //处方ID
        if (StringUtils.isBlank(recipelInfoId))
        {
            return;
        }
        //1.先释放一下预占用库存,报账占用库存的有效性
        this.cancelOccupy(recipelInfo);
        //2.重新占用
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("recipel_info_id", "=", recipelInfoId));
        List<RecipelDetail> recipelDetailList = this.recipelDetailService.listAll(parameters, "");
        if (CollectionUtils.isEmpty(recipelDetailList))
        {
            return;
        }

        for (RecipelDetail recipelDetail:recipelDetailList)
        {
            String drugStuffId = recipelDetail.getDrugStuffId().getDrugStuffId();
            BigDecimal totalBigDecimal = new BigDecimal(recipelDetail.getTotal());    //需要占用的库存
            BigDecimal calculationBigDecimal = BigDecimal.ZERO;                       //用于计算，最后与totalBigDecimal做比较进行控制
            if (totalBigDecimal.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new RuntimeException("处方用量存在开具负数的情况，请正确填写处方开具信息！");
            }
            if (totalBigDecimal.compareTo(BigDecimal.ZERO) == 0)
            {
                continue;
            }

            MedicinalStockRecord medicinalStockRecord = new MedicinalStockRecord();
            medicinalStockRecord.setCompany(recipelDetail.getCompany());
            medicinalStockRecord.setOperationType(2);    //预占用操作标识
            //TODO 加一个诊所Id的条件
            Drug drug = this.drugService.get(drugStuffId);
            Stuff stuff = this.stuffService.get(drugStuffId);
            if (Objects.nonNull(drug) && StringUtils.isNotBlank(drug.getId()))
            {
                medicinalStockRecord.setDrugStuffId(drug.getId());
                medicinalStockRecord.setDrugStuffName(drug.getGoodsName());
                medicinalStockRecord.setType(1);
                if(Objects.equals(drug.getStatus(), "0"))
                {
                    throw new RuntimeException("药品已停用");
                }
            }
            else if (Objects.nonNull(stuff) && StringUtils.isNotBlank(stuff.getId()))
            {
                medicinalStockRecord.setDrugStuffId(stuff.getId());
                medicinalStockRecord.setDrugStuffName(stuff.getName());
                medicinalStockRecord.setType(2);
                if(Objects.equals(stuff.getStatus(), "0"))
                {
                    throw new RuntimeException("材料已停用");
                }
            }
            else
            {
                //目前只有药品、材料才进行动态库存控制，其他不控制
                continue;
            }

            String companyId = recipelInfo.getCompany().getId();
            List<MedicinalStorageControl> medicinalStorageControlList = super.dao.getSurplusStockByDrugStuffIdTo(drugStuffId, companyId);
            if (CollectionUtils.isEmpty(medicinalStorageControlList))
            {
                // throw new RuntimeException("[" + recipelInfo.getName() + "]中[" + medicinalStockRecord.getDrugStuffName() + "]库存不足！");
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "[" + recipelInfo.getName() + "]中[" + medicinalStockRecord.getDrugStuffName() + "]库存不足！"));
            }

            for (MedicinalStorageControl medicinalStorageControl:medicinalStorageControlList)
            {
                if (totalBigDecimal.compareTo(BigDecimal.ZERO) == 0)
                {
                    break;
                }

                BigDecimal surplusStock = medicinalStorageControl.getSurplusStock();   //剩余可用库存数
                BigDecimal occupyStock = medicinalStorageControl.getOccupyStock();		// 当前占用库存数
                if (surplusStock.compareTo(totalBigDecimal) >= 0)   //明细库存够用
                {
                    medicinalStockRecord.setId(null);
                    medicinalStockRecord.setOperationStock(totalBigDecimal);  //操作库存数
                    medicinalStockRecord.setStorageId(medicinalStorageControl.getStorageId());    //占用的入库明细ID
                    medicinalStockRecord.setRecipelInfoId(recipelInfoId);  //占用的处方ID
                    medicinalStockRecord.setRefId(recipelDetail.getId());  //占用的关联记录ID
                    this.medicinalStockRecordService.save(medicinalStockRecord);

                    medicinalStorageControl.setOccupyStock(occupyStock.add(totalBigDecimal));
                    medicinalStorageControl.setSurplusStock(surplusStock.subtract(totalBigDecimal));
                    super.save(medicinalStorageControl);

                    calculationBigDecimal = calculationBigDecimal.add(totalBigDecimal);
                    totalBigDecimal = totalBigDecimal.subtract(totalBigDecimal);
                }
                else                                            //明细库存不够用
                {
                    medicinalStockRecord.setId(null);
                    medicinalStockRecord.setOperationStock(surplusStock);  //操作库存数
                    medicinalStockRecord.setStorageId(medicinalStorageControl.getStorageId());    //占用的入库明细ID
                    medicinalStockRecord.setRecipelInfoId(recipelInfoId);  //占用的处方ID
                    medicinalStockRecord.setRefId(recipelDetail.getId());  //占用的关联记录ID
                    this.medicinalStockRecordService.save(medicinalStockRecord);

                    medicinalStorageControl.setOccupyStock(occupyStock.add(surplusStock));
                    medicinalStorageControl.setSurplusStock(surplusStock.subtract(surplusStock));    //剩余可用库存数,相当于0
                    super.save(medicinalStorageControl);

                    calculationBigDecimal = calculationBigDecimal.add(surplusStock);
                    totalBigDecimal = totalBigDecimal.subtract(surplusStock);
                }
            }

            if (totalBigDecimal.compareTo(BigDecimal.ZERO) > 0)
            {
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "[" + recipelInfo.getName() + "]中[" + medicinalStockRecord.getDrugStuffName() + "]库存不足！"));
//                throw new RuntimeException("[" + recipelInfo.getName() + "]中[" + medicinalStockRecord.getDrugStuffName() + "]库存不足！");
            }

            //更新总控制的预占用
            parameters.clear();
            parameters.add(new Parameter("drug_stuff_id", "=", medicinalStockRecord.getDrugStuffId()));
            parameters.add(new Parameter("company_id", "=", companyId));
            List<MedicinalStockControl> medicinalStockControlList = this.medicinalStockControlService.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStockControlList))
            {
                return;
            }
            MedicinalStockControl medicinalStockControl = medicinalStockControlList.get(0);
            medicinalStockControl.setOccupyStock(medicinalStockControl.getOccupyStock().add(calculationBigDecimal));   //当前占用库存数应该加上去
            medicinalStockControl.setSurplusStock(medicinalStockControl.getSurplusStock().subtract(calculationBigDecimal));     //剩余可用库存数应该减下去
            this.medicinalStockControlService.save(medicinalStockControl);
        }
    }

    /**
     * 确认占用库存（即：把预占用库存转为实际占用库存）
     * 一般在发药的时候即为把预占用库存转为实际占用库存
     * @param
     */
    @Transactional(readOnly = false)
    public void okOccupyStock(String registrationId, String recipelInfoIds) {
        Registration registration = new Registration();
        registration.setId(registrationId);

        RecipelInfo recipelInfo = new RecipelInfo();
        recipelInfo.setId(recipelInfoIds);
        Registration entity = registrationService.get(registrationId);
        recipelInfo.setCompany(entity.getCompany());

        if (!MedicinalStockControlService.enable)
        {
            return;
        }
        String recipelInfoId = recipelInfo.getId();    //处方ID
        if (StringUtils.isBlank(recipelInfoId))
        {
            return;
        }
        //重新预占用库存一把，为了更好的控制库存没有问题，如果重新占用有问题，一般说明控制有我呢提
        this.preOccupyStock(recipelInfo);

        //把预占用库存转为实际占用库存
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("recipel_info_id", "=", recipelInfoId));
        List<MedicinalStockRecord> medicinalStockRecordList = this.medicinalStockRecordService.listAll(parameters, "");
        if (CollectionUtils.isEmpty(medicinalStockRecordList))
        {
            //TODO: 暂做返回处理，可考虑没有查询到就抛异常处理
            return;
        }

        for (MedicinalStockRecord medicinalStockRecord:medicinalStockRecordList)
        {
            BigDecimal operationStock = medicinalStockRecord.getOperationStock();		// 操作库存数
            parameters.clear();
            parameters.add(new Parameter("storage_id", "=", medicinalStockRecord.getStorageId()));
            List<MedicinalStorageControl> medicinalStorageControlList = super.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStorageControlList))
            {
                throw new RuntimeException("动态库存控制失败，未找到需要控制库存的入库明细[明细ID=" + medicinalStockRecord.getStorageId() + "]。");
            }
            MedicinalStorageControl medicinalStorageControl = medicinalStorageControlList.get(0);
            medicinalStorageControl.setUsedStock(medicinalStorageControl.getUsedStock().add(operationStock));            //当前已使用库存数应该加上去
            medicinalStorageControl.setOccupyStock(medicinalStorageControl.getOccupyStock().subtract(operationStock));   //当前占用库存数应该减回去
            super.save(medicinalStorageControl);

            //更新总控制的预占用
            parameters.clear();
            parameters.add(new Parameter("drug_stuff_id", "=", medicinalStockRecord.getDrugStuffId()));
            List<MedicinalStockControl> medicinalStockControlList = this.medicinalStockControlService.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStockControlList))
            {
                throw new RuntimeException("动态库存控制失败，未找到物品[ " + medicinalStockRecord.getDrugStuffName() + " ]的库存总控制记录。");
            }
            MedicinalStockControl medicinalStockControl = medicinalStockControlList.get(0);
            medicinalStockControl.setUsedStock(medicinalStockControl.getUsedStock().add(operationStock));            //当前已使用库存数应该加上去
            medicinalStockControl.setOccupyStock(medicinalStockControl.getOccupyStock().subtract(operationStock));   //当前占用库存数应该减回去
            this.medicinalStockControlService.save(medicinalStockControl);

            //更新动态库存的操作记录状态
            medicinalStockRecord.setOperationType(3);
            this.medicinalStockRecordService.save(medicinalStockRecord);

            //保存发药明细
            Dispensing dispensing = new Dispensing();
            dispensing.setRegistration(registration);
            dispensing.setRecipelInfo(recipelInfo);
            dispensing.setId("");
            SupplierStock supplierStock = new SupplierStock();
            supplierStock.setId(medicinalStorageControl.getStorageId());
            dispensing.setSupplierStock(supplierStock);
            dispensing.setCompany(medicinalStockControl.getCompany());
            dispensing.setNumber(Integer.parseInt(operationStock.stripTrailingZeros().toPlainString()));
            dispensingService.save(dispensing);
        }
    }

    /**
     * 退药暂不退费情况:此情况下药的已使用量要减掉，可用库存量要等退了费用后才加上去
     * @param recipelInfo
     */
    @Transactional(readOnly = false)
    public void drugRepercussion(RecipelInfo recipelInfo)
    {
        if (!MedicinalStockControlService.enable)
        {
            return;
        }
        String recipelInfoId = recipelInfo.getId();    //处方ID
        if (StringUtils.isBlank(recipelInfoId))
        {
            return;
        }

        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("recipel_info_id", "=", recipelInfoId));
        parameters.add(new Parameter("operation_type", "=", 3));
        List<MedicinalStockRecord> medicinalStockRecordList = this.medicinalStockRecordService.listAll(parameters, "");
        if (CollectionUtils.isEmpty(medicinalStockRecordList))
        {
            //TODO: 暂做返回处理，可考虑没有查询到就抛异常处理
            return;
        }

        for (MedicinalStockRecord medicinalStockRecord:medicinalStockRecordList)
        {
            BigDecimal operationStock = medicinalStockRecord.getOperationStock();		// 操作库存数
            parameters.clear();
            parameters.add(new Parameter("storage_id", "=", medicinalStockRecord.getStorageId()));
            List<MedicinalStorageControl> medicinalStorageControlList = super.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStorageControlList))
            {
                throw new RuntimeException("动态库存控制失败，未找到需要控制库存的入库明细[明细ID=" + medicinalStockRecord.getStorageId() + "]。");
            }
            MedicinalStorageControl medicinalStorageControl = medicinalStorageControlList.get(0);
            medicinalStorageControl.setUsedStock(medicinalStorageControl.getUsedStock().subtract(operationStock));            //当前已使用库存数应该减回去
            BigDecimal returnTodoFeeStock1 =medicinalStorageControl.getReturnTodoFeeStock()==null?new BigDecimal("0"):medicinalStorageControl.getReturnTodoFeeStock();
            medicinalStorageControl.setReturnTodoFeeStock(returnTodoFeeStock1.add(operationStock));

            super.save(medicinalStorageControl);

            //更新总控制的预占用
            parameters.clear();
            parameters.add(new Parameter("drug_stuff_id", "=", medicinalStockRecord.getDrugStuffId()));
            List<MedicinalStockControl> medicinalStockControlList = this.medicinalStockControlService.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStockControlList))
            {
                throw new RuntimeException("动态库存控制失败，未找到物品[ " + medicinalStockRecord.getDrugStuffName() + " ]的库存总控制记录。");
            }
            MedicinalStockControl medicinalStockControl = medicinalStockControlList.get(0);
            medicinalStockControl.setUsedStock(medicinalStockControl.getUsedStock().subtract(operationStock));            //当前已使用库存数应该减回去
            BigDecimal returnTodoFeeStock2 =medicinalStockControl.getReturnTodoFeeStock()==null?new BigDecimal("0"):medicinalStockControl.getReturnTodoFeeStock();
            medicinalStockControl.setReturnTodoFeeStock(returnTodoFeeStock2.add(operationStock));
            this.medicinalStockControlService.save(medicinalStockControl);

            //更新动态库存的操作记录状态
            medicinalStockRecord.setOperationType(4);
            this.medicinalStockRecordService.save(medicinalStockRecord);
        }
    }

    /**
     * 退药后，进行退费的情况:此情况下由于之前退药操作已经把”已使用库存数“减回去了，此次只把可用库存加上去就行了
     * @param recipelInfo
     */
    @Transactional(readOnly = false)
    public void goBackFee(RecipelInfo recipelInfo)
    {
        if (!MedicinalStockControlService.enable)
        {
            return;
        }
        String recipelInfoId = recipelInfo.getId();    //处方ID
        if (StringUtils.isBlank(recipelInfoId))
        {
            return;
        }

        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("recipel_info_id", "=", recipelInfoId));
        parameters.add(new Parameter("operation_type", "=", 4));
        List<MedicinalStockRecord> medicinalStockRecordList = this.medicinalStockRecordService.listAll(parameters, "");
        if (CollectionUtils.isEmpty(medicinalStockRecordList))
        {
            //TODO: 暂做返回处理，可考虑没有查询到就抛异常处理
            return;
        }

        for (MedicinalStockRecord medicinalStockRecord:medicinalStockRecordList)
        {
            BigDecimal operationStock = medicinalStockRecord.getOperationStock();		// 操作库存数
            parameters.clear();
            parameters.add(new Parameter("storage_id", "=", medicinalStockRecord.getStorageId()));
            List<MedicinalStorageControl> medicinalStorageControlList = super.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStorageControlList))
            {
                throw new RuntimeException("动态库存控制失败，未找到需要控制库存的入库明细[明细ID=" + medicinalStockRecord.getStorageId() + "]。");
            }
            MedicinalStorageControl medicinalStorageControl = medicinalStorageControlList.get(0);
            medicinalStorageControl.setSurplusStock(medicinalStorageControl.getSurplusStock().add(operationStock));            //当前剩余可用库存数应该加回去
            medicinalStorageControl.setReturnTodoFeeStock(medicinalStorageControl.getReturnTodoFeeStock().subtract(operationStock));
            super.save(medicinalStorageControl);

            //更新总控制的预占用
            parameters.clear();
            parameters.add(new Parameter("drug_stuff_id", "=", medicinalStockRecord.getDrugStuffId()));
            List<MedicinalStockControl> medicinalStockControlList = this.medicinalStockControlService.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStockControlList))
            {
                throw new RuntimeException("动态库存控制失败，未找到物品[ " + medicinalStockRecord.getDrugStuffName() + " ]的库存总控制记录。");
            }
            MedicinalStockControl medicinalStockControl = medicinalStockControlList.get(0);
            medicinalStockControl.setSurplusStock(medicinalStockControl.getSurplusStock().add(operationStock));            //当前剩余可用库存数应该加回去
            medicinalStockControl.setReturnTodoFeeStock(medicinalStockControl.getReturnTodoFeeStock().subtract(operationStock));
            this.medicinalStockControlService.save(medicinalStockControl);

            //更新动态库存的操作记录状态
            //this.medicinalStockRecordService.delete(medicinalStockRecord);
        }

        //删除占用操作明细（此次物理删除）
        this.medicinalStockRecordService.batchDelete(medicinalStockRecordList);
    }

    //获取所有的库存明细
    @Transactional
    public List<MedicinalStorageControl> getAll(String compayId, String type,String variety) {
        return this.dao.getAll(compayId,type,variety);
    }

    @Transactional
    public BigDecimal getByDrugOrStuffId(String drugOrStuffId) {
        return this.dao.getByDrugOrStuffId(drugOrStuffId);
    }

    @Transactional
    public BigDecimal getByDrugOrStuffIds(List drugOrStuffIds) {
        return this.dao.getByDrugOrStuffIds(drugOrStuffIds);
    }



    /**
     * 诊疗项目退费退材料操作
     *
     * @param recipelInfo
     */
    @Transactional
    public void materialRefund(RecipelInfo recipelInfo) {
        if (!MedicinalStockControlService.enable)
        {
            return;
        }
        String recipelInfoId = recipelInfo.getId();    //处方ID
        if (StringUtils.isBlank(recipelInfoId))
        {
            return;
        }

        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("recipel_info_id", "=", recipelInfoId));
        parameters.add(new Parameter("operation_type", "=", 3));
        List<MedicinalStockRecord> medicinalStockRecordList = this.medicinalStockRecordService.listAll(parameters, "");
        if (CollectionUtils.isEmpty(medicinalStockRecordList))
        {
            //TODO: 暂做返回处理，可考虑没有查询到就抛异常处理
            return;
        }

        for (MedicinalStockRecord medicinalStockRecord:medicinalStockRecordList)
        {
            BigDecimal operationStock = medicinalStockRecord.getOperationStock();		// 操作库存数
            parameters.clear();
            parameters.add(new Parameter("storage_id", "=", medicinalStockRecord.getStorageId()));
            List<MedicinalStorageControl> medicinalStorageControlList = super.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStorageControlList))
            {
                throw new RuntimeException("动态库存控制失败，未找到需要控制库存的入库明细[明细ID=" + medicinalStockRecord.getStorageId() + "]。");
            }
            MedicinalStorageControl medicinalStorageControl = medicinalStorageControlList.get(0);
            medicinalStorageControl.setUsedStock(medicinalStorageControl.getUsedStock().subtract(operationStock));            //当前已使用库存数应该减回去
//            BigDecimal returnTodoFeeStock1 =medicinalStorageControl.getReturnTodoFeeStock()==null?new BigDecimal("0"):medicinalStorageControl.getReturnTodoFeeStock();
//            medicinalStorageControl.setReturnTodoFeeStock(returnTodoFeeStock1.add(operationStock));
            medicinalStorageControl.setSurplusStock(medicinalStorageControl.getSurplusStock().add(operationStock));
            super.save(medicinalStorageControl);

            //更新总控制的预占用
            parameters.clear();
            parameters.add(new Parameter("drug_stuff_id", "=", medicinalStockRecord.getDrugStuffId()));
            List<MedicinalStockControl> medicinalStockControlList = this.medicinalStockControlService.listAll(parameters, "");
            if (CollectionUtils.isEmpty(medicinalStockControlList))
            {
                throw new RuntimeException("动态库存控制失败，未找到物品[ " + medicinalStockRecord.getDrugStuffName() + " ]的库存总控制记录。");
            }
            MedicinalStockControl medicinalStockControl = medicinalStockControlList.get(0);
            medicinalStockControl.setUsedStock(medicinalStockControl.getUsedStock().subtract(operationStock));            //当前已使用库存数应该减回去
            medicinalStockControl.setSurplusStock(medicinalStockControl.getSurplusStock().add(operationStock));
//            BigDecimal returnTodoFeeStock2 =medicinalStockControl.getReturnTodoFeeStock()==null?new BigDecimal("0"):medicinalStockControl.getReturnTodoFeeStock();
//            medicinalStockControl.setReturnTodoFeeStock(returnTodoFeeStock2.add(operationStock));
            this.medicinalStockControlService.save(medicinalStockControl);

            //更新动态库存的操作记录状态
//            medicinalStockRecord.setOperationType(4);
//            this.medicinalStockRecordService.save(medicinalStockRecord);
        }
        //删除占用操作明细（此次物理删除）
        this.medicinalStockRecordService.batchDelete(medicinalStockRecordList);
    }


    //预占：按处方来控制，先查询并且释放，再重新预占

    //实占：就是发药时，按处方来控制， 先重新预占一把，再把预占的变为实占

    //退药：先库存退回去，可用库存先不明释放，等退费才真正释放

    //----------------------------
    /**
     * 库房真实库存量=入库总量 - 已使用库存数 - 报损库存数
     * 开药可用库存量=剩余可用库存数
     * 当前占用库存数=当前占用库存数
     * 报损库存数=报损库存数
     * 已退药待退费的药占用库存量=(入库总量 - 已使用库存数 - 报损库存数) - 当前占用库存数 - 剩余可用库存数
     *                                     实际库存（大）
     */
}