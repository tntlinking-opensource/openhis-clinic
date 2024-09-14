package com.geeke.stock.service;/*
package com.geeke.stock.service;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.RecipelDetailService;
import com.geeke.outpatient.service.RecipelInfoService;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.stock.dao.SupplierOutboundOrderDao;
import com.geeke.stock.dao.SupplierStockDao;
import com.geeke.stock.entity.*;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

*/
/**
 * 出库明细Service
 *
 * @author txl
 * @version 2022-06-09/
 *//*


@Service("SupplierOutboundOrderService")
@Transactional(readOnly = true)
public class SupplierOutboundOrderService extends CrudService<SupplierOutboundOrderDao, SupplierOutboundOrder> {

    @Autowired
    private DispensingService dispensingService;

    @Autowired
    private SupplierOutboundOrderService supplierOutboundOrderService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private SupplierOutboundService supplierOutboundService;

    @Autowired
    private RecipelInfoService recipelInfoService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Autowired
    private InventoryVerificationService inventoryVerificationService;


    @Transactional(readOnly = false)
    public void updateStock(SupplierOutbound supplierOutbound) {

        Company company = SessionUtils.getUser().getCompany();
        //正在进行盘点时不能进行出库操作
        List<InventoryVerification> inventoryVerifications = inventoryVerificationService.getByCompanyId(company.getId());
        if ("supplierOutboundStatus_0".equals(supplierOutbound.getExamineBy())) {
            if (!CollectionUtils.isEmpty(inventoryVerifications)) {
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "正在进行盘点，无法进行出库操作!"));
            }
            List<Parameter> parameters = new ArrayList<>();
            String registrationId = supplierOutbound.getId();*/
/*获取出库单id*//*

            SupplierOutbound registration = supplierOutboundService.get(registrationId); */
/*获取出库单信息*//*

            if ("supplierOutboundStatus_0".equals(supplierOutbound.getExamineBy())) { */
/*审批结果为出库中*//*

                //进行动态库存实占
                medicinalStorageControlService.okOccupyStock(registrationId, registrationId);
                */
/** 如果状态为不通过或撤回 *//*

            }
            if ("supplierOutboundStatus_2".equals(supplierOutbound.getExamineBy()) || "supplierOutboundStatus_3".equals(supplierOutbound.getExamineBy())) {
                // 如果出库单不为空
                if (null != supplierOutbound.getId() && !supplierOutbound.getId().isEmpty()) {
                    parameters.add(new Parameter("supplier_outbound_id", "=", registrationId));
                }
                //取消动态库存占用
                medicinalStorageControlService.drugRepercussion(registrationId);
            }
        }
    }


//    public List<SupplierOutboundOrder> getByStorageId(String id){
//        return this.dao.getByStorageId(id);
//    }

        //存入库单和库存
        @Transactional(readOnly = false)
        public void saveDrug (OutboundEvt outboundEvt)
        {
            //先保存入库单
            SupplierOutbound supplierStorage = OutboundEvt.getSupplierOutbound(); */
/*获取出库单信息*//*

            SupplierOutbound.setInitial(supplierStorage.getNumber()); */
/*存入出库数量*//*

            SupplierOutbound save = supplierOutboundService.save(supplierStorage); */
/*存入出库单*//*

            if (null != outboundEvt.getSupplierOutboundDetailList() && !outboundEvt.getSupplierOutboundDetailList().isEmpty()) {
                for (SupplierOutboundOrder entity : OutboundEvt.getSupplierOutboundDetailList()) {
                    //将入库单号存入每个入库表中
                    entity.getSupplierOutbound(save);
                    entity.setInitial(entity.getNumber());
                    entity.setCancellation("0");
                }

                //动态库存控制
                this.medicinalStorageControlService.addStorageStock(save);
            }
        }

        @Transactional(readOnly = false)
        public void cancellation (List < SupplierOutboundOrder > supplierStockList) {
            this.dao.updateCancel(supplierStockList);
        }


        public Page<SupplierOutboundOrder> getDrugIndateWarning (List < Parameter > parameters,int offset,
        int limit, String orderby){
            PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
            int total = this.dao.countWarning(pageRequest);
            List<SupplierOutboundOrder> list = null;
            if (total > 0) {
                list = this.dao.getDrugIndateWarning(pageRequest);
            }
            if (!CollectionUtils.isEmpty(list)) {
                for (SupplierOutboundOrder supplierStock : list) {
                    if (!Objects.isNull(supplierStock.getMedicinalStorageControl()) && !Objects.isNull(supplierStock.getMedicinalStorageControl().getStorageStock())) {
                        String number = supplierStock.getMedicinalStorageControl().getStorageStock().subtract(supplierStock.getMedicinalStorageControl().getUsedStock().add(supplierStock.getMedicinalStorageControl().getReimburseStock())).stripTrailingZeros().toPlainString();
                        supplierStock.setNumber(Integer.parseInt(number));
                    } else {
                        supplierStock.setNumber(0);
                    }
                }

            }
            return new Page((long) total, list);
        }

        public Page<SupplierOutboundOrder> getStuffIndateWarning (List < Parameter > parameters,int offset,
        int limit, String orderby){
            PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
            int total = this.dao.countStuffWarning(pageRequest);
            List<SupplierOutboundOrder> list = null;
            if (total > 0) {
                list = this.dao.getStuffIndateWarning(pageRequest);
            }
            if (!CollectionUtils.isEmpty(list)) {
                for (SupplierOutboundOrder supplierStock : list) {
                    if (!Objects.isNull(supplierStock.getMedicinalStorageControl()) && !Objects.isNull(supplierStock.getMedicinalStorageControl().getStorageStock())) {
                        String number = supplierStock.getMedicinalStorageControl().getStorageStock().subtract(supplierStock.getMedicinalStorageControl().getUsedStock().add(supplierStock.getMedicinalStorageControl().getReimburseStock())).stripTrailingZeros().toPlainString();
                        supplierStock.setNumber(Integer.parseInt(number));
                    } else {
                        supplierStock.setNumber(0);
                    }
                }
            }
            return new Page((long) total, list);
        }
}*/
