package com.geeke.common.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.entity.ColumnView;

/**
 * 表列头管理dao接口
 * 
 * @author lys
 * @date 2019/2/2
 */
@Mapper
public interface ColumnViewDao {

	/**
	 * 根据视图、表、用户 查询表列头实现状态
	 * @param userId
	 * @param routerId
	 * @param tableId
	 * @return
	 */
	List<ColumnView> list(@Param("userId")String userId, @Param("routerId")String routerId, @Param("tableId")String tableId);
	
	int insert(ColumnView cv);
	
	int update(ColumnView cv);
	
	/**
	 * 保存全部显示列
	 * @param userId
	 * @param routerId
	 * @param tableId
	 * @return
	 */
	List<ColumnView> saveAll(@Param("userId")String userId, @Param("routerId")String routerId, @Param("tableId")String tableId);
	
	/**
	 * 删除设置的显示列
	 * @param userId
	 * @param routerId
	 * @param tableId
	 */
	void deleteAll(@Param("userId")String userId, @Param("routerId")String routerId, @Param("tableId")String tableId);
	
	/**
	 * 删除设置的显示列
	 * @param id
	 */
	void delete(@Param("id")String id);
	
	int bulkUpdate(@Param("list")List<ColumnView> list);
	int bulkInsert(@Param("list")List<ColumnView> list);
}
