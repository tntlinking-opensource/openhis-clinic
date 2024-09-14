package com.geeke.outpatient.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.outpatient.entity.RecipelInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.outpatient.entity.RecipelInfo;

import java.util.List;

/**
 * 处方信息DAO接口
 * @author txl
 * @version 2022-06-20
 */
@Mapper
public interface RecipelInfoDao extends CrudDao<RecipelInfo> {
    //根据病例id获取所有的处方信息
    List<RecipelInfo> getByMedicalId(String registrationId);

    int updateById(@Param("cureType") int cureType,@Param("infuseType") int infuseType,@Param("id") String  id);

    List<RecipelInfoDTO> getHistoryRecipel(PageRequest pageRequest);

    void updateSchedule(@Param("id")String id, @Param("cureType")int cureType);
}