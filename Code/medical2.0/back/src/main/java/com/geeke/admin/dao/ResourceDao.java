package com.geeke.admin.dao;

import com.geeke.admin.entity.Resource;
import com.geeke.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源管理DAO接口
 * @author lys
 * @version 2021-11-18
 */
@Mapper
public interface ResourceDao extends CrudDao<Resource> {

    List<Resource> listAllByVersion(@Param("roleId") String roleId);


}