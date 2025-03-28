package com.geeke.outpatient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 医疗目录表
 * @TableName pres_drug
 */
@TableName(value ="pres_drug")
@Data
public class PresDrug implements Serializable {
    /**
     * 医疗目录编码
     */
    @TableId
    private String Id;


    private String medlistcodg;

    /**
     * 国家药品编号
     */
    private String natdrugno;

    /**
     * 通用名
     */
    private String genname;

    /**
     * 商品名
     */
    private String prodname;

    /**
     * 注册名
     */
    private String regname;

    /**
     * 目录类别 (101-西药, 102-中药, 103-自制剂, 104-民族药)
     */
    private String listtype;

    /**
     * 目录类别名称
     */
    private String listtypename;

    /**
     * 规格名称
     */
    private String specname;

    /**
     * 生产厂家
     */
    private String prdrname;

    /**
     * 批准文号
     */
    private String aprvno;

    /**
     * 剂型名称
     */
    private String dosformname;

    /**
     * 最小包装单位
     */
    private String minpacunt;

    /**
     * 最小包装数量
     */
    private String minpaccnt;

    /**
     * 最小制剂单位
     */
    private String minprepunt;

    /**
     * 统筹区编号
     */
    private String poolareano;

    /**
     * 统筹区名称
     */
    private String poolareaname;

    /**
     * 是否双通道标志 (0 - 否, 1 - 是)
     */
    private String dualchnlflag;

    /**
     * 开始时间 (格式：yyyy-MM-dd HH:mm:ss)
     */
    private Date begntime;

    /**
     * 结束时间 (格式：yyyy-MM-dd HH:mm:ss)
     */
    private Date endtime;


    /**
     * 结束时间 (格式：yyyy-MM-dd HH:mm:ss)
     */
    private Float drugprice;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}