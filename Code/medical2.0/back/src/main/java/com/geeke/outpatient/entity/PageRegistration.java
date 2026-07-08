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

    /**
     * 验证列名是否安全（防止SQL注入）
     * 允许的格式: 字母、数字、下划线、点号（用于嵌套属性如 "a.update_date"）
     */
    public boolean isValidColumnName() {
        if (columnName == null || columnName.isEmpty()) {
            return false;
        }
        return columnName.matches("^[a-zA-Z_][a-zA-Z0-9_.]*$");
    }

}
