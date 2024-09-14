package com.geeke.cure.entity;

import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.MedicalRecord;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.entity.RecipelInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CureManagement extends DataEntity<CureManagement> {
    private Company company;
    private RecipelDetail recipelDetail;
    private int executions;
    private Date executeDate;
    private String delType="0";
    private String remarks;
    private int addNumber;
    private String recipelInfoId;
    private Integer infuseGroup;
}
