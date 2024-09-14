package com.geeke.admin.dao;

import com.geeke.admin.entity.UserExt;
import com.geeke.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户拓展DAO接口
 * @author ch
 * @version 2022-06-10
 */
@Mapper
public interface UserExtDao extends CrudDao<UserExt> {

    /**
     * 根据用户id查扩展信息
     * @param userId
     * @return
     */
    UserExt getUserExtByUserId(@Param("userId")String userId,@Param("companyId")String companyId);

    /**
     * 查询所有的拓展数据
     * @param userId
     * @return
     */
    List<UserExt> getAllUserExtByUserId(@Param("userId")String userId);
}