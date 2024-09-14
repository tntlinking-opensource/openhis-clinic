package com.geeke.toll.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.toll.entity.TollDetail;

/**
 * 收费管理DAO接口
 * @author lc
 * @version 2022-06-15
 */
@Mapper
public interface TollDetailDao extends CrudDao<TollDetail> {
}