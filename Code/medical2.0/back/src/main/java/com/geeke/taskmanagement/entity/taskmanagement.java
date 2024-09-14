package com.geeke.taskmanagement.entity;

import com.geeke.org.entity.Company;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class taskmanagement {
    private  String ID;
    /** 租户号 */
    private String companyId ;

    private Company company;
    /** 删除标记;（0：正常；1：删除） */
    private String delFlag ;
    /** 创建人 */
    private String createdBy ;
    /** 创建时间 */
    private Date createdTime ;
    /** 更新人 */
    private String updatedBy ;
    /** 更新时间 */
    private Date updatedTime ;
    private  String tasktype;//'任务类型;tasktype_0：常规任务，tasktype_1：宣传活动' ,
    private  String taskname;
    private  String taskdescribe;
    private  String taskaccessory;
    private  String taskinitiator;
    private  Date taskdeadline;
    private String taskstatus;//'任务状态;0：不通过；1：通过
    private String auditresult;//'审核结果;auditresult_0：通过；auditresult_1：未通过' ,
    private String taskremark;
    private String taskinitiatorname;//任务发起人


/*
执行人
 */
    private List<String> taskregion;

    private List<taskexecutor> taskexecutors;
}
