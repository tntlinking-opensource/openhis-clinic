package com.geeke.outpatient.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 处方详情Entity
 * @author txl
 * @version 2022-06-07
 */
@Data
public class InfuseEvt implements Serializable {

	private static final long serialVersionUID = 1014474470772900079L;
	//名称
	private String name;
	//单次用量
    private String singleDosage;
	//天数
	private String days;
	//总计数量
	private String totalNum;
	//皮试
	private String skinTest;
	//单价
	private String price;
	//材料费
	private String stuffPriceName;
	//材料费金额
	private String stuffPrice;
}