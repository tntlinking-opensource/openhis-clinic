package com.geeke.outpatient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName xt_zyzh
 */
@TableName(value ="xt_zyzh")
@Data
public class XtZyzh implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer zhid;

    /**
     * 
     */
    private String organizeid;

    /**
     * 
     */
    private String zhcode;

    /**
     * 
     */
    private String zhmc;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}