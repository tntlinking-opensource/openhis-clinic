package com.geeke.stock.entity;

import java.util.List;

/**
 * 出库Evt
 * @author txl
 * @version 2022-06-07/
 */
public class OutboundEvt {

    private SupplierOutbound supplierOutbound;
    private List<SupplierOutboundDetail> supplierOutboundOrderList;


    public OutboundEvt(SupplierOutbound supplierOutbound, List<SupplierOutboundDetail> supplierOutboundOrderList) {
        this.supplierOutbound = supplierOutbound;
        this.supplierOutboundOrderList = supplierOutboundOrderList;
    }

    public OutboundEvt() {
    }

    public SupplierOutbound getSupplierOutbound() {
        return supplierOutbound;
    }

    public void setSupplierOutbound(SupplierOutbound supplierOutbound) {
        this.supplierOutbound = supplierOutbound;
    }

    public List<SupplierOutboundDetail> getSupplierOutboundDetailList() {
        return supplierOutboundOrderList;
    }

    public void setSupplierOutboundOrderList(List<SupplierOutboundDetail> supplierOutboundOrderList) {
        this.supplierOutboundOrderList = supplierOutboundOrderList;
    }
}
