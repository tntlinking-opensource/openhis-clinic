package com.geeke.basicdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.basicdata.service.ManufactureFactoryService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.utils.ResultUtil;

/**
 * 生产厂家Controller
 * @author txl
 * @version 2022-06-22
 */
@RestController
@RequestMapping(value = "/basicdata/manufactureFactory")
public class ManufactureFactoryController extends CrudController<ManufactureFactoryService, ManufactureFactory> {

	@Autowired
	private ManufactureFactoryService manufactureFactoryService;

	@Override
	protected ManufactureFactoryService getService() {
		return manufactureFactoryService;
	}

	@Override
	@PostMapping(value = "listAll")
	public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
		List<ManufactureFactory> result = getService().listAlls(searchParams.getParams(), searchParams.getOrderby());
		return ResponseEntity.ok(ResultUtil.successJson(result));
	}
}
