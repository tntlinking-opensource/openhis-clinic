package com.geeke.toll.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.toll.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;

/**
 * 收费管理DAO接口
 * @author lc
 * @version 2022-06-16
 */
@Mapper
public interface TollInfoDao extends CrudDao<TollInfo> {

    List<TollInfo> tollTotalForm(PageRequest var1);

    List<TollInfo> tollDetailForm(PageRequest var1);

    int formTollCount(PageRequest var1);

    int formDetailCount(PageRequest var1);

    TollVo tollTota(PageRequest var1);

    TollVo tollTotalAmountReceivableAndAbleTotal(PageRequest var1);

//    TollVo tollTotalAmountReturnAndAbleTotal(PageRequest var1);

    TollVo tollDetailAmountReceivedAble(PageRequest var1);

    BigDecimal tollTotalCashTotal(PageRequest var1);

    BigDecimal tollTotalAlipayTotal(PageRequest var1);

    BigDecimal tollTotalWeChatTotal(PageRequest var1);

    BigDecimal tollTotalBankCardPayTotal(PageRequest var1);

    BigDecimal tollTotalYbTotalTotal(PageRequest pageRequest);

    TollInfo getTollInfoByRegistrationId(String registrationId);

    List<TollInfo> getCreateBy(PageRequest var1);

    List<WorkLoad> countWorkload(PageRequest var1);

    List<WorkLoad> getWorkload(PageRequest var1);

    List<WorkLoad>  getCount(PageRequest var1);

    WorkLoadStat  getCounts(PageRequest var1);

    List<WorkLoad> getTemporaryCost(PageRequest var1);

    WorkLoadStat getWorkLoadStat(PageRequest var1);

    List<WorkLoadStat> getWorkLoadStats(PageRequest var1);

    List<WorkLoadStat> getTemporaryCostStat(PageRequest var1);

    List<DrugSales> getDrugSales(PageRequest pageRequest);

    List<DrugSales> countDrugSales(PageRequest pageRequest);

    DrugSales getDrugSalesStat(PageRequest pageRequest);


    List<Ypjxc> getypjxcmanagementcounts(YpjxcRc ypjxcRc);
    List<Ypjxc> getypjxcmanagement(YpjxcRc ypjxcRc);
    List<Ypjxc> getypjxcmanagementsums(YpjxcRc ypjxcRc);

    List<Stuffsalessummary> getStuffsalessummarytotals(StuffsalessummaryRc stuffsalessummaryRc);
    List<Stuffsalessummary> getStuffsalessummarylist(StuffsalessummaryRc stuffsalessummaryRc);
    List<Stuffsalessummary> getStuffsalessummarysums(StuffsalessummaryRc stuffsalessummaryRc);

    List<Ypjxc> getcljxcmanagementcounts(YpjxcRc ypjxcRc);
    List<Ypjxc> getcljxcmanagement(YpjxcRc ypjxcRc);
    List<Ypjxc> getcljxcmanagementsums(YpjxcRc ypjxcRc);

    List<Ypclrkcx> getypclrkcxtotal(YpjxcRc ypjxcRc);
    List<Ypclrkcx> getypclrkcxlist(YpjxcRc ypjxcRc);
    List<Ypclrkcx> getypclrkcxsums(YpjxcRc ypjxcRc);

    List<Ypjxc> getjglist(YpjxcRc ypjxcRc);

}