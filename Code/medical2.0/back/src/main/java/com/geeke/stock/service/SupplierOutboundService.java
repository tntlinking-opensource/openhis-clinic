package com.geeke.stock.service;

import com.geeke.admin.entity.User;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.stock.dao.SupplierOutboundDao;
import com.geeke.stock.entity.*;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * 出库单Service
 *
 * @author txl
 * @version 2022-06-02/
 */


@Service("supplierOutboundService")
@Transactional(readOnly = true)
public class SupplierOutboundService extends CrudService<SupplierOutboundDao, SupplierOutbound> {
    @Autowired
    private SupplierStockService supplierStockService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private SupplierOutboundDao supplierOutboundDao;

    @Autowired
    private SupplierOutboundDetailService supplierOutboundDetailService;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Autowired
    private MedicinalStockControlService medicinalStockControlService;

    @Autowired
    private MedicinalStockRecordService medicinalStockRecordService;

    @Autowired
    private SupplierOutboundService supplierOutboundService;
    /**
     * 保存出库单
     *
     * @param supplierOutbound
     * @return
     */
    //@Override
    @Transactional(readOnly = false)
    public SupplierOutbound save(OutboundEvt outboundEvt) {
        if(ObjectUtils.isEmpty(outboundEvt.getSupplierOutbound())==false && ObjectUtils.isEmpty(outboundEvt.getSupplierOutbound().getId())==false) {
            SupplierOutbound old = this.get(outboundEvt.getSupplierOutbound().getId());
            if (old == null)
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单不存在"));
            if (old.getStatus() == 2)
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单已审核，不能修改"));
            if (old.getStatus() == 3)
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单已撤销，不能修改"));
        }

        // 新增时, 处理自动编号字段
        SupplierOutbound supplierOutbound = outboundEvt.getSupplierOutbound();
        supplierOutbound.setCreateDate(new Date());
        supplierOutbound.setExamineBy(null);
        supplierOutbound.setStatus(1);//待审核

        if (StringUtils.isBlank(supplierOutbound.getId())) {

            //获取该诊所最后一次出库的单号
            String oldOutbound = supplierOutboundDao.getCode(supplierOutbound.getCompany().getId());
            if (oldOutbound == "" || oldOutbound == null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(new Date()).replace("-", "");
                String outbound = format + "0000001";
                supplierOutbound.setCode(outbound);
            } else {
                String increment = Integer.parseInt(oldOutbound.substring(8)) + 1 + "";
                String prefix = "";
                for (int i = 0; i < 7 - increment.length(); i++) {
                    prefix += "0";
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(new Date()).replace("-", "");
                String outbound = format + prefix + increment;
                supplierOutbound.setCode(outbound);
            }
        }
        SupplierOutbound supplierOutboundTemp = super.save(supplierOutbound);

        //保存明细
        supplierOutboundDetailService.save(supplierOutboundTemp.getId(), outboundEvt.getSupplierOutboundDetailList());

        return supplierOutboundTemp;
    }

    /**
     * 撤消出库单
     *
     * @param id
     */
    @Transactional(readOnly = false)
    public void cancel(String id) {
        //作废之前先去判断该药品或者材料是否被使用
        SupplierOutbound supplierOutbound = this.get(id);
        if (supplierOutbound == null)
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单不存在"));
        if (supplierOutbound.getStatus() == 3)
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单已撤销，不能重复操作"));
        if (supplierOutbound.getStatus() == 2)
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单已审核，不能撤销"));
        supplierOutbound.setStatus(3);
        //super.save(supplierOutbound);
        supplierOutbound.setCancelDate(new Date());
        supplierOutboundDao.cancel(supplierOutbound);

        //释放库存
        supplierOutboundDetailService.cancelSupplierOutboundDetail(id);
    }

    /**
     * 审核出库单
     *
     * @param id
     */
    @Transactional(readOnly = false)
    public void examine(String id) {
        //作废之前先去判断该药品或者材料是否被使用
        SupplierOutbound supplierOutbound = this.get(id);
        if (supplierOutbound == null)
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单不存在"));
        if (supplierOutbound.getStatus() == 3)
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单已撤消，不能审核"));
        if (supplierOutbound.getStatus() == 2)
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单不能重复审核"));

        //根据明细循环减去库存
        List<SupplierOutboundDetail> details = supplierOutboundDetailService.getByOutboundId(id);
        for (SupplierOutboundDetail detail : details) {
            //更新动态库存
            MedicinalStorageControl medicinalStorageControl = medicinalStorageControlService.get(detail.getMedicinalStorage().getId()); //detail.getMedicinalStorage();
            medicinalStorageControl.setOccupyStock(medicinalStorageControl.getOccupyStock().subtract(detail.getNumber()));
            medicinalStorageControl.setUsedStock(medicinalStorageControl.getUsedStock().add(detail.getNumber()));
            medicinalStorageControl.setSurplusStock(medicinalStorageControl.getSurplusStock().subtract(detail.getNumber()));
            medicinalStorageControlService.save(medicinalStorageControl);

            //更新总库存控制
            String drugOrStuffId = (detail.getDrug() != null && !StringUtils.isNullOrEmpty(detail.getDrug().getId()))? detail.getDrug().getId() : detail.getStuff().getId();
            String companyId = supplierOutboundService.get(id).getCompany().getId();
            List<MedicinalStockControl> stockControls = medicinalStockControlService.getByDrugOrStuffId(drugOrStuffId,companyId);
            if (stockControls.size() == 0)
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "未找到药品材料库存记录"));
            if (stockControls.size() > 1)
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "找到多条药品材料库存记录"));
            MedicinalStockControl medicinalStockControl = stockControls.get(0);
            medicinalStockControl.setUsedStock(medicinalStockControl.getUsedStock().add(detail.getNumber()));
            medicinalStockControl.setSurplusStock(medicinalStockControl.getSurplusStock().subtract(detail.getNumber()));
            medicinalStockControlService.save(medicinalStockControl);

            //保存库存控制记录
            MedicinalStockRecord medicinalStockRecord = new MedicinalStockRecord();
            medicinalStockRecord.setOperationType(6);
            medicinalStockRecord.setOperationStock(detail.getNumber());
            medicinalStockRecord.setCompany(medicinalStorageControl.getCompany());
            medicinalStockRecord.setDrugStuffId(medicinalStorageControl.getDrugStuffId());
            medicinalStockRecord.setStorageId(medicinalStorageControl.getStorageId());
            if (detail.getDrug() != null && Objects.equals("", detail.getDrug().getId())) {
                medicinalStockRecord.setType(1);
            } else {
                medicinalStockRecord.setType(2);
            }
            medicinalStockRecord.setDrugStuffName(medicinalStorageControl.getDrugStuffName());
            medicinalStockRecordService.save(medicinalStockRecord);
        }

        supplierOutbound.setStatus(2);
        //super.save(supplierOutbound);
        User loginUser= SessionUtils.getUser();
        supplierOutbound.setExamineBy(loginUser.getName());
        supplierOutbound.setExamineDate(new Date());
        supplierOutboundDao.examinee(supplierOutbound);
    }

}
