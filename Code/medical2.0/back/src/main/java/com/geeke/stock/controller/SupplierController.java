package com.geeke.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.geeke.common.controller.CrudController;
import com.geeke.stock.entity.Supplier;
import com.geeke.stock.service.SupplierService;

/**
 * 供应商管理Controller
 * @author txl
 * @version 2022-06-22
 */
@RestController
@RequestMapping(value = "/stock/supplier")
public class SupplierController extends CrudController<SupplierService, Supplier> {

    @Autowired
    protected SupplierService supplierService;

    @Override
    protected SupplierService getService() {
        return supplierService;
    }
}
