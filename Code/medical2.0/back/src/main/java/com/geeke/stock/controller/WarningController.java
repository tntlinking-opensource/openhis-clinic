package com.geeke.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.Stuff;
import com.geeke.stock.entity.SupplierStock;
import com.geeke.stock.service.WarningService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/stock/warning")
public class WarningController {
    @Autowired
    private WarningService warningService;

    @PostMapping("getDrugIndateWarning")
    public ResponseEntity<JSONObject> getDrugIndateWarning(@RequestBody SearchParams searchParams){
        Page<SupplierStock> indateWarning = warningService.getDrugIndateWarning(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(indateWarning));
    }

    @PostMapping("getDrugInventoryWarning")
    public ResponseEntity<JSONObject> getDrugInventoryWarning(@RequestBody SearchParams searchParams){
        Page<Drug> drugInventoryWarning = warningService.getDrugInventoryWarning(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(drugInventoryWarning));
    }

    @PostMapping("getStuffIndateWarning")
    public ResponseEntity<JSONObject> getStuffIndateWarning(@RequestBody SearchParams searchParams){
        Page<SupplierStock> indateWarning = warningService.getStuffIndateWarning(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(indateWarning));
    }

    @PostMapping("getStuffInventoryWarning")
    public ResponseEntity<JSONObject> getStuffInventoryWarning(@RequestBody SearchParams searchParams){
        Page<Stuff> stuffInventoryWarning = warningService.getStuffInventoryWarning(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(stuffInventoryWarning));
    }

    @PostMapping("exportTable")
    public void exportTable(@RequestBody SearchParams searchParams, HttpServletResponse response, HttpServletRequest request) throws IOException {
        warningService.exportTable(searchParams,response);
    }
}
