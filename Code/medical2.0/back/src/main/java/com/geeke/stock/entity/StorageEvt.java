package com.geeke.stock.entity;

import java.util.List;

/**
 * 入库Evt
 * @author txl
 * @version 2022-06-07
 */
public class StorageEvt {

    private static final long serialVersionUID = 1005591224273698939L;
    private SupplierStorage supplierStorage;
    private List<SupplierStock> supplierStockList;

    public StorageEvt(SupplierStorage supplierStorage, List<SupplierStock> supplierStockList) {
        this.supplierStorage = supplierStorage;
        this.supplierStockList = supplierStockList;
    }

    public StorageEvt() {
    }

    public SupplierStorage getSupplierStorage() {
        return supplierStorage;
    }

    public void setSupplierStorage(SupplierStorage supplierStorage) {
        this.supplierStorage = supplierStorage;
    }

    public List<SupplierStock> getSupplierStockList() {
        return supplierStockList;
    }

    public void setSupplierStockList(List<SupplierStock> supplierStockList) {
        this.supplierStockList = supplierStockList;
    }
}
