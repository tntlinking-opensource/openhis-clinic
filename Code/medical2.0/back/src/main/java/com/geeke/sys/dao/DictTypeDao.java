package com.geeke.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.sys.entity.DictType;

/**
 * 字典类型DAO接口
 * @author lys
 * @version 2021-08-05
 */
@Mapper
public interface DictTypeDao extends CrudDao<DictType> {
}