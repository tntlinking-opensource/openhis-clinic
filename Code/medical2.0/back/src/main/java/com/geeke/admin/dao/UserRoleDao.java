package com.geeke.admin.dao;

import com.geeke.admin.entity.UserRole;
import com.geeke.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 用户管理DAO接口
 * @author lys
 * @version 2021-08-25
 */
@Mapper
public interface UserRoleDao extends CrudDao<UserRole> {
    @Update("update sys_user_role set role_id = #{roleId} where user_id = #{userId}")
    void updateRelationForTenant(String roleId,String userId);
}