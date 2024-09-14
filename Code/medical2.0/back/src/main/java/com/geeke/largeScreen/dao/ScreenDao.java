package com.geeke.largeScreen.dao;

import com.geeke.largeScreen.entity.Basics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据大屏DAO接口
 * @author zhanghan
 * @version 2023-09-05
 */
@Mapper
public interface ScreenDao {

    /** 新增任务 */
    public String getQuest(String parentId);

    /** 今日挂号人数 */
    public String getRegister(String parentId);

    /** 今日接诊数 */
    public String getReceivePatients(String parentId);

    /** 今日收入 */
    public String getIncome(String parentId);

    /** 今日新增签约数 */
    public String getSigning(String parentId);

}