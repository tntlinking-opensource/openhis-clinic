package com.geeke.cure.entity;

import com.geeke.org.entity.Company;
import com.geeke.treatment.entity.CostItem;
import lombok.Data;

@Data
public class InspectionCheckDetailCostItem {
    private String id;
    private Company company;      // 诊所id
    private CostItem costItem;      // 对应项目id
    private String conclusion;		// 结果
    private int sign;		// 指数高低标记
    private String describes;		// 描述
    private int seq;          //排序
}
