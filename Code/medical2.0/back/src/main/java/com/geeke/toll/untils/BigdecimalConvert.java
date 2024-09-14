package com.geeke.toll.untils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


public class BigdecimalConvert {
    //用于判断金额是否为null进行判断
    public static BigDecimal convert(BigDecimal bigDecimal){
        return bigDecimal!=null?bigDecimal:new BigDecimal("0");
    }
}
