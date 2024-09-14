package com.geeke.org.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.org.entity.Lessee;

/**
 * 租户管理DAO接口
 * @author txl
 * @version 2022-05-23
 */
@Mapper
public interface LesseeDao extends CrudDao<Lessee> {
}