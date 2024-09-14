package com.geeke.stock.controller;

import java.util.List;

import com.geeke.stock.entity.DispensingReportEvt;
import com.geeke.stock.entity.DispensingReportTotalEvt;
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
import com.geeke.stock.entity.Dispensing;
import com.geeke.stock.service.DispensingService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;

/**
 * 发药明细Controller
 * @author txl
 * @version 2022-08-11
 */
@RestController
@RequestMapping(value = "/stock/dispensing")
public class DispensingController extends BaseController {

	@Autowired
	private DispensingService dispensingService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        Dispensing entity = dispensingService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<Dispensing> result = dispensingService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<Dispensing> result = dispensingService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody Dispensing entity) {
        String id = dispensingService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody Dispensing entity) {
        int rows = dispensingService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<Dispensing> entitys) {
        List<String> ids = dispensingService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<Dispensing> entitys) {
        List<String> ids = dispensingService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<Dispensing> entitys) {
        int rows = dispensingService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    /**
     * 发药报表
     * @param searchParams
     * @return
     */
    @PostMapping(value = "reportList")
    public ResponseEntity<JSONObject> reportList(@RequestBody SearchParams searchParams) {
        Page<DispensingReportEvt> result = dispensingService.reportList(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "getOrganizationList")
    public ResponseEntity<JSONObject> getOrganizationList(@RequestBody SearchParams searchParams) {
        Page<DispensingReportEvt> result = dispensingService.getOrganizationList(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 发药报表
     * @param searchParams
     * @return
     */
    @PostMapping(value = "reportAmount")
    public ResponseEntity<JSONObject> reportAmount(@RequestBody SearchParams searchParams) {
        DispensingReportTotalEvt dispensingReportTotalEvt = dispensingService.reportAmount(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(dispensingReportTotalEvt));
    }

}