package com.geeke.stock.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.stock.entity.MedicinalStockRecord;
import com.geeke.stock.entity.MedicinalStorageControl;

/**
 * 库存操作记录DAO接口
 * @author hl
 * @version 2022-09-26
 */
@Mapper
public interface MedicinalStockRecordDao extends CrudDao<MedicinalStockRecord> {
    int batchDelete(@Param("entitys") List<MedicinalStockRecord> var1);
    List<MedicinalStockRecord> getStockRecordByStorageId(@Param("storageId")String storageId);
}