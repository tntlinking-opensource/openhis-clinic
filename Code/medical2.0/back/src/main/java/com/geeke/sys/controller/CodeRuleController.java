package com.geeke.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.geeke.common.controller.CrudController;
import com.geeke.sys.entity.CodeRule;
import com.geeke.sys.service.CodeRuleService;

/**
 * 系统编码规则Controller
 * @author lys
 * @version 2021-05-17
 */
@RestController
@RequestMapping(value = "/sys/codeRule")
public class CodeRuleController extends CrudController<CodeRuleService, CodeRule> {

	@Autowired
	protected CodeRuleService codeRuleService;

	@Override
	protected CodeRuleService getService() {
		return codeRuleService;
	}
}
