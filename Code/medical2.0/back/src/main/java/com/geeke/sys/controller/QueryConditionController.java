package com.geeke.sys.controller;

import java.util.List;

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
import com.geeke.sys.entity.QueryCondition;
import com.geeke.sys.service.QueryConditionService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;

/**
 * 查询条件Controller
 * @author lys
 * @version 2021-07-05
 */
@RestController
@RequestMapping(value = "/sys/queryCondition")
public class QueryConditionController extends BaseController {

	@Autowired
	private QueryConditionService queryConditionService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        QueryCondition entity = queryConditionService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<QueryCondition> result = queryConditionService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<QueryCondition> result = queryConditionService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody QueryCondition entity) {
    	String id = queryConditionService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody QueryCondition entity) {
        int rows = queryConditionService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

	
}