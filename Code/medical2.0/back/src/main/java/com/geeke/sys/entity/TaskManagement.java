package com.geeke.sys.entity;

import com.geeke.admin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskManagement {
    private String id;
    private String createBy;
    private Date createDate;
    private String updateBy;
    private Date updateDate;
    private String delFlag;
    private String tasktype;
    private String taskname;
    private String taskaccessory;
    private String taskdescribe;
    private Date taskdeadline;
    private String taskremark;
    private String auditresult;
    private User user;
}
