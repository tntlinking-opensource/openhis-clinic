package com.geeke.toll.entity;

import lombok.Data;

@Data
public class Ypjxc {
    /*
    机构id
     */
    private String jgid;
    /*
    机构名称
     */
    private String jgmc;
    /**
     * 药品编码
     */
    private String ypbm;
    /*
    药品名称
     */
    private String ypmc;
    /*
    条形码
     */
    private  String txm;
    /*
    国药准字
     */
    private  String gyzz;
    /*
    规格
     */
    private  String gg;
    /*
    生产厂家
     */
    private  String sccj;
    /*
    供应商
     */
    private  String gys;
    /*
    零售价
     */
    private  String lsj;
    /*
    成本价
     */
    private  String cbj;
    /*
    批号
     */
    private  String ph;
    /*
    有效期
     */
    private  String yxq;
    /*
    期初数量
     */
    private  Integer qcsl;
    /*
    期初成本
     */
    private  Integer qccb;
    /*
    本期入库数量
     */
    private  Integer bqrk;
    /*
    本期入库成本
     */
    private  Integer bqrkcb;
    /*
    本期入库销售额
     */
    private  Integer bqrkxse;
    /*
    本期出库数量
     */
    private  Integer bqcksl;
    /*
    本期出库成本
     */
    private  Integer bqckcb;
    /*
    本期出库销售额
     */
    private  Integer bqckxse;
    /*
    本期盘点数量
     */
    private  Integer bqpdsl;
    /*
    本期盘点成本
     */
    private  Integer bqpdcb;
    /*
    本期盘点销售额
     */
    private  Integer bqpdxse;
    /*
    期末数量
     */
    private  Integer qmsl;
    /*
    期末成本
     */
    private  Integer qmcb;
    /*
    期末销售额
     */
    private  Integer qmxse;

    /**
     * 分类
     */
    private  String fl;
    /**
     * 制剂
     */
    private Integer zj;
    private String zxdw;
    /**
     * 猜测时 处方详情使用天数 字典项的name(这个字典的name是数字)
     */
    private String dw;

}
