package com.geeke.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.geeke.common.controller.CrudController;
import com.geeke.sys.entity.Action;
import com.geeke.sys.service.ActionService;

/**
 * 操作日志Controller
 * @author lys
 * @version 2020-06-29
 */
@RestController
@RequestMapping(value = "/sys/action")
public class ActionController extends CrudController<ActionService, Action> {

	@Autowired
	protected ActionService actionService;

	@Override
	protected ActionService getService() {
		return actionService;
	}
}
