package com.geeke.stock.entity;

import com.geeke.common.data.Page;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 发药明细Entity
 * @author txl
 * @version 2022-08-11
 */
@Data
public class DispensingReportTotalEvt {

	private static final long serialVersionUID = 1025152944882663512L;

	//数量合计
	private String numberAmount;
    //进价总价合计
    private String bidTotalAmount;
	//销售总价合计
    private String priceTotalAmount;
    //利润合计
    private String profitAmount;
}