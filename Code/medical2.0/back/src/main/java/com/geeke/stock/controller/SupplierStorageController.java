package com.geeke.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.common.data.Page;
import com.geeke.stock.entity.SupplierStorage;
import com.geeke.stock.service.SupplierStorageService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 入库单Controller
 * @author txl
 * @version 2022-06-02
 */
@RestController
@RequestMapping(value = "/stock/supplierStorage")
public class SupplierStorageController extends CrudController<SupplierStorageService, SupplierStorage> {

	@Autowired
	protected SupplierStorageService supplierStorageService;

	@Override
	protected SupplierStorageService getService() {
		return supplierStorageService;
	}

    @PostMapping(value = "cancel")
    public ResponseEntity<JSONObject> cancel(@RequestBody SupplierStorage entity) {
        supplierStorageService.cancel(entity.getId());
        return ResponseEntity.ok(ResultUtil.successJson());
    }

    /**
     * 可根据拼音码查询
     *
     * @return
     */
    @GetMapping(value = "/listByCode")
    public ResponseEntity<JSONObject> listByCode(@RequestParam(required = false) String pinyinCode,
                                                 @RequestParam(required = false) String status,
                                                 @RequestParam(required = false) String startTime,
                                                 @RequestParam(required = false) String endTime,
                                                 @RequestParam int limit,
                                                 @RequestParam int offset) {
        Page<SupplierStorage> result = supplierStorageService.listByCode(pinyinCode, status, startTime, endTime, limit, offset);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
}
