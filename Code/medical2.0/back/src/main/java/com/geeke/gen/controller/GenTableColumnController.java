package com.geeke.gen.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.gen.entity.GenTableColumn;
import com.geeke.gen.service.GenTableColumnService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 业务字段表Controller
 * @author lys
 * @version 2019-09-13
 */
@RestController
@RequestMapping(value = "/gen/genTableColumn")
public class GenTableColumnController extends BaseController {

	@Autowired
	private GenTableColumnService genTableColumnService;
	
	@GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        GenTableColumn entity = genTableColumnService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
	
	@PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<GenTableColumn> result = genTableColumnService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<GenTableColumn> result = genTableColumnService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }   

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody GenTableColumn entity) {
    	String id = genTableColumnService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody GenTableColumn entity) {
        int rows = genTableColumnService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

	
}