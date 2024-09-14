package com.geeke.collect.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.collect.entity.SysCollect;

import java.util.List;

/**
 * 收藏夹DAO接口
 *
 * @author szy
 * @version 2021-07-28
 */
@Mapper
public interface SysCollectDao extends CrudDao<SysCollect> {

    List<SysCollect> listAllByUserId(@Param("userId") String userId);

    void updateBatch(@Param("sysCollectList") List<SysCollect> sysCollectList);
}