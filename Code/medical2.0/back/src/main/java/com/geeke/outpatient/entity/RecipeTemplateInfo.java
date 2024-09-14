package com.geeke.outpatient.entity;

import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.sys.entity.DictItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RecipeTemplateInfo extends DataEntity<RecipeTemplateInfo> {
    private String id;
    private Company company;
    private Recipetemplate recipetemplate;
    private String dosage;		// 剂量
    private DictItem recipelUse;      // 用法
    private DictItem frequency;      // 频次用量
    private DictItem takeFrequency;      // 服用频次
    private String singleDosage;		// 单次用量
    private BigDecimal fee;		// 处方总价
    private String remaks;     //备注信息
    private DictItem smallType;   //处方小类
    private String entrust;     //医嘱
//    private String createBy;   //创建人
//    private Date createDate;   //创建时间
//    private String updateBy;   //修改人
//    private Date updateDate;   //修改时间
   // private String delFlag;   //删除标志
    private int seq;         //顺序
}
