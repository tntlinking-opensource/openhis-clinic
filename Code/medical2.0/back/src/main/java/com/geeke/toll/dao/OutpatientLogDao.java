package com.geeke.toll.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.common.persistence.CrudDao;
import com.geeke.outpatient.entity.Patient;
import com.geeke.toll.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 门诊日志DAO接口
 * @author lc
 * @version 2022-06-15
 */
@Mapper
public interface OutpatientLogDao extends CrudDao<OutpatientLog> {
    public OutpatientLog getLogs(String id);
    /** id查询 */
    int logCount(PageRequest var1);

    List<OutpatientLog> getOutpatientLog(OutpatientLogRc outpatientLogRc);

    /** 查询所有集合 */
    List<OutpatientLog> getOutpatientlist(OutpatientLogRc outpatientLogRc);

    /** 修改日志信息 */
    int updateLog(OutpatientLog outpatientLogRc);

    List<Patient> getPovert(Patient patient);

    List<Patient> getPoverty(String patientId);

}