package com.geeke.schedule.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.schedule.entity.ScheduleJob;
import org.apache.ibatis.annotations.Select;

/**
 * 定时任务管理DAO接口
 * @author shenzy
 * @version 2021-10-28
 */
@Mapper
public interface ScheduleJobDao extends CrudDao<ScheduleJob> {


    @Select("select * from schedule_job where code =#{code} and is_lock=0")
    ScheduleJob getCron(String code);
}