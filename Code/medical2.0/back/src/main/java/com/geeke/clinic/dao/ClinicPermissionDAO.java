package com.geeke.clinic.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//import com.geeke.sys.entity.Resource;


/**
 * 权限管理DAO接口
 *
 * @author lys
 * @date 2019/7/26
 */
@Mapper
public interface ClinicPermissionDAO {
    @Select("select id from sys_role where parent_id = #{parentId}")
    List<String> queryChildNodesByPid(@Param("parentId") String parentId);
}
