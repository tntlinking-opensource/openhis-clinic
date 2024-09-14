package com.geeke.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.stock.entity.SupplierStorage;
import com.geeke.stock.service.SupplierStorageService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 入库单Controller
 * @author txl
 * @version 2022-06-02
 */
@RestController
@RequestMapping(value = "/stock/supplierStorage")
public class SupplierStorageController extends BaseController {

	@Autowired
	private SupplierStorageService supplierStorageService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        SupplierStorage entity = supplierStorageService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<SupplierStorage> result = supplierStorageService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<SupplierStorage> result = supplierStorageService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody SupplierStorage entity) {
        String id = supplierStorageService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody SupplierStorage entity) {
        int rows = supplierStorageService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<SupplierStorage> entitys) {
        List<String> ids = supplierStorageService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<SupplierStorage> entitys) {
        List<String> ids = supplierStorageService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "cancel")
    public ResponseEntity<JSONObject> cancel(@RequestBody SupplierStorage entity) {
        supplierStorageService.cancel(entity.getId());
        return ResponseEntity.ok(ResultUtil.successJson());
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<SupplierStorage> entitys) {
        int rows = supplierStorageService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
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