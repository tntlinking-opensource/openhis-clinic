package com.geeke.stock.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.stock.dao.MedicinalStockRecordDao;
import com.geeke.stock.entity.MedicinalStockRecord;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 库存操作记录Service
 * @author hl
 * @version 2022-09-26
 */
 
@Service("medicinalStockRecordService")
@Transactional(readOnly = true)
public class MedicinalStockRecordService extends CrudService<MedicinalStockRecordDao, MedicinalStockRecord>{
    @Autowired
    private MedicinalStockRecordDao medicinalStockRecordDao;

    @Transactional(readOnly = false)
    public int batchDelete(List<MedicinalStockRecord> var1)
    {
        return this.medicinalStockRecordDao.batchDelete(var1);
    }

    @Transactional(readOnly = false)
    public List<MedicinalStockRecord> getStockRecordByStorageId(String storageId)
    {
        return this.medicinalStockRecordDao.getStockRecordByStorageId(storageId);
    }
}