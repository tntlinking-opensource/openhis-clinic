package com.geeke.taskmanagement.entity;

import lombok.Data;

@Data
public class gettaskrcvo {
    private int limit;
    private int offset;
    public String kssj;
    public String jssj;
    public String jgid;
    public String tasktype;
    public String taskstatus;
    //public String auditresult;
    public String taskname;
    public String companyId;

    public String taskmanagementid;
}
