package com.geeke.stock.controller;

import java.util.List;

import com.geeke.stock.service.InventoryVerificationDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.stock.entity.InventoryVerification;
import com.geeke.stock.service.InventoryVerificationService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;

/**
 * 库存盘点Controller
 * @author rys
 * @version 2022-11-02
 */
@RestController
@RequestMapping(value = "/stock/inventoryVerification")
public class InventoryVerificationController extends BaseController {

	@Autowired
	private InventoryVerificationService inventoryVerificationService;

	@Autowired
    private InventoryVerificationDetailService inventoryVerificationDetailService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        InventoryVerification entity = inventoryVerificationService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<InventoryVerification> result = inventoryVerificationService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<InventoryVerification> result = inventoryVerificationService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @GetMapping(value = "save/{type}/{variety}")
    public ResponseEntity<JSONObject> save(@PathVariable("type") String type,@PathVariable("variety") String variety) {
        String id = inventoryVerificationService.save(type,variety).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody InventoryVerification entity) {
        int rows = inventoryVerificationService.delete(entity);
        if(rows>0){
            inventoryVerificationDetailService.deleteByInventoryVerificationId(entity.getId());
        }
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<InventoryVerification> entitys) {
        List<String> ids = inventoryVerificationService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<InventoryVerification> entitys) {
        List<String> ids = inventoryVerificationService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<InventoryVerification> entitys) {
        int rows = inventoryVerificationService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "accomplishInventoryVerification")
    public ResponseEntity<JSONObject> accomplishInventoryVerification(@RequestBody InventoryVerification inventoryVerification){
        String id = inventoryVerificationService.accomplishInventoryVerification(inventoryVerification).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

}