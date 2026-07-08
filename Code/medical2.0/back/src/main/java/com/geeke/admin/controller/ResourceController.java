package com.geeke.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.admin.entity.Resource;
import com.geeke.admin.service.ResourceService;
import com.geeke.common.controller.CrudController;

/**
 * 资源管理Controller
 * @author lys
 * @version 2021-11-18
 */
@RestController
@RequestMapping(value = "/admin/resource")
public class ResourceController extends CrudController<ResourceService, Resource> {

	@Autowired
	protected ResourceService resourceService;

	@Override
	protected ResourceService getService() {
		return resourceService;
	}
}
