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
 * @TableName dzcf_d003_output
 */
@TableName(value ="dzcf_d003_output")
@Data
public class DzcfD003Output implements Serializable {
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
    private String rxstascodg;

    /**
     * 
     */
    private String rxstasname;

    /**
     * 
     */
    private String cxyy;

    /**
     * 
     */
    private String cxsj;

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

    /**
     * 
     */
    private String rxchkstascodg;

    /**
     * 
     */
    private String rxchkopnn;

    /**
     * 
     */
    private String rxchktime;

    /**
     * 
     */
    private String rxchkstasname;

    /**
     * 
     */
    private String rxusedstascodg;

    /**
     * 
     */
    private String rxusedstasname;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}