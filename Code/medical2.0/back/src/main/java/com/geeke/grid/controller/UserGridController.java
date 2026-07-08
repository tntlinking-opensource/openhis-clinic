package com.geeke.grid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.grid.entity.UserGrid;
import com.geeke.grid.service.UserGridService;
import com.geeke.common.controller.CrudController;

/**
 * 自定义布局Controller
 * @author ycy
 * @version 2021-10-12
 */
@RestController
@RequestMapping(value = "/grid/userGrid")
public class UserGridController extends CrudController<UserGridService, UserGrid> {

	@Autowired
	protected UserGridService userGridService;

	@Override
	protected UserGridService getService() {
		return userGridService;
	}

}
