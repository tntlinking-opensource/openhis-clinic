package com.geeke.stock.service;

import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.stock.dao.SupplierOutboundDao;
import com.geeke.stock.dao.SupplierOutboundDetailDao;
import com.geeke.stock.entity.*;
import com.geeke.sys.entity.DictItem;
import com.geeke.toll.untils.BigdecimalConvert;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 出库单明细Service
 *
 * @author txl
 * @version 2022-06-02/
 */


@Service("supplierOutboundDetailService")
@Transactional(readOnly = true)
public class SupplierOutboundDetailService extends CrudService<SupplierOutboundDetailDao, SupplierOutboundDetail> {
    @Autowired
    private SupplierStockService supplierStockService;

    @Autowired
    private SupplierOutboundService supplierOutboundService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private SupplierOutboundDetailDao supplierOutboundDetailDao;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    /**
     * 保存出库单明细
     *
     * @param supplierOutboundDetails
     * @return
     */
    @Transactional(readOnly = false)
    public void save(String outboundId, List<SupplierOutboundDetail> supplierOutboundDetails) {
        List<SupplierOutboundDetail> exist = supplierOutboundDetailDao.getByOutboundId(outboundId);

        SupplierOutbound outbound = supplierOutboundService.get(outboundId);
        if (outbound == null) throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单不存在"));

        //更新已有明细，找出需要删除的明细
        List<SupplierOutboundDetail> willDeleteDetail = new ArrayList<>();
        for (SupplierOutboundDetail d : exist) {
            int found = supplierOutboundDetails.indexOf(d);
            if (found < 0) {
                willDeleteDetail.add(d);//即将删除
            } else {
                //更新
                updateSupplierOutboundDetail(d);
            }
        }

        //删除需要删除的
        for (SupplierOutboundDetail d : willDeleteDetail) {
            deleteSupplierOutboundDetail(d.getId());
        }

        //添加
        for (SupplierOutboundDetail d : supplierOutboundDetails) {
            d.setSupplierOutbound(outbound);
            if (!StringUtils.isNullOrEmpty(d.getId())) continue;

            //锁定库存
            MedicinalStorageControl medicinalStorageControl = d.getMedicinalStorage();
            MedicinalStorageControl queryStorageControl = medicinalStorageControlService.get(d.getMedicinalStorage().getId());
            if (queryStorageControl == null) {
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "动态库存记录不存在"));
            }

            if (queryStorageControl.getSurplusStock().compareTo(d.getNumber()) < 0) {
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "可用库存不够"));
            }
            medicinalStorageControl.setOccupyStock(queryStorageControl.getOccupyStock().add(d.getNumber()));
            medicinalStorageControlService.save(queryStorageControl);

            save(d);
        }
    }

    /**
     * 删除出库明细，释放冻结库存
     *
     * @param detailId
     */
    @Transactional(readOnly = false)
    public void deleteSupplierOutboundDetail(String detailId) {
        SupplierOutboundDetail detail = supplierOutboundDetailDao.get(detailId);
        if (detail == null) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单明细不存在"));
        }

        MedicinalStorageControl medicinalStorageControl = medicinalStorageControlService.get(detail.getMedicinalStorage().getId()); //detail.getMedicinalStorage();
        medicinalStorageControl.setOccupyStock(medicinalStorageControl.getOccupyStock().subtract(detail.getNumber()));
        medicinalStorageControlService.save(medicinalStorageControl);

        supplierOutboundDetailDao.delete(detail);
    }

    /**
     * 撤消所有出库单明细，释放库存
     *
     * @param outboundId
     */
    @Transactional(readOnly = false)
    public void cancelSupplierOutboundDetail(String outboundId) {
        List<SupplierOutboundDetail> details = supplierOutboundDetailDao.getByOutboundId(outboundId);
        for (SupplierOutboundDetail detail : details) {
            MedicinalStorageControl medicinalStorageControl = medicinalStorageControlService.get(detail.getMedicinalStorage().getId());//detail.getMedicinalStorage();
            medicinalStorageControl.setOccupyStock(medicinalStorageControl.getOccupyStock().subtract(detail.getNumber()));
            medicinalStorageControlService.save(medicinalStorageControl);
        }
    }

    /**
     * 修改出库旱明细，修正冻结库存数量
     *
     * @param detail
     */
    @Transactional(readOnly = false)
    public void updateSupplierOutboundDetail(SupplierOutboundDetail detail) {
        SupplierOutboundDetail old = supplierOutboundDetailDao.get(detail.getId());
        if (old == null) throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "出库单明细不存在"));

        MedicinalStorageControl medicinalStorageControl = medicinalStorageControlService.get(detail.getMedicinalStorage().getId());//old.getMedicinalStorage();
        BigDecimal addNumber = detail.getNumber().subtract(old.getNumber());//增加冻结的数量
        if (addNumber.compareTo(BigDecimal.ZERO) > 0) {
            if (addNumber.compareTo(medicinalStorageControl.getSurplusStock()) > 0) {
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50002, "可用库存不够"));
            }
            medicinalStorageControl.setOccupyStock(medicinalStorageControl.getOccupyStock().add(addNumber));
            medicinalStorageControl.setSurplusStock(medicinalStorageControl.getSurplusStock().subtract(addNumber));
        } else {
            medicinalStorageControl.setOccupyStock(medicinalStorageControl.getOccupyStock().subtract(addNumber));
            medicinalStorageControl.setSurplusStock(medicinalStorageControl.getSurplusStock().add(addNumber));
        }
        medicinalStorageControlService.save(medicinalStorageControl);
    }

    /**
     * 撤消出库单
     *
     * @param outboundId
     */
    @Transactional(readOnly = false)
    public List<SupplierOutboundDetail> getByOutboundId(String outboundId) {
        return supplierOutboundDetailDao.getByOutboundId(outboundId);
    }
}
