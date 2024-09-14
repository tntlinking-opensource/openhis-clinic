package com.geeke.stock.entity;

import com.geeke.org.entity.Company;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;

import java.math.BigDecimal;

/**
 * 库存盘点Entity
 * @author rys
 * @version 2022-11-02
 */
public class InventoryVerification extends DataEntity<InventoryVerification> {

	private static final long serialVersionUID = 1235640942656921602L;
	private Company company;      // 诊所id 
	private String code;		// 盘点单号
	private String status;		// 盘点状态
	private String type;		//盘点类型（0：药品，1：材料）
	private BigDecimal allPrice;   //盈亏总金额

	
	public InventoryVerification() {
		super();
	}

	public InventoryVerification(String id){
		super(id);
	}
	

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=1, max=255, message="盘点单号长度必须介于 1 和 255 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	@Length(min=0, max=1, message="盘点状态长度必须介于 0 和 1 之间")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getAllPrice() {
		return allPrice;
	}

	public void setAllPrice(BigDecimal allPrice) {
		this.allPrice = allPrice;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "inventory_verification";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1235640942656921602";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return false;
    }
}