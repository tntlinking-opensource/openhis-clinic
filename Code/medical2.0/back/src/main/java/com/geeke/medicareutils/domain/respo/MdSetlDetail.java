package com.geeke.medicareutils.domain.respo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MdSetlDetail {
    private String fundPayType; // 基金支付类型
    private BigDecimal inscpScpAmt; // 符合政策范围金额
    private BigDecimal crtPayblAmt; // 本次可支付限额金额
    private BigDecimal fundPayAmt; // 基金支付金额
    private String fundPayTypeName; // 基金支付类型名称
    private String setlProcInfo; // 结算过程信息
}
