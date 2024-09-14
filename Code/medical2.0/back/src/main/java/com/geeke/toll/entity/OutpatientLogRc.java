package com.geeke.toll.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OutpatientLogRc {
    private int limit;
    private int offset;
    private String companyId;
    private String kssj;
    private String jssj;
    /** ID */
    private String id;

    /** 患者姓名 */
    private String patientName;

    /**家长姓名 */
    private String patriarchName;

    /** 性别 */
    private String sex;

    /** 患者年龄 */
    private String age;

    /** 民族 */
    private String nation;

    /** 职业 */
    private String occupation;

    /** 详细地址 */
    private String address;

    /** 就诊日期 */
    private Date visitDate;

    /** 初/复诊 */
    private String initialVisit;

    /** 血压 */
    private String bloodPressure;

    /** 临床症状 */
    private String symptom;

    /** 体温发热 */
    private String fever;

    /** 流行病学史 */
    private String epidemicDisease;

    /** 诊断 */
    private String diagnosis;

    /** 传染病 */
    private String infect;

    /** 处理情况 */
    private String handle;

    /** 有效证件号 */
    private String certificate;

    /** 工作单位 */
    private String unit;

    /** 医生签名 */
    private String signature;

    /** 贫困标志 */
    private String poverty;

    /** 联系电话 */
    private String telephone;

    /** 发病日期 */
    private Date morbidityDate;

    /** 实验室阳性结果 */
    private String positiveResult;

    /** 个体化健康教育 */
    private String healthEducation;

    /**机构状态（1：租户登录）*/
    private String jgzt;

    /** 机构id（租户id）*/
    private String jgid;
}
