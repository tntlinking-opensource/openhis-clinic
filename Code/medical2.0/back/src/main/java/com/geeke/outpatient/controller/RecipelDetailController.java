package com.geeke.outpatient.controller;

import java.util.List;

import com.geeke.outpatient.service.MedicalRecordService;
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
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.service.RecipelDetailService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;

/**
 * 处方详情Controller
 * @author txl
 * @version 2022-06-07
 */
@RestController
@RequestMapping(value = "/outpatient/recipelDetail")
public class RecipelDetailController extends BaseController {

	@Autowired
	private RecipelDetailService recipelDetailService;

	@Autowired
    private MedicalRecordService medicalRecordService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        RecipelDetail entity = recipelDetailService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<RecipelDetail> result = recipelDetailService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<RecipelDetail> result = recipelDetailService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody RecipelDetail entity) {
        String id = recipelDetailService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody RecipelDetail entity) {
        int rows = recipelDetailService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<RecipelDetail> entitys) {
        List<String> ids = recipelDetailService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<RecipelDetail> entitys) {
        List<String> ids = recipelDetailService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<RecipelDetail> entitys) {
        int rows = recipelDetailService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @GetMapping(value = "/getByRecipelInfoId/{recipelInfoId}")
    public ResponseEntity<JSONObject> getByRecipelInfoId(@PathVariable("recipelInfoId") String recipelInfoId){
        List<RecipelDetail> byRecipelInfoId = recipelDetailService.getByRecipelInfoId(recipelInfoId);
        for (RecipelDetail recipelDetail:
        byRecipelInfoId) {
            {
                recipelDetail.setDrugStuffId(medicalRecordService.getDrugStuffEvt(recipelDetail));
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(byRecipelInfoId));
    }
}