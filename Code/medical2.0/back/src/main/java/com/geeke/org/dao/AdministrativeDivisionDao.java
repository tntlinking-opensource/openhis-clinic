package com.geeke.org.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.org.entity.AdministrativeDivision;

/**
 * 行政区域划分DAO接口
 * @author txl
 * @version 2022-06-21
 */
@Mapper
public interface AdministrativeDivisionDao extends CrudDao<AdministrativeDivision> {
}