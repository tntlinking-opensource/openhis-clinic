package com.geeke.outpatient.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 处方详情Entity
 * @author txl
 * @version 2022-06-07
 */
@Data
public class RecipelDetailEvt implements Serializable {

	private static final long serialVersionUID = 1014474470772900078L;
	//名称
	private String name;
	//规格
    private String norms;
	//数量
	private String num;
	//单价
    private String price;
    //天数
    private String days;
    //用法标识
    private String useText = "用法:";
	//频次
	private String frequency;
	//用法
	private String use;
	//备注
	private String remarks;
	//名称标题
	private String nameTitle = "名称";
	//数量标题
	private String numTitle = "数量";
	//单位标题
	private String unitTitle = "单位";
	//单价标题
	private String priceTitle = "单价";
	//备注标题
	private String remarksTitle = "备注";
	//材料费
	private String stuffPriceName = "材料费";
	//材料费金额
	private String stuffPrice;
}