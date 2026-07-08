package com.geeke.org.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.org.entity.Department;
import com.geeke.org.service.DepartmentService;
import com.geeke.utils.ResultUtil;

/**
 * 部门管理Controller
 * @author lys
 * @version 2021-07-13
 */
@RestController
@RequestMapping(value = "/org/department")
public class DepartmentController extends CrudController<DepartmentService, Department> {

	@Autowired
	protected DepartmentService departmentService;

	@Override
	protected DepartmentService getService() {
		return departmentService;
	}

    @PostMapping(value = "tree")
    public ResponseEntity<JSONObject> tree(@RequestBody SearchParams searchParams) {
        List<Department> result = departmentService.tree(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

}