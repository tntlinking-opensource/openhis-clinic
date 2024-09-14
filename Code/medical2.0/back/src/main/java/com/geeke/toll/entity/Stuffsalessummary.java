package com.geeke.toll.entity;

import lombok.Data;

@Data
public class Stuffsalessummary {
    /** 机构id */
    private String jgid;
    /** 机构名称 */
    private String jgmc;
    /** 材料别名 */
    private String clbm;
    /** 材料名称 */
    private String clmc;
    private String gg;
    /** 数量 */
    private Integer sl;
    /** 制剂 */
    private Integer zj;
    private String bzsl;
    private String bzdw;
    private String zxdw;
    private String dw;
    /** 材料类型 */
    private String cllx;
}
