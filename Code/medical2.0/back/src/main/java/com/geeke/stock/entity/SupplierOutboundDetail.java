package com.geeke.stock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 出库单Entity
 * @author txl
 * @version 2022-06-02/
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SupplierOutboundDetail extends DataEntity<SupplierOutboundDetail> {

	private static final long serialVersionUID = -2281869736391969854L;
	private Company company;      // 诊所id
	private Supplier supplier;      // 供应商id
	private SupplierOutbound supplierOutbound;      // 出库单id
	private Drug drug;      // 药品名称
	private Stuff stuff;      // 材料名称
	private  Integer currentStock;//当前库存
	private BigDecimal number;		// 出库数量
	private MedicinalStorageControl medicinalStorage;  //动态库存明细
	private  String remark;//备注
	private SupplierStock supplierStock;
	//医保字段
	private  String cnt;//医报数量
	private  String drugTracCodg;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public SupplierOutbound getSupplierOutbound() {
		return supplierOutbound;
	}

	public void setSupplierOutbound(SupplierOutbound supplierOutbound) {
		this.supplierOutbound = supplierOutbound;
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

	public Integer getCurrentStock() {
		return currentStock;
	}

	public void setCurrentStock(Integer currentStock) {
		this.currentStock = currentStock;
	}

	public BigDecimal getNumber() {
		return number;
	}

	public void setNumber(BigDecimal number) {
		this.number = number;
	}

	public MedicinalStorageControl getMedicinalStorage() {
		return medicinalStorage;
	}

	public void setMedicinalStorage(MedicinalStorageControl medicinalStorageControl) {
		this.medicinalStorage = medicinalStorageControl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
        return "supplier_outbound_detail";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1007238052174135324";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return false;
    }
}