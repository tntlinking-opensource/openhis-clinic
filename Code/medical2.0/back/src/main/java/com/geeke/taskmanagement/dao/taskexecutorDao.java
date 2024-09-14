package com.geeke.taskmanagement.dao;

import com.geeke.taskmanagement.entity.taskexecutor;
import com.geeke.taskmanagement.entity.taskmanagement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface taskexecutorDao {
    public List<taskexecutor> list(String taskId);

    void delete(taskmanagement task);

}
