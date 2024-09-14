package com.geeke.stock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
/**
 * 材料信息Entity
 * @author txl
 * @version 2022-06-22
 */
public class Stuff extends DataEntity<Stuff> {

	private static final long serialVersionUID = 1004462867645374480L;
    private Company company;      // 诊所id
    private String syncId;     //同步过来的ID
    private int syncNum;    //药品是否已同步
	private String commonName;		// 常用名
	private String code;		// 材料编码
	private DictItem type;      // 材料类型 
	private ManufactureFactory factory;      // 生产厂家 
	private DictItem nature;      // 性质 
	private String barCode;		// 条形码
	private String registrationName;		// 注册证名称
	private String registrationCode;		// 注册证号
	private String specifications;		// 规格
	private Integer packNumber;		// 包装数量
	private DictItem minUnit;      // 最小单位 
	private DictItem packUnit;      // 包装单位 
	private String isOutSell;		// 允许对外销售
	private BigDecimal priceOutSell;		// 对外销售价格
	private String isUnpackSell;		// 允许拆零销售
	private BigDecimal retailPrice;		// 拆开后零售价
	private String status;		// 启用标志
	private String pinyinCode;		// 拼音码
	private String stockNumber;     //库存
	private int inventory;  //库存
	private String locationNumber;   //货位号
	private String inventoryLimit;   //库存上限
	private String inventoryFloor;   //库存下限

	private InventoryVerification inventoryVerification;  //库存盘点
	private String inventoryVerificationDetailId;       //库存盘点详情id
	private BigDecimal bid;      //成本合计
	private String indate;        //有效期
	private MedicinalStockControl stock;


	public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}
	public Stuff() {
		super();
	}

	public Stuff(String id){
		super(id);
	}

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public int getSyncNum() {
        return syncNum;
    }

    public void setSyncNum(int syncNum) {
        this.syncNum = syncNum;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=0, max=45, message="常用名长度必须介于 0 和 45 之间")
	public String getCommonName() {
		return commonName;
	}
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	
	
	@Length(min=0, max=45, message="材料编码长度必须介于 0 和 45 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
    public DictItem getType() {
        return type;
    }

    public void setType(DictItem type) {
        this.type = type;
    }
    
    public ManufactureFactory getFactory() {
        return factory;
    }

    public void setFactory(ManufactureFactory factory) {
        this.factory = factory;
    }
    
    public DictItem getNature() {
        return nature;
    }

    public void setNature(DictItem nature) {
        this.nature = nature;
    }
    
	@Length(min=0, max=64, message="条形码长度必须介于 0 和 64 之间")
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	
	
	@Length(min=0, max=64, message="注册证名称长度必须介于 0 和 64 之间")
	public String getRegistrationName() {
		return registrationName;
	}
	public void setRegistrationName(String registrationName) {
		this.registrationName = registrationName;
	}
	
	
	@Length(min=0, max=64, message="注册证号长度必须介于 0 和 64 之间")
	public String getRegistrationCode() {
		return registrationCode;
	}
	public void setRegistrationCode(String registrationCode) {
		this.registrationCode = registrationCode;
	}
	
	
	@Length(min=0, max=45, message="规格长度必须介于 0 和 45 之间")
	public String getSpecifications() {
		return specifications;
	}
	public void setSpecifications(String specifications) {
		this.specifications = specifications;
	}
	
	
	@NotNull(message="包装数量不能为空")
	public Integer getPackNumber() {
		return packNumber;
	}
	public void setPackNumber(Integer packNumber) {
		this.packNumber = packNumber;
	}
	
	
	@NotNull(message="最小单位不能为空")
    public DictItem getMinUnit() {
        return minUnit;
    }

    public void setMinUnit(DictItem minUnit) {
        this.minUnit = minUnit;
    }
    
	@NotNull(message="包装单位不能为空")
    public DictItem getPackUnit() {
        return packUnit;
    }

    public void setPackUnit(DictItem packUnit) {
        this.packUnit = packUnit;
    }
    
	@Length(min=0, max=2, message="允许对外销售长度必须介于 0 和 2 之间")
	public String getIsOutSell() {
		return isOutSell;
	}
	public void setIsOutSell(String isOutSell) {
		this.isOutSell = isOutSell;
	}
	
	
	public BigDecimal getPriceOutSell() {
		return priceOutSell;
	}
	public void setPriceOutSell(BigDecimal priceOutSell) {
		this.priceOutSell = priceOutSell;
	}
	
	
	@Length(min=0, max=2, message="允许拆零销售长度必须介于 0 和 2 之间")
	public String getIsUnpackSell() {
		return isUnpackSell;
	}
	public void setIsUnpackSell(String isUnpackSell) {
		this.isUnpackSell = isUnpackSell;
	}
	
	
	public BigDecimal getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(BigDecimal retailPrice) {
		this.retailPrice = retailPrice;
	}


	@Length(min=1, max=1, message="启用标志长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}


	@Length(min=0, max=100, message="拼音码长度必须介于 0 和 100 之间")
	public String getPinyinCode() {
		return pinyinCode;
	}
	public void setPinyinCode(String pinyinCode) {
		this.pinyinCode = pinyinCode;
	}

	public String getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(String stockNumber) {
		this.stockNumber = stockNumber;
	}

	public String getLocationNumber() {
		return locationNumber;
	}

	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}

	public String getInventoryLimit() {
		return inventoryLimit;
	}

	public void setInventoryLimit(String inventoryLimit) {
		this.inventoryLimit = inventoryLimit;
	}

	public String getInventoryFloor() {
		return inventoryFloor;
	}

	public void setInventoryFloor(String inventoryFloor) {
		this.inventoryFloor = inventoryFloor;
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

	public BigDecimal getBid() {
		return bid;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public String getIndate() {
		return indate;
	}

	public void setIndate(String indate) {
		this.indate = indate;
	}

	public MedicinalStockControl getStock() {
		return stock;
	}

	public void setStock(MedicinalStockControl stock) {
		this.stock = stock;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "stuff";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1004462867645374480";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}