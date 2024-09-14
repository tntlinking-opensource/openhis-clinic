package com.geeke.common.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.entity.ColumnView;
import com.geeke.common.service.ColumnViewService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;



@RestController
@RequestMapping("/common/column")
public class ColumnViewController extends BaseController {

	@Autowired
    private ColumnViewService columnViewService;

	
    @GetMapping("")
    public ResponseEntity<JSONObject> list(@RequestParam(required = true) String routerId, @RequestParam(required = true) String tableId) {
    	List<ColumnView> list = columnViewService.listColumn(routerId, tableId);
    	return ResponseEntity.ok(ResultUtil.successJson(list));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody ColumnView entity) {
        int rows = columnViewService.save(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }
    
    /**
     * 全选显示列
     * @param routerId
     * @param tableId
     * @return
     */
    @GetMapping(value = "selectAll")
    public ResponseEntity<JSONObject> selectAll(@RequestParam(required = true) String routerId, @RequestParam(required = true) String tableId) {   	
		List<ColumnView> list = columnViewService.listColumn(routerId, tableId, true);
		return ResponseEntity.ok(ResultUtil.successJson(list));
    } 
    
    /**
     * 选择默认显示列
     * @param routerId
     * @param tableId
     * @return
     */
    @GetMapping(value = "selectDefault")
    public ResponseEntity<JSONObject> selectDefault(@RequestParam(required = true) String routerId, @RequestParam(required = true) String tableId) {
    	columnViewService.reset(routerId, tableId);
    	List<ColumnView> list = columnViewService.listColumn(routerId, tableId);
        return ResponseEntity.ok(ResultUtil.successJson(list));
    }     

}
