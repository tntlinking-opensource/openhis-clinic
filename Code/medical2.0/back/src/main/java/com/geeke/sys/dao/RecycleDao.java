package com.geeke.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 操作日志DAO接口
 * @author lys	人工编写
 * @version 2019-12-12
 */
@Mapper
public interface RecycleDao {
	
	/**
	 * 修改操作日志status为已还原状态
	 * @param id  操作日志Id
	 * @return
	 */
	int updateActionStaus(@Param("id")String id);
	
	
	/**
	 * 恢复业务对象，把del_flag字段修改为0
	 * @param tableName  业务表名
	 * @param id		 业务对象Id
	 * @return
	 */
	int restore(@Param("tableName")String tableName, @Param("id")String id);
}