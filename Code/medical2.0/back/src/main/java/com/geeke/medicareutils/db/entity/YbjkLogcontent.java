package com.geeke.medicareutils.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ybjk_logcontent
 */
@TableName(value ="ybjk_logcontent")
@Data
public class YbjkLogcontent implements Serializable {
    /**
     * 
     */
    @TableId
    private Long innumbier;

    /**
     * 
     */
    private String tradinumber;

    /**
     * 
     */
    private String hisid;

    /**
     * 
     */
    private LocalDateTime begindate;

    /**
     * 
     */
    private LocalDateTime  enddate;

    /**
     * 
     */
    private String inhead;

    /**
     * 
     */
    private String incontent;

    /**
     * 
     */
    private String outhead;

    /**
     * 
     */
    private String outcontent;

    /**
     * 
     */
    private String errormsg;

    /**
     * 
     */
    private String stateid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}