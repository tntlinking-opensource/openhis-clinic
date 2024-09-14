package com.geeke.outpatient.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.outpatient.controller.schedulingController;
import com.geeke.outpatient.entity.schedulingmxDTO;
import com.geeke.outpatient.entity.scheduling;
import com.geeke.outpatient.entity.schedulingDTO;
import org.apache.ibatis.annotations.Mapper;

import com.geeke.common.persistence.CrudDao;

import java.util.Date;
import java.util.List;

/**
 * 排班数据DAO接口
 * @author txl
 * @version 2022-06-20
 */
@Mapper
public interface SchedulingDao extends CrudDao<scheduling> {


    List<schedulingDTO> getpbuserlist(schedulingDTO pageRequest);

    List<scheduling> listscheduling(schedulingDTO sche);

    List<scheduling> listcount(scheduling scheduling);

    List<schedulingmxDTO> listschedulingmx(schedulingDTO sche);

    List<scheduling> getList(Date schedulingtime);
}