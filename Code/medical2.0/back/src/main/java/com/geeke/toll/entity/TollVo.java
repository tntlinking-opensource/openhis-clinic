package com.geeke.toll.entity;

import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.RecipelInfoEvt;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * LC
 * 汇总报表出参
 */
@Data
public class TollVo implements Serializable {

    private static final long serialVersionUID = 1024846610635145503L;

    private Page page;
    private BigDecimal amountReceivableTotal;		// 应收(退)金额合计
    private BigDecimal amountReceivedTotal;		// 实收金额合计
    private BigDecimal cashTotal;		// 现金支付合计
    private BigDecimal weChatTotal;		// 微信支付合计
    private BigDecimal alipayTotal;		// 支付宝支付合计
    private BigDecimal bankCardPayTotal;		// 银行卡支付合计
    private BigDecimal ybTotal;		// 医保支付合计
}
