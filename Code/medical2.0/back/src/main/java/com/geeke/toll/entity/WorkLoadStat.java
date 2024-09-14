package com.geeke.toll.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkLoadStat {
    private String jgid;
    private String jgmc;
    private String companyId;
    private String count;   //接诊次数
    private BigDecimal grossAmount;  //应收金额
    private BigDecimal actual;  //实收金额
    private BigDecimal favorable;  //优惠金额
    private BigDecimal registrationCost;    //挂号费
    private BigDecimal westCost;
    private BigDecimal chineseCost;
    private BigDecimal chinesePatentCost;
    private BigDecimal stuffCost;
    private BigDecimal examinesCost;
    private BigDecimal checkoutCost;
    private BigDecimal therapyCost;
    private BigDecimal cureCost;
    private BigDecimal otherCost;
    private BigDecimal temporaryCost;
    private String types;
}
