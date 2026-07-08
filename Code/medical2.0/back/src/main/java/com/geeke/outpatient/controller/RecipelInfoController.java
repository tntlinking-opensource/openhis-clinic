package com.geeke.outpatient.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.outpatient.service.RecipelInfoService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 处方信息Controller
 * @author txl
 * @version 2022-06-20
 */
@RestController
@RequestMapping(value = "/outpatient/recipelInfo")
public class RecipelInfoController extends CrudController<RecipelInfoService, RecipelInfo> {

	@Autowired
	private RecipelInfoService recipelInfoService;

	@Override
	protected RecipelInfoService getService() {
		return recipelInfoService;
	}

	@GetMapping("/update/notShow")
	public ResponseEntity<JSONObject> updateNotShowById(@RequestParam("id") String id) {
		int res = recipelInfoService.updateNotShowById(id);
		return ResponseEntity.ok(ResultUtil.successJson(res));
	}

	@GetMapping("/invalid/{id}")
	public ResponseEntity<JSONObject> invalid(@PathVariable String id) {
		int res = recipelInfoService.invalid(id);
		return ResponseEntity.ok(ResultUtil.successJson(res));
	}
}
