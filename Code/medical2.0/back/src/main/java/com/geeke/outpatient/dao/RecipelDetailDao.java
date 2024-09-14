package com.geeke.outpatient.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.entity.RecipelStastics;

import java.math.BigDecimal;
import java.util.List;

/**
 * 处方详情DAO接口
 * @author txl
 * @version 2022-06-07
 */
@Mapper
public interface RecipelDetailDao extends CrudDao<RecipelDetail> {

    List<RecipelDetail> getByRecipelInfoId(@Param("recipelInfoId") String recipelInfoId);

    void updateActualPayment(@Param("actualPayment")  BigDecimal actualPayment, @Param("id") String id);

    List<RecipelDetail> getRecipelDetail(@Param("id") String id);

    int schedule(@Param("id") String id);

    List<RecipelDetail> getInfuseByRecipelInfoId(@Param("recipelInfoId") String recipelInfoId);

    List<RecipelStastics> getDetailStasticsForOccupy(@Param("companyId")String companyId,
                                                     @Param("days") int days);
    List<RecipelStastics> getDetailStasticsForNowByRecordId(@Param("recordId")String recordId,
                                                            @Param("companyId")String companyId);
    int batchDelete(@Param("entitys") List<RecipelDetail> var1);

    int updateById(@Param("executions") BigDecimal executions, @Param("id") String id, @Param("infuseType") int infuseType);

    List<RecipelDetail> findByInfoId(@Param("recipelInfoId") String recipelInfoId,@Param("stuffType") String stuffType);
}