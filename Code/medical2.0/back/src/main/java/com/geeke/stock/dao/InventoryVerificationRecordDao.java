package com.geeke.stock.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.InventoryVerification;
import com.geeke.stock.entity.InventoryVerificationRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryVerificationRecordDao extends CrudDao<InventoryVerificationRecord> {
}
