package com.geeke.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.sys.entity.SysSeting;

/**
 * 系统设置DAO接口
 * @author szy
 * @version 2021-08-26
 */
@Mapper
public interface SysSetingDao extends CrudDao<SysSeting> {
}