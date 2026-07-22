package com.geeke.stock.service;

import cn.hutool.core.collection.ListUtil;
import com.geeke.common.constants.BizConstants;
import com.geeke.common.data.Page;
import com.geeke.common.sequence.service.SequenceService;
import com.geeke.common.service.CrudService;
import com.geeke.stock.dao.SupplierStorageDao;
import com.geeke.stock.entity.SupplierStock;
import com.geeke.stock.entity.SupplierStorage;
import com.geeke.sys.entity.DictItem;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 入库单Service
 * @author txl
 * @version 2022-06-02
 */
 
@Service("supplierStorageService")
@Transactional(readOnly = true)
public class SupplierStorageService extends CrudService<SupplierStorageDao, SupplierStorage>{
    @Autowired
    SequenceService sequenceService;

    @Autowired
    private SupplierStockService supplierStockService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private SupplierStorageDao supplierStorageDao;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Override
    @Transactional(readOnly = false)
    public SupplierStorage save(SupplierStorage supplierStorage) {
        // 新增时, 处理自动编号字段
        supplierStorage.setCreateDate(new Date());
        if (StringUtils.isBlank(supplierStorage.getId())){
            String oldCode = supplierStorageDao.getCode(supplierStorage.getCompany().getId());
            supplierStorage.setCode(generateDatePrefixCode(oldCode));
        }
        SupplierStorage supplierStorageTemp = super.save(supplierStorage);
        return supplierStorageTemp;
    }

    @Transactional(readOnly = false)
    public void cancel(String id) {
        //作废之前先去判断该药品或者材料是否被使用
        SupplierStorage supplierStorage = this.get(id);
        // 入库单状态->作废
        DictItem dictItem = new DictItem();
        dictItem.setValue(BizConstants.SUPPLIER_STORAGE_EXAMINE_VOIDED);
        supplierStorage.setExamine(dictItem);
        supplierStorage.setExamineDate(new Date());
        this.save(supplierStorage);
        //作废后需要将对应的明细表作废掉
        List<SupplierStock> byStorageId = supplierStockService.getByStorageId(id);

        supplierStockService.cancellation(byStorageId);

        //调用动态库存控制
        this.medicinalStorageControlService.invalidStorageStock(supplierStorage);

        // 作废减库存 - 已迁移至动态库存控制机制
    }


    public Page<SupplierStorage> listByCode(String pinyinCode, String status, String startTime, String endTime, int limit, int offset) {

        String companyId = SessionUtils.getUserDto().getCompanyId();
        List<SupplierStorage> supplierStorages = supplierStorageDao.listByCode(companyId, pinyinCode, status, startTime, endTime);
        int total = supplierStorages.size();
        List<SupplierStorage> list = ListUtil.page(offset, limit, supplierStorages);

        return new Page<>(total, list);
    }
}