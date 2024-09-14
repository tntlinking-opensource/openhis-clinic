package com.geeke.taskmanagement.dao;

import com.geeke.taskmanagement.entity.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskmangementDao {
    /*
    新增任务
     */
    int insertask(taskmanagement taskmanagement);
    /*
    获取任务条数
     */
    List<taskmanagement> selecttasktotal(gettaskrcvo rcvo);
    /*
    根据分页获取数据
     */
    List<taskmanagement> selecttasklist(gettaskrcvo rcvo);

    List<Sysusertreeentity> getusertree(gettaskrcvo rcvo);

    int savetaskexecutor(taskexecutor taskexecutor);

    List<feedbacktable> getfeedbacktable(gettaskrcvo rcvo);

    int updateaudit(taskmanagement task);

    int savefeedbacktable(feedbacktable fee);

    int update(taskmanagement task);

    int release(taskmanagement taskmanagement);

    int delete(taskmanagement taskmanagement);

    taskmanagement getById(String countId);

}
