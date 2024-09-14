package com.geeke.taskmanagement.entity;

import com.geeke.org.entity.Company;
import lombok.Data;

import java.util.Date;

@Data
public class taskexecutor {
    private String ID;
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
    private String clinic_id;
    private String userid;

    private Company company;
}
