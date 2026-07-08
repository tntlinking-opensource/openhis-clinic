package com.geeke.org.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.common.controller.CrudController;
import com.geeke.org.entity.AdministrativeDivision;
import com.geeke.org.service.AdministrativeDivisionService;

/**
 * 行政区域划分Controller
 * @author txl
 * @version 2022-06-21
 */
@RestController
@RequestMapping(value = "/org/administrativeDivision")
public class AdministrativeDivisionController extends CrudController<AdministrativeDivisionService, AdministrativeDivision> {

	@Autowired
	protected AdministrativeDivisionService administrativeDivisionService;

	@Override
	protected AdministrativeDivisionService getService() {
		return administrativeDivisionService;
	}
}
