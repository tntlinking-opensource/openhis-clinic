package com.geeke.admin.dao;

import com.geeke.admin.entity.Router;
import com.geeke.common.persistence.TreeDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 路由管理DAO接口
 * @author lys
 * @version 2021-11-18
 */
@Mapper
public interface RouterDao extends TreeDao<Router> {

     List<Router> listAllByVersion(@Param("roleId") String roleId);

    List<String> getUserIndateWarning(String userId);

}