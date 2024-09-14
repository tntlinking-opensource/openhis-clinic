package com.geeke.stock.entity;

import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.Registration;
import javax.validation.constraints.NotNull;
import com.geeke.stock.entity.SupplierStock;
import com.geeke.outpatient.entity.RecipelInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 发药明细Entity
 * @author txl
 * @version 2022-08-11
 */
public class Dispensing extends DataEntity<Dispensing> {

	private static final long serialVersionUID = 1025152944882663511L;
	private Company company;      // 诊所id 
	private Registration registration;      // 挂号id 
	private SupplierStock supplierStock;      // 库存id 
	private RecipelInfo recipelInfo;      // 处方信息id 
	private Integer number;		// 数量

	
	public Dispensing() {
		super();
	}

	public Dispensing(String id){
		super(id);
	}
	

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@NotNull(message="挂号id不能为空")
    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }
    
	@NotNull(message="库存id不能为空")
    public SupplierStock getSupplierStock() {
        return supplierStock;
    }

    public void setSupplierStock(SupplierStock supplierStock) {
        this.supplierStock = supplierStock;
    }
    
    public RecipelInfo getRecipelInfo() {
        return recipelInfo;
    }

    public void setRecipelInfo(RecipelInfo recipelInfo) {
        this.recipelInfo = recipelInfo;
    }
    
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "dispensing";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1025152944882663511";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}