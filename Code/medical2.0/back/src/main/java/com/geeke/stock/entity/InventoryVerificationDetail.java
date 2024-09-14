package com.geeke.stock.entity;

import com.geeke.org.entity.Company;
import javax.validation.constraints.NotNull;
import com.geeke.stock.entity.InventoryVerification;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.Stuff;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;

import java.math.BigDecimal;

/**
 * 库存盘点详情Entity
 * @author 超级管理员
 * @version 2022-11-02
 */
public class InventoryVerificationDetail extends DataEntity<InventoryVerificationDetail> {

	private static final long serialVersionUID = 1235640942656921614L;
	private Company company;      // 诊所id 
	private InventoryVerification inventoryVerification;      // 库存盘点id 
	private Drug drug;      // 盘点药品id 
	private Stuff stuff;      // 盘点材料id 
	private Integer currentInventory;		// 当前库存
	private Integer checkInventory;		// 盘点库存
	private Integer profitAndLoss;		// 盈亏
    private BigDecimal profitAndLossPrice;  //盈亏金额
    private MedicinalStorageControl medicinalStorageControl;  //动态库存明细
    private SupplierStock supplierStock;

	
	public InventoryVerificationDetail() {
		super();
	}

	public InventoryVerificationDetail(String id){
		super(id);
	}
	

	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@NotNull(message="库存盘点id不能为空")
    public InventoryVerification getInventoryVerification() {
        return inventoryVerification;
    }

    public void setInventoryVerification(InventoryVerification inventoryVerification) {
        this.inventoryVerification = inventoryVerification;
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
    
	public Integer getCurrentInventory() {
		return currentInventory;
	}
	public void setCurrentInventory(Integer currentInventory) {
		this.currentInventory = currentInventory;
	}
	
	
	public Integer getCheckInventory() {
		return checkInventory;
	}
	public void setCheckInventory(Integer checkInventory) {
		this.checkInventory = checkInventory;
	}
	
	
	public Integer getProfitAndLoss() {
		return profitAndLoss;
	}
	public void setProfitAndLoss(Integer profitAndLoss) {
		this.profitAndLoss = profitAndLoss;
	}


    public BigDecimal getProfitAndLossPrice() {
        return profitAndLossPrice;
    }

    public void setProfitAndLossPrice(BigDecimal profitAndLossPrice) {
        this.profitAndLossPrice = profitAndLossPrice;
    }

    public MedicinalStorageControl getMedicinalStorageControl() {
        return medicinalStorageControl;
    }

    public void setMedicinalStorageControl(MedicinalStorageControl medicinalStorageControl) {
        this.medicinalStorageControl = medicinalStorageControl;
    }

    public SupplierStock getSupplierStock() {
        return supplierStock;
    }

    public void setSupplierStock(SupplierStock supplierStock) {
        this.supplierStock = supplierStock;
    }

    /**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "inventory_verification_detail";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1235640942656921614";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return false;
    }
}