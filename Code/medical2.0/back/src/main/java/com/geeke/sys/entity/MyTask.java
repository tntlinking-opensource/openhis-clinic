package com.geeke.sys.entity;

import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyTask extends DataEntity<MyTask> {
    private String id ;
    private Company company;
    private String creatBy;
    private Date creatDate;
    private String updateBy;
    private Date updateDate;
    private String delFage;
    private TaskManagement taskManagement;
    private String fileId;
    private String remark;
    private String executeschedule;
    private String auditType;
}
