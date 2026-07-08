package com.geeke.cure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.cure.entity.InspectionCheckDetail;
import com.geeke.cure.service.InspectionCheckDetailService;
import com.geeke.common.controller.CrudController;

/**
 * 检验检查详情Controller
 * @author rys
 * @version 2022-10-19
 */
@RestController
@RequestMapping(value = "/cure/inspectionCheckDetail")
public class InspectionCheckDetailController extends CrudController<InspectionCheckDetailService, InspectionCheckDetail> {

	@Autowired
	protected InspectionCheckDetailService inspectionCheckDetailService;

	@Override
	protected InspectionCheckDetailService getService() {
		return inspectionCheckDetailService;
	}

}
