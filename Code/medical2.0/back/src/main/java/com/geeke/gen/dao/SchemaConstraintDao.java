package com.geeke.gen.dao;

import com.geeke.gen.entity.Constraint;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统表约束字段DAO接口
 * @author lys
 * @version 2019-08-31
 */
@Mapper
public interface SchemaConstraintDao {
	
	/**
	 * 插入数据
	 * @param entity
	 * @return
	 */
	public List<Constraint> listAll(String tableName);
}