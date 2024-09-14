package com.geeke.cure.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.cure.entity.InspectionCheck;

import java.util.List;

/**
 * 检验检查DAO接口
 * @author rys
 * @version 2022-10-18
 */
@Mapper
public interface InspectionCheckDao extends CrudDao<InspectionCheck> {
    void allSave(InspectionCheck inspectionCheck);

    List<InspectionCheck> getByRecipelInfoId(String registrationId);

    void deleteByRecipelInfoId(String recipelInfoId);

    List<InspectionCheck> getByRecipelDetail(String recipelDetailId);

}