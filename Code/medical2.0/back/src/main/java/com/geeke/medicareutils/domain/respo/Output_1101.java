package com.geeke.medicareutils.domain.respo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Output_1101 {

    private BaseInfo baseinfo; // 基本信息
    private List<InsuInfo> insuinfo; // 参保信息列表
    private List<IdetInfo> idetinfo; // 身份信息列表

    @Data
    public static class BaseInfo {
        private String psn_no; // 人员编号
        private String psn_cert_type; // 人员证件类型
        private String certno; // 证件号码
        private String psn_name; // 人员姓名
        private String gend; // 性别
        private String naty; // 民族
        private String brdy; // 出生日期
        private int age; // 年龄
        private String expContent; // 字段扩展
    }

    @Data
    public static class InsuInfo {
        private BigDecimal balc; // 余额
        private String insutype; // 险种类型
        private String psn_type; // 人员类别
        private String psn_insu_stas; // 人员参保状态
        private String psn_insu_date; // 个人参保日期
        private String paus_insu_date; // 暂停参保日期
        private String cvlserv_flag; // 公务员标志
        private String insuplc_admdvs; // 参保地医保区划
        private String emp_name; // 单位名称
    }

    @Data
    public static class IdetInfo {
        private String psn_idet_type; // 人员身份类别
        private String psn_type_lv; // 人员类别等级
        private String memo; // 备注
        private String begntime; // 开始时间
        private String endtime; // 结束时间
    }

}
