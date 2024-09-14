package com.geeke.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.sys.entity.Attachment;

/**
 * 文件附件DAO接口
 * @author lys
 * @version 2021-04-28
 */
@Mapper
public interface AttachmentDao extends CrudDao<Attachment> {
	
	/**
	 * 根据业务对象id删除附件
	 * @param bizId
	 * @see public int delete(T entity)
	 * @return
	 */
	
	public int deleteByBizId(String bizId);
}