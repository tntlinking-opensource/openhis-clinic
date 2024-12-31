package com.geeke.medicareutils.domain.respo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 费用明细输出
 */
@Data
public class MdFeeDetail {
    private String feedetlSn; // 费用明细流水号
    private BigDecimal detItemFeeSumamt; // 明细项目费用总额
    private BigDecimal cnt; // 数量
    private BigDecimal pric; // 单价
    private BigDecimal pricUplmtAmt; // 定价上限金额
    private BigDecimal selfpayProp; // 自付比例
    private BigDecimal fulamtOwnpayAmt; // 全自费金额
    private BigDecimal overlmtAmt; // 超限价金额
    private BigDecimal preselfpayAmt; // 先行自付金额
    private BigDecimal inscpScpAmt; // 符合政策范围金额
    private String chrgitmLv; // 收费项目等级
    private String medChrgitmType; // 医疗收费项目类别
    private String basMednFlag; // 基本药物标志
    private String hiNegoDrugFlag; // 医保谈判药品标志
    private String chldMedcFlag; // 儿童用药标志
    private String listSpItemFlag; // 目录特项标志
    private String lmtUsedFlag; // 限制使用标志
    private String drtReimFlag; // 直报标志
    private String memo; // 备注
    private String expContent; // 字段扩展
}
