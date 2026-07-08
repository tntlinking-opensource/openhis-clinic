package com.geeke.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.geeke.common.controller.CrudController;
import com.geeke.sys.entity.QueryCondition;
import com.geeke.sys.service.QueryConditionService;

/**
 * 查询条件Controller
 * @author lys
 * @version 2021-07-05
 */
@RestController
@RequestMapping(value = "/sys/queryCondition")
public class QueryConditionController extends CrudController<QueryConditionService, QueryCondition> {

	@Autowired
	protected QueryConditionService queryConditionService;

	@Override
	protected QueryConditionService getService() {
		return queryConditionService;
	}
}
