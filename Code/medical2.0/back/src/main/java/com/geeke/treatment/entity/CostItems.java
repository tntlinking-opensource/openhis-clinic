package com.geeke.treatment.entity;

import lombok.Data;

@Data
public class CostItems extends CostItem{
    private String sign; //高低指数标记
    private String conclusion;   //结论
    private String describe;    //描述
}
