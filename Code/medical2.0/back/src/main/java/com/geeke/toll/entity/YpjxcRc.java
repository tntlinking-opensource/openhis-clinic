package com.geeke.toll.entity;

import lombok.Data;

@Data
public class YpjxcRc {
    private int limit;
    private int offset;
    private String companyId;
    private String kssj;
    private  String jssj;
    private String lx;
    private String zt;
    private String spm;
    private String ph;
    private String orderby;
    private String ypcltype;
    /*
    机构状态（1：租户登录）
     */
    private String jgzt;
    /*
    机构id（租户id）
     */
    private String jgid;
}
