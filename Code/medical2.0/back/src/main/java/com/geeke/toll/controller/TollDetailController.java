package com.geeke.toll.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.common.controller.CrudController;
import com.geeke.toll.entity.TollDetail;
import com.geeke.toll.service.TollDetailService;

/**
 * 收费管理Controller
 * @author lc
 * @version 2022-06-15
 */
@RestController
@RequestMapping(value = "/toll/tollDetail")
public class TollDetailController extends CrudController<TollDetailService, TollDetail> {

	@Autowired
	protected TollDetailService tollDetailService;

	@Override
	protected TollDetailService getService() {
		return tollDetailService;
	}

}
