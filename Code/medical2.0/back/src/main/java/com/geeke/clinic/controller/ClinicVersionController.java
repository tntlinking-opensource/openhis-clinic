package com.geeke.clinic.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.clinic.entity.ClinicVersion;
import com.geeke.clinic.service.ClinicVersionService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 诊所版本Controller
 * @author txl
 * @version 2022-05-23
 */
@RestController
@RequestMapping(value = "/clinic/clinicVersion")
public class ClinicVersionController extends BaseController {

	@Autowired
	private ClinicVersionService clinicVersionService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        ClinicVersion entity = clinicVersionService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity)) ;
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<ClinicVersion> result = clinicVersionService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<ClinicVersion> result = clinicVersionService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody ClinicVersion entity) {
        String id = clinicVersionService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody ClinicVersion entity) {
        int i = clinicVersionService.deleteClinic(entity);
        return ResponseEntity.ok(ResultUtil.successJson(i));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<ClinicVersion> entitys) {
        List<String> ids = clinicVersionService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<ClinicVersion> entitys) {
        List<String> ids = clinicVersionService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<ClinicVersion> entitys) {
        int rows = clinicVersionService.bulkDeleteClinic(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

}