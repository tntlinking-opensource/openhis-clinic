package com.geeke.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.common.controller.CrudController;
import com.geeke.sys.entity.PersonalTheme;
import com.geeke.sys.service.PersonalThemeService;

/**
 * 系统主题Controller
 * @author lys
 * @version 2021-07-16
 */
@RestController
@RequestMapping(value = "/sys/personalTheme")
public class PersonalThemeController extends CrudController<PersonalThemeService, PersonalTheme> {

	@Autowired
	protected PersonalThemeService personalThemeService;

	@Override
	protected PersonalThemeService getService() {
		return personalThemeService;
	}

}
