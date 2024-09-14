package com.geeke.toll.entity;

import lombok.Data;

@Data
public class StuffsalessummaryRc {
    private int limit;
    private int offset;
    private String companyId;
    private String kssj;
    private String jssj;
    private String clmc;
    private String lx;
    /*
  机构状态（1：租户登录）
   */
    private String jgzt;
    /*
    机构id（租户id）
     */
    private String jgid;
}
