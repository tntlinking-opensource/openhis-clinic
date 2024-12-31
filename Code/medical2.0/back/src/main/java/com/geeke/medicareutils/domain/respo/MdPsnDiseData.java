package com.geeke.medicareutils.domain.respo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 诊断信息
 */
@Data
public class MdPsnDiseData {
    /** 诊断信息 ID */
    private String diagInfoId;  // 字符型，30字符

    /** 就诊 ID */
    private String mdtrtId;  // 字符型，30字符

    /** 人员编号 */
    private String psnNo;  // 字符型，30字符

    /** 出入院诊断类别 */
    private String inoutDiagType;  // 字符型，3字符

    /** 诊断类别 */
    private String diagType;  // 字符型，3字符

    /** 主诊断标志 */
    private String maindiagFlag;  // 字符型，3字符

    /** 诊断排序号 */
    private Integer diagSrtNo;  // 数值型，2字符

    /** 诊断代码 */
    private String diagCode;  // 字符型，20字符

    /** 诊断名称 */
    private String diagName;  // 字符型，100字符

    /** 入院病情 */
    private String admCond;  // 字符型，500字符

    /** 诊断科室 */
    private String diagDept;  // 字符型，50字符

    /** 诊断医生编码 */
    private String diseDorNo;  // 字符型，30字符

    /** 诊断医生姓名 */
    private String diseDorName;  // 字符型，50字符

    /** 诊断时间 */
    private LocalDateTime diagTime;  // 日期时间型，格式：yyyy-MM-dd HH:mm:ss

    /** 经办人 ID */
    private String opterId;  // 字符型，20字符

    /** 经办人姓名 */
    private String opterName;  // 字符型，50字符

    /** 经办时间 */
    private LocalDateTime optTime;  // 日期时间型，格式：yyyy-MM-dd HH:mm:ss
}
