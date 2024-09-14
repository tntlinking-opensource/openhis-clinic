
package com.geeke.common.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.geeke.common.data.PageRequest;

/**
 * DAO支持类实现
 * @author lys
 * @version 2014-05-16
 * @param <T>
 */
public interface CrudDao<T> extends BaseDao {

	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	public T get(String id);
	
	
	/**
	 * 查询数据条数(用在分页查询)
	 * @param pageRequest
	 * @return
	 */
	public int count(PageRequest pageRequest);
	
	/**
	 * 分页查询数据列表
	 * @param pageRequest
	 * @return
	 */
	public List<T> listPage(PageRequest pageRequest);
	
	/**
	 * 查询所有数据列表
	 * @param parameters
	 * @return
	 */
	public List<T> listAll(PageRequest pageRequest);
	
	
	/**
	 * 插入数据
	 * @param entity
	 * @return
	 */
	public int insert(T entity);
	
	/**
	 * 更新数据
	 * @param entity
	 * @return
	 */
	public int update(T entity);
	
	/**
	 * 有删除字段时，一般为逻辑删除，更新del_flag字段为1；否则物理删除
	 * @param entity
	 * @see public int delete(T entity)
	 * @return
	 */
	
	public int delete(T entity);
	
	/**
	 * 批量增加
	 * @param entitys
	 * @return
	 */
	public int bulkInsert(@Param("entitys")List<T> entitys);
	
	/**
	 * 批量修改
	 * @param entitys
	 * @return
	 */
	public int bulkUpdate(@Param("entitys")List<T> entitys);
	
	/**
	 * 批量删除
	 * @param entitys
	 * @return
	 */
	public int bulkDelete(@Param("entitys")List<T> entitys);
}