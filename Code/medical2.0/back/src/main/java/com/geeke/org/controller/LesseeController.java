package com.geeke.org.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.common.controller.CrudController;
import com.geeke.org.entity.Lessee;
import com.geeke.org.service.LesseeService;

/**
 * 租户管理Controller
 * @author txl
 * @version 2022-05-23
 */
@RestController
@RequestMapping(value = "/org/lessee")
public class LesseeController extends CrudController<LesseeService, Lessee> {

	@Autowired
	protected LesseeService lesseeService;

	@Override
	protected LesseeService getService() {
		return lesseeService;
	}
}
