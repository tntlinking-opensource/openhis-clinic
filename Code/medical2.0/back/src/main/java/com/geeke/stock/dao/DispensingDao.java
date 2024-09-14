package com.geeke.stock.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.stock.entity.DispensingReportEvt;
import com.geeke.stock.entity.DispensingReportTotalEvt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.Dispensing;

import java.util.List;

/**
 * 发药明细DAO接口
 * @author txl
 * @version 2022-08-11
 */
@Mapper
public interface DispensingDao extends CrudDao<Dispensing> {

    int reportListCount(PageRequest var1);

    List<DispensingReportEvt> reportList(PageRequest pageRequest);

    DispensingReportTotalEvt reportAmount(PageRequest pageRequest);

    void updateDelFlag(@Param("recipelInfoId") String recipelInfoId);

    int getOrganizationListCount(PageRequest pageRequest);

    List<DispensingReportEvt> getOrganizationList(PageRequest pageRequest);

}