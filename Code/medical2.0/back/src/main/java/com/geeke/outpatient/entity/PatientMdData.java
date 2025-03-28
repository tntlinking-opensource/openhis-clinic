package com.geeke.outpatient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 患者医保信息表
 * @TableName patient_md_data
 */
@TableName(value ="patient_md_data")
@Data
public class PatientMdData implements Serializable {
    /**
     * 
     */
    @TableId
    private Long id;


    /**
     * 组织id
     */
    private Long OrganizeId;

    /**
     * 患者id
     */
    private Long patientId;

    /**
     * 人员编号
     */
    private String psnNo;

    /**
     * 证件类型
     */
    private String psnCertType;

    /**
     * 证件号码
     */
    private String certno;

    /**
     * 人员姓名
     */
    private String psnName;

    /**
     * 性别
     */
    private String gend;

    /**
     * 民族
     */
    private String naty;

    /**
     * 出生日期
     */
    private String brdy;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 字段拓展
     */
    private String expcontent;

    /**
     * 余额
     */
    private BigDecimal balc;

    /**
     * 险种类型
     */
    private String insutype;

    /**
     * 人员类别
     */
    private String psnType;

    /**
     * 人员参保状态
     */
    private String psnInsuStas;

    /**
     * 个人参保日期
     */
    private String psnInsuDate;

    /**
     * 暂停参保日期
     */
    private String pausInsuDate;

    /**
     * 公务员标志
     */
    private String cvlservFlag;

    /**
     * 参保地医保区划
     */
    private String insuplcAdmdvs;

    /**
     * 单位名称
     */
    private String empName;

    /**
     * 待遇检查类型
     */
    private String trtChkType;

    /**
     * 基金支付类型
     */
    private String fundPayType;

    /**
     * 基金款项待遇享受标志
     */
    private String trtEnjymntFlag;

    /**
     * 人员待遇开始日期
     */
    private Date trtBegndate;

    /**
     * 人原待遇结束日期
     */
    private Date trtEnddate;

    /**
     * 待遇检查结果
     */
    private String trtChkRslt;

    /**
     * 字段拓展
     */
    private String trtExpcontent;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}