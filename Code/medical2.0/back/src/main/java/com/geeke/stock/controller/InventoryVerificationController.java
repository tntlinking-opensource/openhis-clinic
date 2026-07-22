package com.geeke.stock.controller;

import java.util.List;

import com.geeke.stock.service.InventoryVerificationDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.stock.entity.InventoryVerification;
import com.geeke.stock.service.InventoryVerificationService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.utils.ResultUtil;

/**
 * 库存盘点Controller
 * @author rys
 * @version 2022-11-02
 */
@RestController
@RequestMapping(value = "/stock/inventoryVerification")
public class InventoryVerificationController extends CrudController<InventoryVerificationService, InventoryVerification> {

	@Autowired
	protected InventoryVerificationService inventoryVerificationService;

	@Autowired
    private InventoryVerificationDetailService inventoryVerificationDetailService;

	@Override
	protected InventoryVerificationService getService() {
		return inventoryVerificationService;
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

    @PostMapping(value = "accomplishInventoryVerification")
    public ResponseEntity<JSONObject> accomplishInventoryVerification(@RequestBody InventoryVerification inventoryVerification){
        String id = inventoryVerificationService.accomplishInventoryVerification(inventoryVerification).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

}
