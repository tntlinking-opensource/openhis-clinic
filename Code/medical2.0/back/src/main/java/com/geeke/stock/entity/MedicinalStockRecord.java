package com.geeke.stock.entity;

import com.geeke.org.entity.Company;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 库存操作记录Entity
 * @author hl
 * @version 2022-09-26
 */
public class MedicinalStockRecord extends DataEntity<MedicinalStockRecord> {

	private static final long serialVersionUID = 1139486400240443467L;
	private Company company;      // 诊所ID 
	private String drugStuffId;		// 药品/材料ID
	private String drugStuffName;		// 药品/材料名称
	private Integer type;		// 类型（1：药品；2：材料）
	private Integer operationType;		// 操作类型：-2入库明细到期自动作废 -1作废入库信息   1入库操作  2预占库存   3实际占用  4退药暂未退费 6-出库单出库
	private BigDecimal operationStock;		// 操作库存数
	private String storageId;		// 入库明细ID
	private String recipelInfoId;		// 处方ID
	private String refId;		// 关联记录ID

	
	public MedicinalStockRecord() {
		super();
	}

	public MedicinalStockRecord(String id){
		super(id);
	}
	

	@NotNull(message="诊所ID不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=1, max=20, message="药品/材料ID长度必须介于 1 和 20 之间")
	public String getDrugStuffId() {
		return drugStuffId;
	}
	public void setDrugStuffId(String drugStuffId) {
		this.drugStuffId = drugStuffId;
	}
	
	
	@Length(min=1, max=255, message="药品/材料名称长度必须介于 1 和 255 之间")
	public String getDrugStuffName() {
		return drugStuffName;
	}
	public void setDrugStuffName(String drugStuffName) {
		this.drugStuffName = drugStuffName;
	}
	
	
	@NotNull(message="类型（1：药品；2：材料）不能为空")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	
	public Integer getOperationType() {
		return operationType;
	}
	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}
	
	
	@NotNull(message="操作库存数不能为空")
	public BigDecimal getOperationStock() {
		return operationStock;
	}
	public void setOperationStock(BigDecimal operationStock) {
		this.operationStock = operationStock;
	}
	
	
	@Length(min=1, max=20, message="入库明细ID长度必须介于 1 和 20 之间")
	public String getStorageId() {
		return storageId;
	}
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}
	
	
	@Length(min=0, max=20, message="处方ID长度必须介于 0 和 20 之间")
	public String getRecipelInfoId() {
		return recipelInfoId;
	}
	public void setRecipelInfoId(String recipelInfoId) {
		this.recipelInfoId = recipelInfoId;
	}
	
	
	@Length(min=0, max=255, message="关联记录ID长度必须介于 0 和 255 之间")
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "medicinal_stock_record";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1139486400240443467";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}