package com.geeke.toll.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DrugSales {
    private String jgid;
    private String jgmc;
    private String type;
    private String name;
    private String dosis;
    private String dosisUnit;
    private String preparation;
    private String preparationUnit;
    private String pack;
    private int total;
    private BigDecimal allFee;
}
