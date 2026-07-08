package com.geeke.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.sys.entity.DictType;
import com.geeke.sys.service.DictTypeService;
import com.geeke.utils.ResultUtil;

/**
 * 字典类型Controller
 * @author lys
 * @version 2021-08-20
 */
@RestController
@RequestMapping(value = "/sys/dictType")
public class DictTypeController extends CrudController<DictTypeService, DictType> {

	@Autowired
	protected DictTypeService dictTypeService;

	@Override
	protected DictTypeService getService() {
		return dictTypeService;
	}

    @PostMapping(value = "importJson")
    public ResponseEntity<JSONObject> importJson(@RequestBody DictType entity) {
        String id = dictTypeService.importJson(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
}