package com.geeke.outpatient.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.common.persistence.DataEntity;
import com.geeke.cure.entity.InspectionCheckInfo;
import com.geeke.org.entity.Company;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.Stuff;
import com.geeke.sys.entity.DictItem;
import com.geeke.treatment.entity.CostItem;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 处方详情Entity
 * @author txl
 * @version 2022-06-07
 */
public class DrugStuffEvt implements Serializable {

	private static final long serialVersionUID = 1014474470772900073L;
	private String drugStuffId;          // 药品/材料/诊疗项目id
    private String name;                 // 名称
    private BigDecimal price;            // 包装单价
    private BigDecimal retailPrice;		 // 拆开后零售价
    private DictItem dosisUnit;          // 剂量单位(作废)
    private DictItem preparationUnit;    // 制剂单位
    private DictItem pack;               // 包装单位
    private String isUnpackSell;		 // 允许拆零销售
    private Drug drug;
    private Stuff stuff;
    private CostItem costItem;
    private InspectionCheckInfo inspectionCheckInfo;

    public String getDrugStuffId() {
        return drugStuffId;
    }

    public void setDrugStuffId(String drugStuffId) {
        this.drugStuffId = drugStuffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public DictItem getDosisUnit() {
        return dosisUnit;
    }

    public void setDosisUnit(DictItem dosisUnit) {
        this.dosisUnit = dosisUnit;
    }

    public DictItem getPack() {
        return pack;
    }

    public void setPack(DictItem pack) {
        this.pack = pack;
    }

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public Stuff getStuff() {
        return stuff;
    }

    public void setStuff(Stuff stuff) {
        this.stuff = stuff;
    }

    public CostItem getCostItem() {
        return costItem;
    }

    public void setCostItem(CostItem costItem) {
        this.costItem = costItem;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public DictItem getPreparationUnit() {
        return preparationUnit;
    }

    public void setPreparationUnit(DictItem preparationUnit) {
        this.preparationUnit = preparationUnit;
    }

    public String getIsUnpackSell() {
        return isUnpackSell;
    }

    public void setIsUnpackSell(String isUnpackSell) {
        this.isUnpackSell = isUnpackSell;
    }

    public InspectionCheckInfo getInspectionCheckInfo() {
        return inspectionCheckInfo;
    }

    public void setInspectionCheckInfo(InspectionCheckInfo inspectionCheckInfo) {
        this.inspectionCheckInfo = inspectionCheckInfo;
    }
}