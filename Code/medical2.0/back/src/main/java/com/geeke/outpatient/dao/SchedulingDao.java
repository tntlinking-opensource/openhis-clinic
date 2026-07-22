package com.geeke.outpatient.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.outpatient.entity.SchedulingmxDTO;
import com.geeke.outpatient.entity.Scheduling;
import com.geeke.outpatient.entity.SchedulingDTO;
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
public interface SchedulingDao extends CrudDao<Scheduling> {


    List<SchedulingDTO> getpbuserlist(SchedulingDTO pageRequest);

    List<Scheduling> listscheduling(SchedulingDTO sche);

    List<Scheduling> listcount(Scheduling scheduling);

    List<SchedulingmxDTO> listschedulingmx(SchedulingDTO sche);

    List<Scheduling> getList(Date schedulingtime);
}