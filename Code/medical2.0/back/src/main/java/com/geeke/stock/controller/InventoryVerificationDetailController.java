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
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
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
public class InventoryVerificationDetailController extends CrudController<InventoryVerificationDetailService, InventoryVerificationDetail> {

	@Autowired
	protected InventoryVerificationDetailService inventoryVerificationDetailService;

	@Autowired
    private DrugService drugService;

	@Autowired
    private StuffService stuffService;

	@Override
	protected InventoryVerificationDetailService getService() {
		return inventoryVerificationDetailService;
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
