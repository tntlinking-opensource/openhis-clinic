package com.geeke.stock.service;

import com.geeke.common.service.CrudService;
import com.geeke.stock.dao.InventoryVerificationDetailDao;
import com.geeke.stock.dao.InventoryVerificationRecordDao;
import com.geeke.stock.entity.InventoryVerificationDetail;
import com.geeke.stock.entity.InventoryVerificationRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("InventoryVerificationRecordService")
@Transactional(readOnly = true)
public class InventoryVerificationRecordService extends CrudService<InventoryVerificationRecordDao, InventoryVerificationRecord> {

}
