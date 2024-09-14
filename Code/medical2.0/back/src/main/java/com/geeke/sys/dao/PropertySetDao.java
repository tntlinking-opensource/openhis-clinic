package com.geeke.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.sys.entity.PropertySet;

/**
 * 属性集管理DAO接口
 * @author lys
 * @version 2021-12-26
 */
@Mapper
public interface PropertySetDao extends CrudDao<PropertySet> {
}