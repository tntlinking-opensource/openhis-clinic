package com.geeke.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.common.controller.CrudController;
import com.geeke.schedule.entity.ScheduleJob;
import com.geeke.schedule.service.ScheduleJobService;

/**
 * 定时任务管理Controller
 * @author shenzy
 * @version 2021-10-28
 */
@RestController
@RequestMapping(value = "/schedule/scheduleJob")
public class ScheduleJobController extends CrudController<ScheduleJobService, ScheduleJob> {

	@Autowired
	protected ScheduleJobService scheduleJobService;

	@Override
	protected ScheduleJobService getService() {
		return scheduleJobService;
	}

}
