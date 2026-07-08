package com.geeke.cure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.cure.entity.InspectionCheck;
import com.geeke.cure.service.InspectionCheckService;
import com.geeke.common.controller.CrudController;

/**
 * 检验检查Controller
 * @author rys
 * @version 2022-10-18
 */
@RestController
@RequestMapping(value = "/cure/inspectionCheck")
public class InspectionCheckController extends CrudController<InspectionCheckService, InspectionCheck> {

	@Autowired
	protected InspectionCheckService inspectionCheckService;

	@Override
	protected InspectionCheckService getService() {
		return inspectionCheckService;
	}

}
