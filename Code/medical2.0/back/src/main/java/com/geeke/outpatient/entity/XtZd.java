package com.geeke.outpatient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName xt_zd
 */
@TableName(value ="xt_zd")
@Data
public class XtZd implements Serializable {
    /**
     * 
     */
    private Integer zdid;

    /**
     * 
     */
    private String organizeid;

    /**
     * 
     */
    private String zdcode;

    /**
     * 
     */
    private String zdmc;

    /**
     * 
     */
    private String icd10;

    /**
     * 
     */
    private String icd10fjm;

    /**
     * 
     */
    private String py;

    /**
     * 
     */
    private String wb;

    /**
     * 
     */
    private Integer creatorcode;

    /**
     * 
     */
    private String createtime;

    /**
     * 
     */
    private String lastmodifytime;

    /**
     * 
     */
    private String lastmodifiercode;

    /**
     * 
     */
    private String px;

    /**
     * 
     */
    private Integer zt;

    /**
     * 
     */
    private String zdlx;

    /**
     * 
     */
    private String ybnhlx;

    /**
     * 
     */
    private String gjybdm;

    /**
     * 
     */
    private String zdcodeYb;

    /**
     * 
     */
    private String zdmcYb;

    /**
     * 
     */
    private String mxb;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}