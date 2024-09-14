package com.geeke.outpatient.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.outpatient.service.RecipelInfoService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处方信息Controller
 * @author txl
 * @version 2022-06-20
 */
@RestController
@RequestMapping(value = "/outpatient/recipelInfo")
public class RecipelInfoController extends BaseController {

	@Autowired
	private RecipelInfoService recipelInfoService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        RecipelInfo entity = recipelInfoService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<RecipelInfo> result = recipelInfoService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<RecipelInfo> result = recipelInfoService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody RecipelInfo entity) {
        String id = recipelInfoService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody RecipelInfo entity) {
        int rows = recipelInfoService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<RecipelInfo> entitys) {
        List<String> ids = recipelInfoService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<RecipelInfo> entitys) {
        List<String> ids = recipelInfoService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<RecipelInfo> entitys) {
        int rows = recipelInfoService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @GetMapping("/update/notShow")
    public ResponseEntity<JSONObject> updateNotShowById(@RequestParam("id") String id) {
        int res = recipelInfoService.updateNotShowById(id);
        return ResponseEntity.ok(ResultUtil.successJson(res));
    }



}