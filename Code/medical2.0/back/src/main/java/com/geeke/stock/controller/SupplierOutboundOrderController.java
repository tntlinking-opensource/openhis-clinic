package com.geeke.stock.controller;/*
package com.geeke.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.stock.entity.*;
import com.geeke.stock.service.SupplierOutboundOrderService;
import com.geeke.stock.service.SupplierOutboundService;
import com.geeke.stock.service.SupplierStorageService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

*/
/**
 * 出库明细Controller
 * @author txl
 * @version 2022-06-09/
 *//*

@RestController
@RequestMapping(value = "/stock/supplierOutbound")
public class SupplierOutboundOrderController extends BaseController {

	@Autowired
	private SupplierOutboundOrderService supplierOutboundOrderService;

	@Autowired
	private SupplierStorageService supplierStorageService;


    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        SupplierOutboundOrder entity = supplierOutboundOrderService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @GetMapping("/stock/{sid}")
    public ResponseEntity<JSONObject> getByStorageId(@PathVariable("sid") String sid) {
        List<SupplierOutboundOrder> byStorageId = supplierOutboundOrderService.getByStorageId(sid);
        return ResponseEntity.ok(ResultUtil.successJson(byStorageId));
    }

    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<SupplierOutboundOrder> result = supplierOutboundOrderService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<SupplierOutboundOrder> result = supplierOutboundOrderService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody SupplierOutboundOrder entity) {
        logger.info("dayin:", entity);
        String id = supplierOutboundOrderService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody SupplierOutboundOrder entity) {
        int rows = supplierOutboundOrderService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<SupplierOutboundOrder> entitys) {
        List<String> ids = supplierOutboundOrderService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<SupplierOutboundOrder> entitys) {
        List<String> ids = supplierOutboundOrderService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<SupplierOutboundOrder> entitys) {
        int rows = supplierOutboundOrderService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "inStorage")
    public ResponseEntity<JSONObject> inStorage(@RequestBody OutboundEvt outboundEvt) {
        supplierOutboundOrderService.saveDrug(outboundEvt);
        return ResponseEntity.ok(ResultUtil.successJson());
    }

    @PostMapping(value = "cancel")
    public ResponseEntity<JSONObject> cancel(@RequestBody String id) {
        supplierStorageService.cancel(id);
        return ResponseEntity.ok(ResultUtil.successJson());
    }

    @PostMapping(value = "updateStock")
    public ResponseEntity<JSONObject> updateStock(@RequestBody OutboundEvt outboundEvt) {
        if (null != outboundEvt.getSupplierOutboundDetailList() && !outboundEvt.getSupplierOutboundDetailList().isEmpty()) {
            supplierOutboundOrderService.updateStock(outboundEvt);
        }
        return ResponseEntity.ok(ResultUtil.successJson());
    }


}*/
