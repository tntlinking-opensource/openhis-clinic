package com.geeke.medicareutils.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.outpatient.entity.RecipelInfoEvt;
import com.geeke.stock.entity.DispensingEvt;
import com.geeke.stock.entity.OutboundEvt;
import com.geeke.stock.entity.StorageEvt;

/**
 * 医保进销存管理
 */
public interface MdInventoryService {

    /**
     * 商品盘存上传  3501
     * @return
     */
    JSONObject uploadInventory_3501();
    /**
     * 商品盘存上传 3501A
     * @return
     */
    JSONObject uploadInventoryList_3501A();

    /**
     * 商品库存变更 3502
     * @return
     */
    JSONObject updateInventory_3502(StorageEvt storageEvt, String invChgType);

    /**
     * 入库  3502A
     * @return
     */
    JSONObject updateInventoryList_3502A(StorageEvt storageEvt, String invChgType);

    /**
     * 发药退药 3502A
     * @param dispensingEvt
     * @return
     */
    JSONObject updateInventoryList_3502A(DispensingEvt dispensingEvt);


    /**
     * 出库 3502A
     * @param
     * @return
     */
    JSONObject updateInventoryList_3502A(OutboundEvt outboundEvt);

    /**
     * 零售出库 3502A
     * @param
     * @return
     */
    JSONObject updateInventoryList_3502A(RecipelInfoEvt recipelInfoEvt);




    /**
     * 商品采购 3503
     * @return
     */
    JSONObject createPurchaseOrder_3503();

    /**
     * 商品采购 3503A
     * @return
     */
    JSONObject createPurchaseOrderList_3503A();

    /**
     * 商品采购退货 3504
     * @return
     */
    JSONObject createPurchaseReturnOrder_3504();

    /**
     * 商品采购退货 3504A
     * @return
     */
    JSONObject createPurchaseReturnOrderList_3504A();

    /**
     * 商品销售 3505
     * @return
     */
    JSONObject createSaleOrder_3505();

    /**
     * 商品销售 3505A
     * @return
     */
    JSONObject createSaleOrderList_3505A();

    /**
     * 销售退货 3506
     * @return
     */
    JSONObject createSaleReturnOrder_3506();

    /**
     * 销售退货 3506A
     * @return
     */
    JSONObject createSaleReturnOrderList_3506A();

    /**
     * 商品信息删除 3507
     * 通过此交易删除某一批次商品信息
     * @return
     */
    JSONObject removeProductRecord_3507();
    /**
     * 商品信息删除 3507A
     * 通过此交易删除某一批次商品信息
     * @return
     */
    JSONObject removeProductRecordList();

    /**
     *商品库存信息查询 3508
     * @return
     */
     JSONObject getInventoryInfo_3508();

    /**
     * 库存变更记录查询 3509
     * @return
     */
     JSONObject getInventoryChangeRecords_3509();

    /**
     * 采购记录查询 3510
     * @return
     */
     JSONObject getProcurementRecords_3510();

    /**
     * 销售记录查询 3511
     * @return
     */
     JSONObject getSalesRecords_3511();

    /**
     * 入库药品追溯信息查询   3512
     * @return
     */
     JSONObject getInboundDetails_3512();

    /**
     * 销售药品追溯信息查询   3513
     * @return
     */
     JSONObject getSalesDetails_3513();




}
