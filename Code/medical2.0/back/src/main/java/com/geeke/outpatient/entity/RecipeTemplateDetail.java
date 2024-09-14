package com.geeke.outpatient.entity;

import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.sys.entity.DictItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecipeTemplateDetail extends DataEntity<RecipeTemplateDetail> {
    private String id;
    private Company company;
    private RecipeTemplateInfo recipeTemplateInfo;      // 模板处方信息
    private DrugStuffEvt drugStuffId;		// 药品材料id
    private Integer singleDosage;		// 单次用量
    private Integer total;		// 总量
    private BigDecimal allFee;		// 总价
    private DictItem westernMedicineUse;      // 西药用法
    private DictItem frequency;      // 频次用量
    private DictItem days;      // 天数
    private DictItem chineseMedicineUse;      // 中药用法
    private DictItem infuseUse;      // 输液用法
    private String drippingSpeed;		// 滴速
    private DictItem skinTest;      // 皮试
    private Integer infuseGroup;		// 组数
    private String retailType;		// retail_type
    private Integer isExtra;		// 是否附加费
    private Integer seq;		// 显示顺序
    private Integer isUnpackSell;		// 是否拆零销售
    private BigDecimal unitPrice;		// 单价
    private String unitType;		// 单价单位
    private String stuffType;		// 物料类型
    private Integer minTotal;		// 最小单位总量
    private int executions;			//执行进度
    private int infuseType;        //输液执行状态
}
