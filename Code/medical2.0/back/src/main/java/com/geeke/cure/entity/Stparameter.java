package com.geeke.cure.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Stparameter {
    private int limit;
    private int offset;
    private String companyId;
    private String kssj;
    private  String jssj;
    private String zxzt;
    private String hzxm;
    private String kdys;
    private String detailid;
    private String zxr;
    private Date zxsj;
    private String stresult;
    private String sttime;
}
