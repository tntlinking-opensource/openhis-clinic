package com.geeke.cure.dao;

import com.geeke.cure.entity.InspectionCheckDetailCostItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.cure.entity.InspectionCheckDetail;

import java.util.List;

/**
 * 检验检查详情DAO接口
 * @author rys
 * @version 2022-10-19
 */
@Mapper
public interface InspectionCheckDetailDao extends CrudDao<InspectionCheckDetail> {
    int insert(InspectionCheckDetail inspectionCheckDetail);

    List<InspectionCheckDetail> getByInfoId(String infoId);

    List<InspectionCheckDetailCostItem> getByInfoId2(String infoId);

    void deleteBy(String id);

}