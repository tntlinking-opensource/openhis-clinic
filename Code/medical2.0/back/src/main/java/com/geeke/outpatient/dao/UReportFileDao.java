package com.geeke.outpatient.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.entity.UReportFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 患者信息DAO接口
 * @author txl
 * @version 2022-06-23
 */
@Mapper
public interface UReportFileDao extends CrudDao<UReportFile> {

    UReportFile getReportFileByName(String name);

    List<UReportFile> getListAll();
}