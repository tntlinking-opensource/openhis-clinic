package com.geeke.treatment.entity;

import java.util.List;

public class CostItemDTO {
    private CostItem costItem;
    private List<CostItemPackage> costItemPackage;
    private List<CostItem> costItems;

    public CostItemDTO() {
    }

    public CostItemDTO(CostItem costItem, List<CostItemPackage> costItemPackage, List<CostItem> costItems) {
        this.costItem = costItem;
        this.costItemPackage = costItemPackage;
        this.costItems = costItems;
    }

    public CostItem getCostItem() {
        return costItem;
    }

    public void setCostItem(CostItem costItem) {
        this.costItem = costItem;
    }

    public List<CostItemPackage> getCostItemPackage() {
        return costItemPackage;
    }

    public void setCostItemPackage(List<CostItemPackage> costItemPackage) {
        this.costItemPackage = costItemPackage;
    }

    public List<CostItem> getCostItems() {
        return costItems;
    }

    public void setCostItems(List<CostItem> costItems) {
        this.costItems = costItems;
    }

    @Override
    public String toString() {
        return "CostItemDTO{" +
                "costItem=" + costItem +
                ", costItemPackage=" + costItemPackage +
                ", costItems=" + costItems +
                '}';
    }
}
