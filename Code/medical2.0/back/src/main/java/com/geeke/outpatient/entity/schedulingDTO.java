package com.geeke.outpatient.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class schedulingDTO {

    private String userid;
    private String username;
    private String ksid;
    private String ksname;
    private Date schedulingtime;
    private String pbid;
    private Integer pbsl;
    private  List<weekdayDTO> dates;
    private String stratime;
    private String endtime;
    private String companyid;
    private List<String> datelist;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date datestra;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date dateend;
    private String status;
    private String patientname;
    private List<Integer> datehouse;
}
