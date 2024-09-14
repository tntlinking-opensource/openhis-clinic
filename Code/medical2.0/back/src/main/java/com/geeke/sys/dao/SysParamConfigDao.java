package com.geeke.sys.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.sys.entity.PersonalTheme;
import com.geeke.sys.entity.SysParamConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置DAO接口
 * @author lc
 */
@Mapper
public interface SysParamConfigDao extends CrudDao<SysParamConfig> {
}