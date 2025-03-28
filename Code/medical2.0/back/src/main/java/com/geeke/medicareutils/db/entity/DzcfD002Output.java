package com.geeke.medicareutils.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName dzcf_d002_output
 */
@TableName(value ="dzcf_d002_output")
@Data
public class DzcfD002Output implements Serializable {
    /**
     * 
     */
    private String mzh;

    /**
     * 
     */
    private String cfh;

    /**
     * 
     */
    private String organizeid;

    /**
     * 
     */
    private String inputcontent;

    /**
     * 
     */
    private String originalvalue;

    /**
     * 
     */
    private String originalrxfile;

    /**
     * 
     */
    private String rxfile;

    /**
     * 
     */
    private String signdigest;

    /**
     * 
     */
    private String signcertsn;

    /**
     * 
     */
    private String signcertdn;

    /**
     * 
     */
    private String czydm;

    /**
     * 
     */
    private Date czrq;

    /**
     * 
     */
    private Integer zt;

    /**
     * 
     */
    private String ztCzy;

    /**
     * 
     */
    private Date ztRq;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}