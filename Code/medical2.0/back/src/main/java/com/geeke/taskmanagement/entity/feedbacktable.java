package com.geeke.taskmanagement.entity;

import lombok.Data;

import java.util.Date;

@Data
public class feedbacktable {
    private String id;
    /** 租户号 */
    private String companyId ;
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
    private String taskmanagementid;
    private String executeschedule;//'执行进度;0：已完成，1：进行中'
    private String picture;
    private String remark;
    private String fileid;
    private String auditType;
}
