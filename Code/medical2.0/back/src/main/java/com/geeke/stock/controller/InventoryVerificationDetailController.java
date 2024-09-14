package com.geeke.stock.controller;

import java.io.IOException;
import java.util.List;

import com.geeke.stock.entity.*;
import com.geeke.stock.service.DrugService;
import com.geeke.stock.service.StuffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.stock.service.InventoryVerificationDetailService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 库存盘点详情Controller
 * @author 超级管理员
 * @version 2022-11-02
 */
@RestController
@RequestMapping(value = "/stock/inventoryVerificationDetail")
public class InventoryVerificationDetailController extends BaseController {

	@Autowired
	private InventoryVerificationDetailService inventoryVerificationDetailService;

	@Autowired
    private DrugService drugService;

	@Autowired
    private StuffService stuffService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        InventoryVerificationDetail entity = inventoryVerificationDetailService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = "list/{type}")
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams,@PathVariable String type) {
        Page<InventoryVerificationDetail> result = inventoryVerificationDetailService.listPages(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        List<InventoryVerificationDetail> rows = result.getRows();
        if (rows!=null){
            for (InventoryVerificationDetail row : rows) {
                if(!StringUtils.isNullOrEmpty(row.getDrug())){
                    Drug drug = drugService.get(row.getDrug().getId());
                    row.setDrug(drug);
                }else {
                    Stuff stuff = stuffService.get(row.getStuff().getId());
                    row.setStuff(stuff);
                }
            }
        }
        Page<InventoryVerificationDetail> inventoryVerificationDetailPage = new Page<>(result.getTotal(), rows);
        return ResponseEntity.ok(ResultUtil.successJson(inventoryVerificationDetailPage));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<InventoryVerificationDetail> result = inventoryVerificationDetailService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody InventoryVerificationDetail entity) {
        String id = inventoryVerificationDetailService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody InventoryVerificationDetail entity) {
        int rows = inventoryVerificationDetailService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<InventoryVerificationDetail> entitys) {
        List<String> ids = inventoryVerificationDetailService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<InventoryVerificationDetail> entitys) {
        List<String> ids = inventoryVerificationDetailService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<InventoryVerificationDetail> entitys) {
        int rows = inventoryVerificationDetailService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }


//    @PostMapping("getInventoryVerificationDetailByInventoryId")
//    public ResponseEntity<JSONObject> getInventoryVerificationDetailByInventoryId(@RequestBody SearchParams searchParams) {
//        List<InventoryVerificationDetail> entity = inventoryVerificationDetailService.getInventoryVerificationDetailByInventoryId(searchParams);
//        return ResponseEntity.ok(ResultUtil.successJson(entity));
//    }

    @PostMapping("saveAll")
    public ResponseEntity<JSONObject> saveAll(@RequestBody List<List<InventoryVerificationDetail>> inventoryVerificationDetailDTOS){
        String s = inventoryVerificationDetailService.saveAll(inventoryVerificationDetailDTOS);
        return ResponseEntity.ok(ResultUtil.successJson(s));
    }

    @PostMapping("exportExcel")
    public void exportExcel(@RequestBody InventoryVerification inventoryVerification, HttpServletRequest request,HttpServletResponse response) throws IOException {
        inventoryVerificationDetailService.exportExcel(inventoryVerification,response);
    }
}