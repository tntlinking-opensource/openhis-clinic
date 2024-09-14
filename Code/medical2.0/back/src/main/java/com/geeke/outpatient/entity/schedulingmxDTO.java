package com.geeke.outpatient.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class schedulingmxDTO {
    private String id;
    private String patientid;
    private String name;
    private String gendername;
    private String age;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date subscribedate;
    private String ysname;
    private String ksname;
    private String sourcename;
    private String statusname;
    private String remarks;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createdate;
    private Integer houser;
}
