package com.geeke.sys.controller;

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
import com.geeke.common.data.Page;
import com.geeke.sys.entity.CodeRule;
import com.geeke.sys.entity.CompanyCodeRule;
import com.geeke.sys.service.CodeRuleService;
import com.geeke.sys.service.CompanyCodeRuleService;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.utils.ResultUtil;
import com.google.common.collect.Lists;

/**
 * 公司编码规则Controller
 * @author lys
 * @version 2021-05-17
 */
@RestController
@RequestMapping(value = "/sys/companyCodeRule")
public class CompanyCodeRuleController extends CrudController<CompanyCodeRuleService, CompanyCodeRule> {

	@Autowired
	protected CompanyCodeRuleService companyCodeRuleService;

	@Autowired
	private CodeRuleService codeRuleService;

	@Override
	protected CompanyCodeRuleService getService() {
		return companyCodeRuleService;
	}

    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
    	initCompanyCodeRule(SessionUtils.getUserJson().getString("companyId"));

        Page<CompanyCodeRule> result = companyCodeRuleService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
    	initCompanyCodeRule(SessionUtils.getUserJson().getString("companyId"));

        List<CompanyCodeRule> result = companyCodeRuleService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

	private void initCompanyCodeRule(String companyId) {
		List<CodeRule> rules =  codeRuleService.listNotInCompany(companyId);
		List<CompanyCodeRule> ccrs = Lists.newArrayList();
		for(CodeRule cr : rules) {
			CompanyCodeRule ccr = new CompanyCodeRule();
			ccr.setCompanyId(companyId);
			ccr.setSysRule(cr);
			ccr.setCode(cr.getCode());
			ccr.setName(cr.getName());
			ccr.setRuleDef(cr.getRuleDef());
			ccr.setRemarks(cr.getRemarks());
			ccrs.add(ccr);
			companyCodeRuleService.save(ccr);
		}
		if(ccrs.size() > 0) {
			companyCodeRuleService.bulkInsert(ccrs);
		}
	}
}