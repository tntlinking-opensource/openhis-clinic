package com.geeke.gen.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.gen.entity.GenScheme;
import org.apache.ibatis.annotations.Mapper;

/**
 * 代码方案管理DAO接口
 * @author lys
 * @version 2019-12-06
 */
@Mapper
public interface GenSchemeDao extends CrudDao<GenScheme> {
	
}