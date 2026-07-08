package com.geeke.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.geeke.common.controller.CrudController;
import com.geeke.sys.entity.PropertySet;
import com.geeke.sys.service.PropertySetService;

/**
 * 属性集管理Controller
 * @author lys
 * @version 2021-12-26
 */
@RestController
@RequestMapping(value = "/sys/propertySet")
public class PropertySetController extends CrudController<PropertySetService, PropertySet> {

	@Autowired
	protected PropertySetService propertySetService;

	@Override
	protected PropertySetService getService() {
		return propertySetService;
	}
}
