package com.geeke.org.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公司管理Controller
 * @author lys
 * @version 2022-05-25
 */
@RestController
@RequestMapping(value = "/org/tenant")
public class TenantController extends CrudController<CompanyService, Company> {

	@Autowired
	private CompanyService companyService;

	@Override
	protected CompanyService getService() {
		return companyService;
	}

	@Override
	@PostMapping(value = {"list", ""})
	public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
		Page<Company> result = companyService.listTenantPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
		return ResponseEntity.ok(ResultUtil.successJson(result));
	}

	@Override
	@PostMapping(value = "listAll")
	public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
		List<Company> result = companyService.listAllTenant(searchParams.getParams(), searchParams.getOrderby());
		return ResponseEntity.ok(ResultUtil.successJson(result));
	}

	@Override
	@PostMapping(value = "save")
	public ResponseEntity<JSONObject> save(@RequestBody Company entity) {
		String id = companyService.saveTenant(entity).getId();
		return ResponseEntity.ok(ResultUtil.successJson(id));
	}
}
