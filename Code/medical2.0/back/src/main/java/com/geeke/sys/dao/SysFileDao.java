package com.geeke.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.sys.entity.SysFile;

/**
 * 系统附件DAO接口
 * @author szy
 * @version 2021-09-22
 */
@Mapper
public interface SysFileDao extends CrudDao<SysFile> {
    // 根据ID数组删除
    int deleteByIds(@Param("ids") String[] ids);
}