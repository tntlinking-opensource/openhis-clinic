package com.geeke.stock.entity;

import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.sys.entity.DictItem;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 材料信息Entity
 * @author txl
 * @version 2022-06-22
 */
@Data
public class MedicalProject
{
	private String id;
	private String name;
	private String pinyinCode;        // 拼音码
	private ManufactureFactory factory;      // 生产厂家
	private String specifications;        // 规格
	private Integer packNumber;        // 包装数量
	private DictItem minUnit;      // 最小单位
	private DictItem packUnit;      // 包装单位
	private BigDecimal priceOutSell;        // 对外销售价格
	private String isUnpackSell;        // 允许拆零销售
	private BigDecimal retailPrice;        // 拆开后零售价
	private String stuffType;		// 物料类型
	private int inventory;  //库存
	private DictItem dosisUnit; //单位
	private DictItem pack;//包装类
}