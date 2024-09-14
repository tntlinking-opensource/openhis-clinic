package com.geeke.org.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.org.entity.Clinic;

/**
 * 诊所管理DAO接口
 * @author txl
 * @version 2022-05-19
 */
@Mapper
public interface ClinicDao extends CrudDao<Clinic> {
}