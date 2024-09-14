package com.geeke.largeScreen.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.largeScreen.entity.Basics;
import com.geeke.largeScreen.service.ScreenService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 数据大屏Controller
 * @author zhanghan
 * @version 2023-09-05
 */
@RestController
@RequestMapping(value = "/largeScreen")
public class ScreenController extends BaseController {

	@Autowired
	private ScreenService screenService;

	/** 基础数据获取 */
	@GetMapping("/{parentId}")
	public ResponseEntity<JSONObject> getBasics(@PathVariable("parentId") String parentId) {
		Basics entity = screenService.getBasics(parentId);
		return ResponseEntity.ok(ResultUtil.successJson(entity));
	}

}