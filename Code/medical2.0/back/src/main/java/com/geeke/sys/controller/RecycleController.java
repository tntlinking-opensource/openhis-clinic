package com.geeke.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.sys.entity.Action;
import com.geeke.sys.service.RecycleService;
import com.geeke.utils.ResultUtil;

/**
 * 操作日志Controller
 * @author lys   手工编写
 * @version 2019-12-12
 */
@RestController
@RequestMapping(value = "/sys/recycle")
public class RecycleController extends BaseController {

	@Autowired
	private RecycleService recycleService;
	
    @PostMapping(value = "restor")
    public ResponseEntity<JSONObject> restor(@RequestBody Action entity) {
        int rows = recycleService.restore(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }
}