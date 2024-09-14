package com.geeke.stock.entity;

import com.geeke.org.entity.Company;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 药品/材料入库明细控制Entity
 * @author hl
 * @version 2022-09-26
 */
public class MedicinalStorageControl extends DataEntity<MedicinalStorageControl> {

	private static final long serialVersionUID = 1139486400240443448L;
	private Company company;      // 诊所ID 
	private String storageId;		// 入库明细ID
	private String drugStuffId;		// 药品/材料ID
	private String drugStuffName;		// 药品/材料名称
	private Integer type;		// 类型（1：药品；2：材料）
	private BigDecimal storageStock;		// 入库总量
	private BigDecimal usedStock;		// 已使用库存数
	private BigDecimal occupyStock;		// 当前占用库存数
	private BigDecimal surplusStock;		// 剩余可用库存数
	private BigDecimal reimburseStock;		// 报损库存数
	private BigDecimal returnTodoFeeStock;		// 退药未退费量
	private Drug drug;
	private Stuff stuff;
	private InventoryVerification inventoryVerification;
	private String inventoryVerificationDetailId;       //库存盘点详情id
	private SupplierStock supplierStock;

	
	public MedicinalStorageControl() {
		super();
	}

	public MedicinalStorageControl(String id){
		super(id);
	}
	

	@NotNull(message="诊所ID不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=1, max=20, message="入库明细ID长度必须介于 1 和 20 之间")
	public String getStorageId() {
		return storageId;
	}
	public void setStorageId(String storageId) {
		this.storageId = storageId;
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
	
	
	@NotNull(message="入库总量不能为空")
	public BigDecimal getStorageStock() {
		return storageStock;
	}
	public void setStorageStock(BigDecimal storageStock) {
		this.storageStock = storageStock;
	}
	
	
	@NotNull(message="已使用库存数不能为空")
	public BigDecimal getUsedStock() {
		return usedStock;
	}
	public void setUsedStock(BigDecimal usedStock) {
		this.usedStock = usedStock;
	}
	
	
	@NotNull(message="当前占用库存数不能为空")
	public BigDecimal getOccupyStock() {
		return occupyStock;
	}
	public void setOccupyStock(BigDecimal occupyStock) {
		this.occupyStock = occupyStock;
	}
	
	
	@NotNull(message="剩余可用库存数不能为空")
	public BigDecimal getSurplusStock() {
		return surplusStock;
	}
	public void setSurplusStock(BigDecimal surplusStock) {
		this.surplusStock = surplusStock;
	}
	
	
	@NotNull(message="报损库存数不能为空")
	public BigDecimal getReimburseStock() {
		return reimburseStock;
	}
	public void setReimburseStock(BigDecimal reimburseStock) {
		this.reimburseStock = reimburseStock;
	}

	public BigDecimal getReturnTodoFeeStock() {
		return returnTodoFeeStock;
	}

	public void setReturnTodoFeeStock(BigDecimal returnTodoFeeStock) {
		this.returnTodoFeeStock = returnTodoFeeStock;
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


	public InventoryVerification getInventoryVerification() {
		return inventoryVerification;
	}

	public void setInventoryVerification(InventoryVerification inventoryVerification) {
		this.inventoryVerification = inventoryVerification;
	}

	public String getInventoryVerificationDetailId() {
		return inventoryVerificationDetailId;
	}

	public void setInventoryVerificationDetailId(String inventoryVerificationDetailId) {
		this.inventoryVerificationDetailId = inventoryVerificationDetailId;
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
        return "medicinal_storage_control";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1139486400240443448";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}