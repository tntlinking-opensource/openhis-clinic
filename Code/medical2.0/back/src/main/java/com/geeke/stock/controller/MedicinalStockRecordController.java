package com.geeke.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.geeke.common.controller.CrudController;
import com.geeke.stock.entity.MedicinalStockRecord;
import com.geeke.stock.service.MedicinalStockRecordService;

/**
 * 库存操作记录Controller
 * @author hl
 * @version 2022-09-26
 */
@RestController
@RequestMapping(value = "/stock/medicinalStockRecord")
public class MedicinalStockRecordController extends CrudController<MedicinalStockRecordService, MedicinalStockRecord> {

    @Autowired
    protected MedicinalStockRecordService medicinalStockRecordService;

    @Override
    protected MedicinalStockRecordService getService() {
        return medicinalStockRecordService;
    }
}
