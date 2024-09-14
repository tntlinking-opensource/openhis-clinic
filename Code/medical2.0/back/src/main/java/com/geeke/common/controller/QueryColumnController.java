package com.geeke.common.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.gen.entity.GenTableColumn;
import com.geeke.gen.service.GenTableColumnService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.common.service.QueryColumnViewService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;

/**
 * 查询条件的业务字段Controller
 * @author lys
 * @version 2020-8-17
 */
@RestController
@RequestMapping(value = "/common/columnQuery")
public class QueryColumnController extends BaseController {

	@Autowired
	private QueryColumnViewService queryColumnViewService;

    
	@GetMapping("")
    public ResponseEntity<JSONObject> listAll(@RequestParam(value="tableId", required = true) String tableId, 
    		@RequestParam(value="schemeId", required = true) String schemeId) {
        List<GenTableColumn> result = queryColumnViewService.listColumn(tableId, schemeId);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }   
}