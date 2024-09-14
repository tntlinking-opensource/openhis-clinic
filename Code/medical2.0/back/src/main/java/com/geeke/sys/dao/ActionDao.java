package com.geeke.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.sys.entity.Action;

/**
 * 操作日志DAO接口
 * @author lys
 * @version 2019-12-06
 */
@Mapper
public interface ActionDao extends CrudDao<Action> {
	
}