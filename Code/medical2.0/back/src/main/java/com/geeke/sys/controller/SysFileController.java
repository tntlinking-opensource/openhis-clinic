package com.geeke.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.geeke.common.controller.CrudController;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.service.SysFileService;

/**
 * 系统附件Controller
 * @author szy
 * @version 2021-09-22
 */
@RestController
@RequestMapping(value = "/sys/sysFile")
public class SysFileController extends CrudController<SysFileService, SysFile> {

	@Autowired
	protected SysFileService sysFileService;

	@Override
	protected SysFileService getService() {
		return sysFileService;
	}
}
