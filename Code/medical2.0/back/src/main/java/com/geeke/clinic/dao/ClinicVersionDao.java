package com.geeke.clinic.dao;

import com.geeke.clinic.entity.ClinicVersion;
import com.geeke.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 诊所版本DAO接口
 * @author txl
 * @version 2022-05-23
 */
@Mapper
public interface ClinicVersionDao extends CrudDao<ClinicVersion> {
    @Select("select role_id from clinic_version where id = #{id}")
    String getRoleIdById(@Param("id")String id);
}