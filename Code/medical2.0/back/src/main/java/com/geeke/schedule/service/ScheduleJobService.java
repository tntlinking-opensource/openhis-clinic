package com.geeke.schedule.service;

import java.util.Map;

import com.geeke.utils.CronUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.schedule.dao.ScheduleJobDao;
import com.geeke.schedule.entity.ScheduleJob;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 定时任务管理Service
 * @author shenzy
 * @version 2021-10-28
 */
 
@Service("scheduleJobService")
@Transactional(readOnly = true)
public class ScheduleJobService extends CrudService<ScheduleJobDao, ScheduleJob>{


    @Override
    @Transactional
    public ScheduleJob save(ScheduleJob entity) {
        String cron = entity.getCron();
        if (StringUtils.isEmpty(cron)|| ! CronUtils.isValid(cron)){
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_90003, "cron表达式参数异常"));
        }
        return super.save(entity);
    }
}