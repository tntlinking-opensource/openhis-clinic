package com.geeke.outpatient.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.persistence.CrudDao;
import com.geeke.outpatient.entity.RecipelInfoReview;
import com.geeke.outpatient.vo.PrescriptionStatisticsVO;
import com.geeke.outpatient.vo.StatementVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 处方审查接口
 * @author lc
 */
@Mapper
public interface RecipelInfoReviewDao extends CrudDao<RecipelInfoReview> {
    RecipelInfoReview getByRecipelInfoId(String recipelInfoId);

    List<StatementVO> listPageStatement(@Param("params") List<Parameter> params,@Param("groupByColumns") List<String> groupByColumns);

    PrescriptionStatisticsVO prescriptionStatistics(String id,String startTime,String endTime);
}