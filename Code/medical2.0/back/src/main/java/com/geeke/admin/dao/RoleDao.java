package com.geeke.admin.dao;

import com.geeke.admin.entity.Role;
import com.geeke.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色管理DAO接口
 * @author lys
 * @version 2022-05-24
 */
@Mapper
public interface RoleDao extends CrudDao<Role> {
    int deleteRoleByRoleCode(@Param("roleCode") String roleCode,@Param("tenantId")String tenantId);

    List<Role> listAllByTenantId(@Param("tenantId")String tenantId);
}