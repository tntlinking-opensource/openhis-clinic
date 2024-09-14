package com.geeke.outpatient.entity;

import com.geeke.admin.entity.User;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;

import java.math.BigDecimal;
import java.util.Date;

public class UReportFile extends DataEntity<UReportFile> {

    private static final long serialVersionUID = 1014786989773332527L;
    private String content;      // 报表内容

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
