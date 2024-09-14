package com.geeke.stock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.outpatient.entity.Registration;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 发药明细Entity
 * @author txl
 * @version 2022-08-11
 */
@Data
public class DispensingReportEvt {

	private static final long serialVersionUID = 1025152944882663511L;

	//处方单号
	private String recipelCode;
	//类型
    private String type;
	//是否拆零
    private String isUnpackSell;
    //名称
    private String name;
    //规格
    private String norms;
    //数量
    private String number;
    //制剂
    private String preparation;
    //单位
    private String unit;
    //包装单位
    private String pack;
    //进价单价
    private BigDecimal bid;
    //进价包装价格
    private BigDecimal packageBid;
    //进价总价
    private BigDecimal bidTotal;
    //销售单价
    private BigDecimal price;
    //销售包装价
    private BigDecimal packagePrice;
    //销售总价
    private BigDecimal priceTotal;
    //利润
    private BigDecimal profit;
    //生产厂家
    private String factory;
    //生产批号
    private String batchNo;
    //失效日期
    private Date endDate;
    //发药日期
    private Date dispensingDate;

    private Company company;
}