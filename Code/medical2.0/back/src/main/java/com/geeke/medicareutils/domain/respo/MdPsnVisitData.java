package com.geeke.medicareutils.domain.respo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员就诊信息
 */
@Data
public class MdPsnVisitData {

        /** 就诊 ID */
        private String mdtrtId;

        /** 人员编号 */
        private String psnNo;

        /** 人员证件类型 */
        private String psnCertType;

        /** 证件号码 */
        private String certno;

        /** 人员姓名 */
        private String psnName;

        /** 性别 */
        private String gend;

        /** 民族 */
        private String naty;

        /** 出生日期 */
        private LocalDate brdy;

        /** 年龄 */
        private int age;

        /** 联系人姓名 */
        private String conerName;

        /** 联系电话 */
        private String tel;

        /** 险种类型 */
        private String insutype;

        /** 人员类别 */
        private String psnType;

        /** 医疗救助对象标志 */
        private String mafPsnFlag;

        /** 公务员标志 */
        private String cvlservFlag;

        /** 灵活就业标志 */
        private String flxempeFlag;

        /** 新生儿标志 */
        private String nwbFlag;

        /** 参保机构医保区划 */
        private String insuOptins;

        /** 单位名称 */
        private String empName;

        /** 开始时间 */
        private LocalDateTime begntime;

        /** 结束时间 */
        private LocalDateTime endtime;

        /** 就诊凭证类型 */
        private String mdtrtCertType;

        /** 医疗类别 */
        private String medType;

        /** 跨年度住院标志 */
        private String arsYearIptFlag;

        /** 先行支付标志 */
        private String prePayFlag;

        /** 住院/门诊号 */
        private String iptOtpNo;

        /** 病历号 */
        private String medrcdno;

        /** 主治医生编码 */
        private String atddrNo;

        /** 主诊医师姓名 */
        private String chfpdrName;

        /** 入院科室编码 */
        private String admDeptCodg;

        /** 入院科室名称 */
        private String admDeptName;

        /** 入院床位 */
        private String admBed;

        /** 住院主诊断代码 */
        private String dscgMaindiagCode;

        /** 住院主诊断名称 */
        private String dscgMaindiagName;

        /** 出院科室编码 */
        private String dscgDeptCodg;

        /** 出院科室名称 */
        private String dscgDeptName;

        /** 出院床位 */
        private String dscgBed;

        /** 离院方式 */
        private String dscgWay;

        /** 主要病情描述 */
        private String mainCondDscr;

        /** 病种编码 */
        private String diseCodg;

        /** 病种名称 */
        private String diseName;

        /** 手术操作代码 */
        private String oprnOprtCode;

        /** 手术操作名称 */
        private String oprnOprtName;

        /** 门诊诊断信息 */
        private String otpDiagInfo;

        /** 在院状态 */
        private String inhospStas;

        /** 死亡日期 */
        private java.util.Date dieDate;

        /** 住院天数 */
        private int iptDays;

        /** 计划生育服务证号 */
        private String fpscNo;

        /** 生育类别 */
        private String matnType;

        /** 计划生育手术类别 */
        private String birctrlType;

        /** 晚育标志 */
        private String latechbFlag;

        /** 孕周数 */
        private int gesoVal;

        /** 胎次 */
        private int fetts;

        /** 胎儿数 */
        private int fetusCnt;

        /** 早产标志 */
        private String pretFlag;

        /** 计划生育手术或生育日期 */
        private LocalDateTime birctrlMatnDate;

        /** 伴有并发症标志 */
        private String copFlag;

        /** 经办人 ID */
        private String opterId;

        /** 经办人姓名 */
        private String opterName;

        /** 经办时间 */
        private java.util.Date optTime;

        /** 备注 */
        private String memo;


}
