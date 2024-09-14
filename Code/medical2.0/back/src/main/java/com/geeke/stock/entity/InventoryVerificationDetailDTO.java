package com.geeke.stock.entity;

import lombok.Data;

import java.util.List;

@Data
public class InventoryVerificationDetailDTO {
    private List<InventoryVerificationDetail> inventoryVerificationDetailList;
}
