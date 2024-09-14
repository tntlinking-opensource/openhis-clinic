package com.geeke.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.stock.entity.InventoryVerification;
import com.geeke.stock.entity.OutboundEvt;
import com.geeke.stock.entity.SupplierOutbound;
import com.geeke.stock.service.SupplierOutboundService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 出库单Controller
 *
 * @author txl
 * @version 2023-07-14/
 */

@RestController
@RequestMapping(value = "/stock/supplierOutbound")
public class SupplierOutboundController extends BaseController {

    @Autowired
    private SupplierOutboundService supplierOutboundService;

    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<SupplierOutbound> result = supplierOutboundService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        SupplierOutbound entity = supplierOutboundService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }


    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody OutboundEvt entity) {
        String id = supplierOutboundService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @GetMapping("/cancel/{id}")
    public ResponseEntity<JSONObject> cancel(@PathVariable("id") String id) {
        supplierOutboundService.cancel(id);
        return ResponseEntity.ok(ResultUtil.successJson());
    }

    @GetMapping("/examine/{id}")
    public ResponseEntity<JSONObject> examine(@PathVariable("id") String id) {
        supplierOutboundService.examine(id);
        return ResponseEntity.ok(ResultUtil.successJson());
    }
}
