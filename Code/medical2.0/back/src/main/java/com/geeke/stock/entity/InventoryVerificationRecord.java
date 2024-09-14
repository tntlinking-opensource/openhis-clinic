package com.geeke.stock.entity;

import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import lombok.Data;

@Data
public class InventoryVerificationRecord extends DataEntity<InventoryVerificationRecord> {
    private String id;
    private Company company;
    private SupplierStock supplierStock;
    private int number;
}
