package com.geeke.toll.utils;

import java.math.BigDecimal;

/**
 * BigDecimal 空安全工具类
 */
public class BigdecimalConvert {

    /**
     * 用于判断金额是否为null，为null则返回 BigDecimal.ZERO
     */
    public static BigDecimal convert(BigDecimal bigDecimal) {
        return bigDecimal != null ? bigDecimal : BigDecimal.ZERO;
    }
}
