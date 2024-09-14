package com.geeke.outpatient.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.common.persistence.CrudDao;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.entity.RemoteDiagnosisTreatment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 远程诊断管理DAO接口
 * @author zh
 * @version 2023-12-06
 */
@Mapper
public interface RemoteDiagnosisTreatmentDao extends CrudDao<Patient> {
    /** id查询 */
    RemoteDiagnosisTreatment diagnosisById(@Param("id")String id);
    /** 挂号查询 */
    RemoteDiagnosisTreatment getRegistrationId(@Param("registrationId")String registrationId);
    /** 数量查询 */
    int diagnosisCount(PageRequest pageRequest);
    /** 分页查询 */
    List<RemoteDiagnosisTreatment> diagnosisPage(PageRequest pageRequest);
    /** 新增数据 */
    int diagnosisInsert(RemoteDiagnosisTreatment entity);
    /** 修改数据 */
    int diagnosisUpdate(RemoteDiagnosisTreatment entity);
    /** 修改状态 */
    int modifiedState(RemoteDiagnosisTreatment entity);
    /** 远程诊疗收费 */
    int chargeState(@Param("id")String id);
    /** 获取处方信息 */
    List<RecipelInfo> getCase(@Param("id")String id);
    /** 修改收费状态 */
    int chargeEdit(@Param("id")String id);
    /** 删除记录 */
    int diagnosisDelete(@Param("id")String id);

}