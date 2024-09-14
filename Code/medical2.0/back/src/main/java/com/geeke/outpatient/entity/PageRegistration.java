package com.geeke.outpatient.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRegistration {
    private String columnName;
    private int limit;
    private int offset;
    private String order;
    private String companyId;
    private String recipeStatus;
    private Date updateDate;
    private String status;
    private int chargeStatus=3;
    private int dispensionStatus=3;
    private String recipelType;
    private String patientName;
    private String patientCode;
    private int cureType=3;
    private int infuseType=3;
    private String overlook="3";
    private String doctorid;
    private String openId;

}
