package com.geeke.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.geeke.common.controller.CrudController;
import com.geeke.stock.entity.MedicinalStorageControl;
import com.geeke.stock.service.MedicinalStorageControlService;

/**
 * 药品/材料入库明细控制Controller
 * @author hl
 * @version 2022-09-26
 */
@RestController
@RequestMapping(value = "/stock/medicinalStorageControl")
public class MedicinalStorageControlController extends CrudController<MedicinalStorageControlService, MedicinalStorageControl> {

    @Autowired
    protected MedicinalStorageControlService medicinalStorageControlService;

    @Override
    protected MedicinalStorageControlService getService() {
        return medicinalStorageControlService;
    }
}
