package com.geeke.admin.entity;

import com.geeke.common.persistence.DataEntity;
import lombok.Data;

/**
 * 用户扩展Entity
 * @author ch
 * @version 2021-08-25
 */
@Data
public class UserExt extends DataEntity<UserExt>{

    private static final long serialVersionUID = 1L;
    private String userId;		// 用户id
    private String companyId;		// 诊所id
    private String photoId;		// 上传照片id
    private String nation;		// 民族
    private String sex;			// 性别
    private String birthday;		// 生日
    private String isDuty;		// 是否在职（0否1是）
    private String creditType;		// 证件类型
    private String creditNum;		// 证件号码
    private String post;		// 职位
    private String office;		// 科室名称
    private String jobType;		// 职业类型
    private String description;		// 描述信息
    private String startWorkTime;		// 诊所开始工作时间
    private String endWorkTime;		// 诊所结束时间

}
