package com.geeke.sys.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.sys.entity.Attachment;
import com.geeke.sys.entity.MyTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyTaskDao extends CrudDao<MyTask> {
}
