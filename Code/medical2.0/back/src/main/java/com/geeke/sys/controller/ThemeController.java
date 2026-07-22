package com.geeke.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.geeke.common.controller.CrudController;
import com.geeke.sys.entity.Theme;
import com.geeke.sys.service.ThemeService;

/**
 * 系统主题Controller
 * @author lys
 * @version 2021-09-19
 */
@RestController
@RequestMapping(value = "/sys/theme")
public class ThemeController extends CrudController<ThemeService, Theme> {

	@Autowired
	protected ThemeService themeService;

	@Override
	protected ThemeService getService() {
		return themeService;
	}
}
