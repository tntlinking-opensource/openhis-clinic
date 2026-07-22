package com.geeke.sys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.sys.entity.DictItem;
import com.geeke.sys.service.DictItemService;
import com.geeke.utils.ResultUtil;

/**
 * 字典项Controller
 * @author lys
 * @version 2020-06-30
 */
@RestController
@RequestMapping(value = "/sys/dictItem")
public class DictItemController extends CrudController<DictItemService, DictItem> {

	@Autowired
	protected DictItemService dictItemService;

	@Override
	protected DictItemService getService() {
		return dictItemService;
	}

	/** 根据字典类型code获取所有字典项 */
	@GetMapping("/listByCode/{code}")
	public ResponseEntity<JSONObject> listByCode(@PathVariable String code) {
		List<DictItem> items = dictItemService.listByDictTypeCode(code);
		return ResponseEntity.ok(ResultUtil.successJson(items));
	}
}
