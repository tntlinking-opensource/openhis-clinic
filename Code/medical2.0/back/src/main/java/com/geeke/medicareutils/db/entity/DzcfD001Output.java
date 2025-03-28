package com.geeke.medicareutils.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName dzcf_d001_output
 */
@TableName(value ="dzcf_d001_output")
@Data
public class DzcfD001Output implements Serializable {
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
    private String rxtracecode;

    /**
     * 
     */
    private String hirxno;

    /**
     * 
     */
    private String czydm;

    /**
     * 
     */
    private LocalDateTime czrq;

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